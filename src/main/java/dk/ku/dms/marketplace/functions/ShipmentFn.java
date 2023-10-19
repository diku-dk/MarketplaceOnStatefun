package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.entities.Shipment;
import dk.ku.dms.marketplace.entities.Package;
import dk.ku.dms.marketplace.entities.TransactionMark;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.order.ShipmentNotification;
import dk.ku.dms.marketplace.messages.shipment.PaymentConfirmed;
import dk.ku.dms.marketplace.messages.shipment.ShipmentMessages;
import dk.ku.dms.marketplace.states.ShipmentState;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ShipmentFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/shipment");

    public static final ValueSpec<Integer> NEXT_SHIPMENT_ID_STATE = ValueSpec.named("nextShipmentId").withIntType();
    static final ValueSpec<ShipmentState> SHIPMENT_STATE = ValueSpec.named("shipmentState").withCustomType(ShipmentState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(SHIPMENT_STATE)
            .withSupplier(ShipmentFn::new)
            .build();

//    private static Connection conn;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    static {
//        try {
//            conn = PostgreHelper.getConnection();
//            PostgreHelper.initLogTable(conn);
//            System.out.println("Connection established for ShipmentFn ...............");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            if (message.is(ShipmentMessages.PAYMENT_CONFIRMED_TYPE)) {
                onProcessShipment(context, message);
            }
//            else if (message.is(GetPendingPackages.TYPE)) {
//                onGetPendingPackages(context, message);
//            } else if (message.is(UpdateShipment.TYPE)) {
//                onUpdateShipment(context, message);
//            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return context.done();
    }

    private int generateNextShipmentId(Context context) {
        int nextId = context.storage().get(NEXT_SHIPMENT_ID_STATE).orElse(0) + 1;
        context.storage().set(NEXT_SHIPMENT_ID_STATE, nextId);
        return nextId;
    }


    private ShipmentState getShipmentState(Context context) {
        return context.storage().get(SHIPMENT_STATE).orElse(new ShipmentState());
    }

    private void onProcessShipment(Context context, Message message) {
        PaymentConfirmed paymentConfirmed = message.as(ShipmentMessages.PAYMENT_CONFIRMED_TYPE);
        LocalDateTime now = LocalDateTime.now();

        int shipmentId = generateNextShipmentId(context);
        Shipment shipment = new Shipment(
                shipmentId,
                paymentConfirmed.getOrderId(),
                paymentConfirmed.getCustomerCheckout().getCustomerId(),
                paymentConfirmed.getItems().size(),
                (float)  paymentConfirmed.getItems().stream().mapToDouble(OrderItem::getFreightValue).sum(),
                now,
                Enums.ShipmentStatus.APPROVED,
                paymentConfirmed.getCustomerCheckout().getFirstName(),
                paymentConfirmed.getCustomerCheckout().getLastName(),
                paymentConfirmed.getCustomerCheckout().getStreet(),
                paymentConfirmed.getCustomerCheckout().getZipCode(),
                paymentConfirmed.getCustomerCheckout().getCity(),
                paymentConfirmed.getCustomerCheckout().getState()
        );

        int packageId = 1;
        List<Package> packages = new ArrayList<>();
        for (OrderItem orderItem : paymentConfirmed.getItems()) {
            Package pkg = new Package(
                    shipmentId,
                    paymentConfirmed.getOrderId(),
                    packageId,
                    orderItem.getSellerId(),
                    orderItem.getProductId(),
                    orderItem.getQuantity(),
                    orderItem.getFreightValue(),
                    orderItem.getProductName(),
                    now,
                    Enums.PackageStatus.SHIPPED
            );
            packages.add(pkg);
            packageId++;
        }

        ShipmentState shipmentState = getShipmentState(context);
        shipmentState.getShipments().put(shipmentId, shipment);
        shipmentState.getPackages().put(shipmentId, packages);
        context.storage().set(SHIPMENT_STATE, shipmentState);

        ShipmentNotification shipmentNotification = new ShipmentNotification(
                paymentConfirmed.getOrderId(), paymentConfirmed.getCustomerCheckout().getCustomerId(),
                Enums.ShipmentStatus.APPROVED, now
                );

        Message shipmentOrderMsg =
                MessageBuilder.forAddress(OrderFn.TYPE, context.self().id())
                        .withCustomType(OrderMessages.SHIPMENT_NOTIFICATION_TYPE,
                                shipmentNotification)
                        .build();
        context.send(shipmentOrderMsg);

//        List<OrderItem> orderItems = invoice.getItems();
//        List<Integer> sellerIds = new ArrayList<>();
//        for (OrderItem orderItem : orderItems) {
//            if (!sellerIds.contains(orderItem.getSellerId())) {
//                sellerIds.add(orderItem.getSellerId());
//                Utils.sendMessage(
//                        context,
//                        SellerFn.TYPE,
//                        String.valueOf(orderItem.getSellerId()),
//                        ShipmentNotification.TYPE,
//                        new ShipmentNotification(
//                                invoice.getOrderID(),
//                                invoice.getCustomerCheckout().getCustomerId(),
//                                Enums.ShipmentStatus.APPROVED,
//                                now
//                        )
//                );
//            }
//        }

        TransactionMark mark = new TransactionMark(paymentConfirmed.getInstanceId(),
                Enums.TransactionType.CUSTOMER_SESSION, paymentConfirmed.getCustomerCheckout().getCustomerId(),
                Enums.MarkStatus.SUCCESS, "shipment");

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                        .build();

        context.send(egressMessage);

    }

//
//    private void onGetPendingPackages(Context context, Message message) {
//        int sellerId = message.as(GetPendingPackages.TYPE).getSellerID();
//        ShipmentState shipmentState = getShipmentState(context);
//        Map<Integer, List<Package>> packages = shipmentState.getPackages();
//
//        List<Package> pendingPackages =
//                packages.values().stream()
//                .flatMap(List::stream)
//                .filter(p -> p.getPackageStatus().equals(Enums.PackageStatus.SHIPPED) && p.getSellerId() == sellerId)
//                .collect(Collectors.toList());
//
//
//        String pendingPackagesStr = pendingPackages.stream()
//                .map(Package::toString)
//                .collect(Collectors.joining("\n"));
//
//        String log = getPartionText(context.self().id()) + "PendingPackages: \n" + pendingPackagesStr + "\n";
////        logger.info(log);
//    }
//
//    /**
//     * get the oldest (OPEN) shipment per seller
//     * select seller id, min(shipment id)
//     * from packages
//     * where packages.status == shipped
//     * group by seller id
//     *
//     */
//    // TODO: 6/30/2023 we only need update the oldest? or should we update by seller id
//    // 每次更新整个shipment，shipment包含多个package
//    private void onUpdateShipment(Context context, Message message) throws SQLException, JsonProcessingException {
//
//        ShipmentState shipmentState = getShipmentState(context);
//        Map<Integer, List<Package>> packages = shipmentState.getPackages();
//
//        String log = getPartionText(context.self().id()) + "UpdateShipment in, packages have : " + packages + "\n";
////        showLog(log);
//
//        // contains the minimum shipment ID for each seller.
//        // 对应 每个卖家（sellerId）对应的最小发货单号（shipmentId）
//        Map<Integer, Integer> q = shipmentState.GetOldestOpenShipmentPerSeller();
//
//        for (Map.Entry<Integer, Integer> kv : q.entrySet()) {
//            // 获取相应的包裹列表
//            List<Package> packagesForSeller = shipmentState.GetShippedPackagesByShipmentIDAndSeller(kv.getKey(), kv.getValue());
//            updatePackageDelivery(context, packagesForSeller, kv.getKey());
//        }
//
////        String log_ = getPartionText(context.self().id())
////                + "Update Shipment finished\n";
////        showLog(log_);
//
//        context.storage().set(SHIPMENT_STATE, shipmentState);
//
//        UpdateShipment updateShipment = message.as(UpdateShipment.TYPE);
//        // send ack to caller (proxy)
//        Utils.sendMessageToCaller(context, UpdateShipment.TYPE, updateShipment);
//    }
//
//    private void updatePackageDelivery(Context context, List<Package> packageToUpdate, int sellerID) throws SQLException, JsonProcessingException {
//        ShipmentState shipmentState = getShipmentState(context);
//        Map<Integer, Shipment> shipments = shipmentState.getShipments();
//        Map<Integer, List<Package>> packages = shipmentState.getPackages();
//        int shipmentId = packageToUpdate.get(0).getShipmentId();
//        int sellerId = packageToUpdate.get(0).getSellerId();
//
//        Shipment shipment = shipments.get(shipmentId);
//        LocalDateTime now = LocalDateTime.now();
//
//        if (shipment.getStatus() == Enums.ShipmentStatus.APPROVED) {
//            shipment.setStatus(Enums.ShipmentStatus.DELIVERY_IN_PROGRESS);
//            // 更新shipments
//            ShipmentNotification shipmentNotification = new ShipmentNotification(
//                    shipment.getOrderId(),
//                    shipment.getCustomerId(),
//                    shipment.getStatus(),
//                    now
//            );
//
//            Utils.sendMessage(
//                    context, OrderFn.TYPE, String.valueOf(shipment.getOrderPartition()),
//                    ShipmentNotification.TYPE, shipmentNotification
//            );
//
//            Utils.sendMessage(
//                    context, SellerFn.TYPE, String.valueOf(sellerID),
//                    ShipmentNotification.TYPE, shipmentNotification
//            );
//        }
//
//        int countDelivered = shipmentState.GetTotalDeliveredPackagesForShipment(shipmentId);
//
////        String log = getPartionText(context.self().id())
////                + " -- Count delivery for shipment id " + shipmentId
////                + ": " + countDelivered + " total of " + shipments.get(shipmentId).getPackageCnt() + "\n";
////        System.out.println(log);
//
//        for (Package p : packageToUpdate) {
//            p.setPackageStatus(Enums.PackageStatus.DELIVERED);
//            p.setDelivered_time(now);
//
//            DeliveryNotification deliveryNotification = new DeliveryNotification(
//                    shipment.getCustomerId(),
//                    p.getOrderId(),
//                    p.getPackageId(),
//                    p.getSellerId(),
//                    p.getProductId(),
//                    p.getProductName(),
//                    Enums.PackageStatus.DELIVERED,
//                    now
//            );
//
//            Utils.sendMessage(
//                    context, SellerFn.TYPE, String.valueOf(sellerID),
//                    DeliveryNotification.TYPE, deliveryNotification
//            );
//
//            // notify customer
//            Utils.sendMessage(
//                    context, CustomerFn.TYPE, String.valueOf(shipment.getCustomerId()),
//                    DeliveryNotification.TYPE, deliveryNotification
//            );
//        }
//
//        if (shipment.getPackageCnt() == countDelivered + packageToUpdate.size()) {
//            shipment.setStatus(Enums.ShipmentStatus.CONCLUDED);
//            // save in onUpdateShipment function
//
//            ShipmentNotification shipmentNotification = new ShipmentNotification(
//                    shipment.getOrderId(),
//                    shipment.getCustomerId(),
//                    shipment.getStatus(),
//                    now
//            );
//
//            Utils.sendMessage(
//                    context, OrderFn.TYPE, String.valueOf(shipment.getOrderPartition()),
//                    ShipmentNotification.TYPE, shipmentNotification
//            );
//
//            Utils.sendMessage(
//                    context, SellerFn.TYPE, String.valueOf(sellerID),
//                    ShipmentNotification.TYPE, shipmentNotification
//            );
//
//            //  todo
//
//            String type = "ShipmentFn";
//            String id_ = String.valueOf(shipment.getShipmentId());
//            String orderJson = objectMapper.writeValueAsString(shipment);
//
//            Statement st = conn.createStatement();
//            String sql = String.format("INSERT INTO public.log (\"type\",\"key\",\"value\") VALUES ('%s', '%s', '%s')", type, id_, orderJson);
//            st.execute(sql);
//
//            shipments.remove(shipmentId);
//            packages.remove(shipmentId);
//
//        }
//    }
}

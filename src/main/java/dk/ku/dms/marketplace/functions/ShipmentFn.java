package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.common.Entity.*;
import dk.ku.dms.marketplace.common.Utils.PostgreHelper;
import dk.ku.dms.marketplace.common.Utils.Utils;
import dk.ku.dms.marketplace.constants.Enums;
import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.types.State.ShipmentState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.sdk.java.*;
import dk.ku.dms.marketplace.types.MsgToOrderFn.ShipmentNotification;
import dk.ku.dms.marketplace.types.MsgToSeller.DeliveryNotification;
import dk.ku.dms.marketplace.types.MsgToShipment.GetPendingPackages;
import dk.ku.dms.marketplace.types.MsgToShipment.ProcessShipment;
import dk.ku.dms.marketplace.types.MsgToShipment.UpdateShipment;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ShipmentFn implements StatefulFunction {

    Logger logger = Logger.getLogger("ShipmentFn");

    static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNS_NAMESPACE, "shipment");

    static final ValueSpec<Integer> SHIPMENTIDSTATE = ValueSpec.named("shipmentIdState").withIntType();
    static final ValueSpec<ShipmentState> SHIPMENT_STATE = ValueSpec.named("shipmentState").withCustomType(ShipmentState.TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpecs(SHIPMENTIDSTATE, SHIPMENT_STATE)
            .withSupplier(ShipmentFn::new)
            .build();

    private static Connection conn;

    private final ObjectMapper objectMapper = new ObjectMapper();

    static {
        try {
            conn = PostgreHelper.getConnection();
            PostgreHelper.initLogTable(conn);
            System.out.println("Connection established for ShipmentFn ...............");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            if (message.is(ProcessShipment.TYPE)) {
                onProcessShipment(context, message);
            } else if (message.is(GetPendingPackages.TYPE)) {
                onGetPendingPackages(context, message);
            } else if (message.is(UpdateShipment.TYPE)) {
                onUpdateShipment(context, message);
            }
        } catch (Exception e) {
            System.out.println("ShipmentFn Exception !!!!!!!!!!!!!!!!");
        }
        return context.done();
    }

    private int generateNextShipmentId(Context context) {
        int shipmentId = context.storage().get(SHIPMENTIDSTATE).orElse(0) + 1;
        context.storage().set(SHIPMENTIDSTATE, shipmentId);
        return shipmentId;
    }

    private String getPartionText(String id) {
        return String.format("[ ShipmentFn partitionId %s ] ", id);
    }

    private ShipmentState getShipmentState(Context context) {
        return context.storage().get(SHIPMENT_STATE).orElse(new ShipmentState());
    }

    private void showLog(String log) {
        logger.info(log);
//        System.out.println(log);
    }

    private void showLogPrt(String log) {
//        logger.info(log);
        System.out.println(log);
    }

    private void onProcessShipment(Context context, Message message) {
        ProcessShipment processShipment = message.as(ProcessShipment.TYPE);
        Invoice invoice = processShipment.getInvoice();
        CustomerCheckout customerCheckout = invoice.getCustomerCheckout();
        int customerId = customerCheckout.getCustomerId();
        int transactionID = processShipment.getInstanceId();
        LocalDateTime now = LocalDateTime.now();
//        String log = getPartionText(context.self().id()) + "ShipmentFn: onProcessShipment: " + processShipment.toString();
//        logger.info(log);

        // The Java equivalent code
        List<OrderItem> invoiceItems = invoice.getItems();
        List<OrderItem> items = invoiceItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getSellerId))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, List<OrderItem>>comparingByValue(Comparator.comparingInt(List::size)).reversed())
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());

        int shipmentId = generateNextShipmentId(context);
        Shipment shipment = new Shipment(
                shipmentId,
                invoice.getOrderID(),
                customerCheckout.getCustomerId(),
                items.size(),
                (float) items.stream().mapToDouble(OrderItem::getFreightValue).sum(),
                now,
                Enums.ShipmentStatus.APPROVED,
                customerCheckout.getFirstName(),
                customerCheckout.getLastName(),
                customerCheckout.getStreet(),
                Integer.parseInt(invoice.getOrderPartitionID()),

                customerCheckout.getZipCode(),
                customerCheckout.getCity(),
                customerCheckout.getState()
        );

        int packageId = 1;
//        List<OrderItem> orderItems = invoice.getItems();
        List<PackageItem> packages = new ArrayList<>();
        for (OrderItem orderItem : items) {
            PackageItem pkg = new PackageItem(
                    shipmentId,
                    invoice.getOrderID(),
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
        shipmentState.addShipment(shipmentId, shipment);
        shipmentState.addPackage(shipmentId, packages);

        context.storage().set(SHIPMENT_STATE, shipmentState);

        /**
         * Based on olist (https://dev.olist.com/docs/orders), the status of the order is
         * shipped when "at least one order item has been shipped"
         * All items are considered shipped here, so just signal the order about that
         */

        String orderPartition = invoice.getOrderPartitionID();

        Utils.sendMessage(
                context,
                OrderFn.TYPE,
                String.valueOf(orderPartition),
                ShipmentNotification.TYPE,
                new ShipmentNotification(
                        invoice.getOrderID(),
                        invoice.getCustomerCheckout().getCustomerId(),
                        Enums.ShipmentStatus.APPROVED,
                        now
                )
        );

        List<OrderItem> orderItems = invoice.getItems();
        List<Integer> sellerIds = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            if (!sellerIds.contains(orderItem.getSellerId())) {
                sellerIds.add(orderItem.getSellerId());
                Utils.sendMessage(
                        context,
                        SellerFn.TYPE,
                        String.valueOf(orderItem.getSellerId()),
                        ShipmentNotification.TYPE,
                        new ShipmentNotification(
                                invoice.getOrderID(),
                                invoice.getCustomerCheckout().getCustomerId(),
                                Enums.ShipmentStatus.APPROVED,
                                now
                        )
                );
            }
        }

        Utils.notifyTransactionComplete(context,
                Enums.TransactionType.checkoutTask.toString(),
                String.valueOf(customerId),
                customerId,
                transactionID,
                String.valueOf(customerId),
                Enums.MarkStatus.SUCCESS,
                "shipment");

        String log2 = getPartionText(context.self().id())
                + "checkout success, " + "tid : " + transactionID + "\n";
        printLog(log2);
//        logger.info("[success] {tid=" + transactionID + "} checkout (success), shipmentFn " + context.self().id());
    }

    private void printLog(String log) {
        System.out.println(log);
    }

    private void onGetPendingPackages(Context context, Message message) {
        int sellerId = message.as(GetPendingPackages.TYPE).getSellerID();
        ShipmentState shipmentState = getShipmentState(context);
        Map<Integer, List<PackageItem>> packages = shipmentState.getPackages();

        List<PackageItem> pendingPackages =
                packages.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getPackageStatus().equals(Enums.PackageStatus.SHIPPED) && p.getSellerId() == sellerId)
                .collect(Collectors.toList());


        String pendingPackagesStr = pendingPackages.stream()
                .map(PackageItem::toString)
                .collect(Collectors.joining("\n"));

        String log = getPartionText(context.self().id()) + "PendingPackages: \n" + pendingPackagesStr + "\n";
//        logger.info(log);
    }

    /**
     * get the oldest (OPEN) shipment per seller
     * select seller id, min(shipment id)
     * from packages
     * where packages.status == shipped
     * group by seller id
     * 
     */
    // TODO: 6/30/2023 we only need update the oldest? or should we update by seller id
    // 每次更新整个shipment，shipment包含多个package
    private void onUpdateShipment(Context context, Message message) throws SQLException, JsonProcessingException {

        ShipmentState shipmentState = getShipmentState(context);
        Map<Integer, List<PackageItem>> packages = shipmentState.getPackages();

        String log = getPartionText(context.self().id()) + "UpdateShipment in, packages have : " + packages + "\n";
//        showLog(log);

        // contains the minimum shipment ID for each seller.
        // 对应 每个卖家（sellerId）对应的最小发货单号（shipmentId）
        Map<Integer, Integer> q = shipmentState.GetOldestOpenShipmentPerSeller();

        for (Map.Entry<Integer, Integer> kv : q.entrySet()) {
            // 获取相应的包裹列表
            List<PackageItem> packagesForSeller = shipmentState.GetShippedPackagesByShipmentIDAndSeller(kv.getKey(), kv.getValue());
            updatePackageDelivery(context, packagesForSeller, kv.getKey());
        }

//        String log_ = getPartionText(context.self().id())
//                + "Update Shipment finished\n";
//        showLog(log_);

        context.storage().set(SHIPMENT_STATE, shipmentState);

        UpdateShipment updateShipment = message.as(UpdateShipment.TYPE);
        // send ack to caller (proxy)
        Utils.sendMessageToCaller(context, UpdateShipment.TYPE, updateShipment);
    }

    private void updatePackageDelivery(Context context, List<PackageItem> packageToUpdate, int sellerID) throws SQLException, JsonProcessingException {
        ShipmentState shipmentState = getShipmentState(context);
        Map<Integer, Shipment> shipments = shipmentState.getShipments();
        Map<Integer, List<PackageItem>> packages = shipmentState.getPackages();
        int shipmentId = packageToUpdate.get(0).getShipmentId();
        int sellerId = packageToUpdate.get(0).getSellerId();

        Shipment shipment = shipments.get(shipmentId);
        LocalDateTime now = LocalDateTime.now();

        if (shipment.getStatus() == Enums.ShipmentStatus.APPROVED) {
            shipment.setStatus(Enums.ShipmentStatus.DELIVERY_IN_PROGRESS);
            // 更新shipments
            ShipmentNotification shipmentNotification = new ShipmentNotification(
                    shipment.getOrderId(),
                    shipment.getCustomerId(),
                    shipment.getStatus(),
                    now
            );

            Utils.sendMessage(
                    context, OrderFn.TYPE, String.valueOf(shipment.getOrderPartition()),
                    ShipmentNotification.TYPE, shipmentNotification
            );

            Utils.sendMessage(
                    context, SellerFn.TYPE, String.valueOf(sellerID),
                    ShipmentNotification.TYPE, shipmentNotification
            );
        }

        int countDelivered = shipmentState.GetTotalDeliveredPackagesForShipment(shipmentId);

//        String log = getPartionText(context.self().id())
//                + " -- Count delivery for shipment id " + shipmentId
//                + ": " + countDelivered + " total of " + shipments.get(shipmentId).getPackageCnt() + "\n";
//        System.out.println(log);

        for (PackageItem p : packageToUpdate) {
            p.setPackageStatus(Enums.PackageStatus.DELIVERED);
            p.setDelivered_time(now);

            DeliveryNotification deliveryNotification = new DeliveryNotification(
                    shipment.getCustomerId(),
                    p.getOrderId(),
                    p.getPackageId(),
                    p.getSellerId(),
                    p.getProductId(),
                    p.getProductName(),
                    Enums.PackageStatus.DELIVERED,
                    now
            );

            Utils.sendMessage(
                    context, SellerFn.TYPE, String.valueOf(sellerID),
                    DeliveryNotification.TYPE, deliveryNotification
            );

            // notify customer
            Utils.sendMessage(
                    context, CustomerFn.TYPE, String.valueOf(shipment.getCustomerId()),
                    DeliveryNotification.TYPE, deliveryNotification
            );
        }

        if (shipment.getPackageCnt() == countDelivered + packageToUpdate.size()) {
            shipment.setStatus(Enums.ShipmentStatus.CONCLUDED);
            // save in onUpdateShipment function

            ShipmentNotification shipmentNotification = new ShipmentNotification(
                    shipment.getOrderId(),
                    shipment.getCustomerId(),
                    shipment.getStatus(),
                    now
            );

            Utils.sendMessage(
                    context, OrderFn.TYPE, String.valueOf(shipment.getOrderPartition()),
                    ShipmentNotification.TYPE, shipmentNotification
            );

            Utils.sendMessage(
                    context, SellerFn.TYPE, String.valueOf(sellerID),
                    ShipmentNotification.TYPE, shipmentNotification
            );

            //  todo

            String type = "ShipmentFn";
            String id_ = String.valueOf(shipment.getShipmentId());
            String orderJson = objectMapper.writeValueAsString(shipment);

            Statement st = conn.createStatement();
            String sql = String.format("INSERT INTO public.log (\"type\",\"key\",\"value\") VALUES ('%s', '%s', '%s')", type, id_, orderJson);
            st.execute(sql);

            shipments.remove(shipmentId);
            packages.remove(shipmentId);

        }
    }
}

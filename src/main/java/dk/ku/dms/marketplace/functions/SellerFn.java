package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.entities.OrderEntry;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.entities.Seller;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import dk.ku.dms.marketplace.messages.seller.SellerMessages;
import dk.ku.dms.marketplace.states.SellerState;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dk.ku.dms.marketplace.states.SellerState.SELLER_STATE_TYPE;
import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class SellerFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(SellerFn.class);

    static final TypeName TYPE = TypeName.typeNameFromString("marketplace/seller");

    public static final Type<Seller> ENTITY_STATE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "SellerEntityState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Seller.class));

    static final ValueSpec<Seller> SELLER_ENTITY_STATE = ValueSpec.named("SellerEntityState").withCustomType(ENTITY_STATE_TYPE);
    static final ValueSpec<SellerState> SELLER_STATE = ValueSpec.named("SellerState").withCustomType(SELLER_STATE_TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpecs(SELLER_ENTITY_STATE, SELLER_STATE)
            .withSupplier(SellerFn::new)
            .build();
//
//    private static Connection conn;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    static {
//        try {
//            conn = PostgreHelper.getConnection();
//            PostgreHelper.initLogTable(conn);
//            System.out.println("Connection established for SellerFn ...............");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // client ---> seller (init seller type)
            if (message.is(SellerMessages.SET_SELLER_TYPE)) {
                onSetSeller(context, message);
            }
            // order ---> seller
            else if (message.is(PaymentMessages.INVOICE_ISSUED_TYPE)) {
                onInvoiceIssued(context, message);
            }
//            // client ---> seller (increase stock)
//            else if (message.is(IncreaseStock.TYPE)) {
//                onIncrStockAsyncBegin(context, message);
//            }
//            // driver ----> query dashboard
//            else if (message.is(QueryDashboard.TYPE)) {
//                onQueryDashboard(context, message);
//            }
//            else if (message.is(PaymentNotification.TYPE)) {
//                ProcessPaymentResult(context, message);
//            }
//            else if (message.is(ShipmentNotification.TYPE)) {
//                ProcessShipmentNotification(context, message);
//            }
//            else if (message.is(DeliveryNotification.TYPE)) {
//                ProcessDeliveryNotification(context, message);
//            }
        } catch (Exception e) {
            System.out.println("SellerFn apply error !!!!!!!!!!!!!!!");
            e.printStackTrace();
        }

        return context.done();
    }

    private SellerState getSellerState(Context context) {
        return context.storage().get(SELLER_STATE).orElse(new SellerState());
    }

    private void onSetSeller(Context context, Message message) {
        Seller seller = message.as(SellerMessages.SET_SELLER_TYPE);
        context.storage().set(SELLER_ENTITY_STATE, seller);
    }

    private static String buildUniqueOrderIdentifier(InvoiceIssued invoiceIssued)
    {
        return new StringBuilder(invoiceIssued.getCustomerCheckout().getCustomerId()).append('-').append(invoiceIssued.getOrderId()).toString();
    }

    private void onInvoiceIssued(Context context, Message message) {

        SellerState sellerState = getSellerState(context);
        InvoiceIssued invoiceIssued = message.as(PaymentMessages.INVOICE_ISSUED_TYPE);

        List<OrderItem> orderItems = invoiceIssued.getItems();
        int sellerId = Integer.parseInt(context.self().id());

        List<OrderEntry> list = new ArrayList<>(invoiceIssued.getItems().size());
        sellerState.getOrderEntries().put( buildUniqueOrderIdentifier(invoiceIssued), list);

        for (OrderItem orderItem : orderItems) {
            OrderEntry orderEntry = new OrderEntry(
                    invoiceIssued.getOrderId(),
                    sellerId,
                    // packageI =
                    orderItem.getProductId(),
                    orderItem.getProductName(),
                    orderItem.getQuantity(),
                    orderItem.getTotalAmount(),
                    orderItem.getTotalPrice(),
                    // total_invoice =
                    // total_incentive =
                    orderItem.getFreightValue(),
                    // shipment_date
                    // delivery_date
                    orderItem.getUnitPrice(),
                    Enums.OrderStatus.INVOICED
                    // product_category = ? should come from product
            );
            list.add(orderEntry);
        }

        context.storage().set(SELLER_STATE, sellerState);
    }

//    private void onQueryDashboard(Context context, Message message) {
//        SellerState sellerState = getSellerState(context);
//
//        Set<OrderEntry> orderEntries = sellerState.getOrderEntries();
////        Map<Integer, OrderEntryDetails> orderEntryDetails = sellerState.getOrderEntryDetails();
//
//        int sellerID = sellerState.getSeller().getId();
//        int tid = message.as(QueryDashboard.TYPE).getTid();
////        logger.info("[receive] {tid=" + tid + "} query dashboard, sellerFn " + context.self().id());
//        String log = getPartionText(context.self().id())
//                + "query dashboard [receive], " + "tid : " + tid + "\n";
//        printLog(log);
//        // count_items, total_amount, total_freight, total_incentive, total_invoice, total_items
//        OrderSellerView orderSellerView = new OrderSellerView(
//                sellerID,
//                orderEntries.size(),
//                (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalAmount).sum(),
//                (float) orderEntries.stream().mapToDouble(OrderEntry::getFreight_value).sum(),
//                (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalIncentive).sum(),
//                (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalInvoice).sum(),
//                (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalItems).sum()
//        );
//
//        SellerDashboard sellerDashboard = new SellerDashboard(
//                orderSellerView,
//                orderEntries
//        );
//
//        Utils.notifyTransactionComplete(
//                context,
//                Enums.TransactionType.QUERY_DASHBOARD.toString(),
//                context.self().id(),
//                sellerID, tid, context.self().id(), Enums.MarkStatus.SUCCESS, "seller");
//
//        String log_ = getPartionText(context.self().id())
//                + "query dashboard success, " + "tid : " + tid + "\n";
//        printLog(log_);
////        logger.info("[success] {tid=" + tid + "} query dashboard, sellerFn " + context.self().id());
//    }
//
//    private void ProcessPaymentResult(Context context, Message message) throws SQLException, JsonProcessingException {
//        SellerState sellerState = getSellerState(context);
//        PaymentNotification orderStateUpdate = message.as(PaymentNotification.TYPE);
//
//        int orderId = orderStateUpdate.getOrderId();
////        if (orderId != sellerState.getSeller().getId()) {
////            throw new RuntimeException("sellerId != orderId");
////        }
//
//        Enums.OrderStatus orderStatus = orderStateUpdate.getOrderStatus();
//        updateOrderStatus(sellerState.getOrderEntries(), orderId, orderStatus, null);
//
//        context.storage().set(SELLERSTATE, sellerState);
//    }
//
//    private void ProcessShipmentNotification(Context context, Message message) throws SQLException, JsonProcessingException {
//        SellerState sellerState = getSellerState(context);
//        ShipmentNotification shipmentNotification = message.as(ShipmentNotification.TYPE);
//        Enums.OrderStatus orderStatus = null;
//        if (shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.APPROVED) {
//            orderStatus = Enums.OrderStatus.READY_FOR_SHIPMENT;
//        } else if (shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.DELIVERY_IN_PROGRESS) {
//            orderStatus = Enums.OrderStatus.IN_TRANSIT;
//        } else if (shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.CONCLUDED) {
//            orderStatus = Enums.OrderStatus.DELIVERED;
//        }
//
//        updateOrderStatus(sellerState.getOrderEntries(), shipmentNotification.getOrderId(), orderStatus, shipmentNotification.getEventDate());
//
//        context.storage().set(SELLERSTATE, sellerState);
////        UpdateOrderStatus(context, orderId, status, eventTime);
//    }
//
//    /**
//     * Process individual (i.e., each package at a time) delivery notifications
//     */
//    private void ProcessDeliveryNotification(Context context, Message message) {
//        SellerState sellerState = getSellerState(context);
//        DeliveryNotification deliveryNotification = message.as(DeliveryNotification.TYPE);
//
//        Set<OrderEntry> orderEntries = sellerState.getOrderEntries();
//        for (OrderEntry orderEntry : orderEntries) {
//            if (orderEntry.getOrder_id() == deliveryNotification.getOrderId()
//                    && orderEntry.getProduct_id() == deliveryNotification.getProductID())
//            {
//                orderEntry.setDelivery_status(deliveryNotification.getPackageStatus());
//                orderEntry.setDelivery_date(deliveryNotification.getEventDate());
//                orderEntry.setPackage_id(deliveryNotification.getPackageId());
//            }
//        }
//
//        context.storage().set(SELLERSTATE, sellerState);
//    }
//
//    public void updateOrderStatus(Set<OrderEntry> orderEntries, int orderEntryId, Enums.OrderStatus orderStatus, LocalDateTime updateTime) throws JsonProcessingException, SQLException {
//        // 更新orderEntries,如果更新后的不属于only for INVOICED / PAYMENT_PORCESSED / READY_FOR_SHIPMENT / IN_TRANSIT，
//        // 则将其移动到orderEntriesHistory
//        for (OrderEntry orderEntry : orderEntries) {
//            if (orderEntry.getOrder_id() == orderEntryId) {
////                this.orderEntries.remove(orderEntry);
//                orderEntry.setOrder_status(orderStatus);
//
//                if (orderStatus == Enums.OrderStatus.IN_TRANSIT) {
//                    orderEntry.setShipment_date(updateTime);
//                }
//
//                if (orderStatus == Enums.OrderStatus.DELIVERED) {
//                    orderEntries.remove(orderEntry);
//
//                    String type = "SellerFn";
//                    String id_ = String.valueOf(orderEntry.getOrder_id());
//                    String orderJson = objectMapper.writeValueAsString(orderEntry);
//
//                    Statement st = conn.createStatement();
//                    String sql = String.format("INSERT INTO public.log (\"type\",\"key\",\"value\") VALUES ('%s', '%s', '%s')", type, id_, orderJson);
//                    st.execute(sql);
//                }
//                break;
//            }
//        }
//
//    }
}

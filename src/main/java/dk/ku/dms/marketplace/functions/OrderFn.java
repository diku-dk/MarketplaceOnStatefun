package dk.ku.dms.marketplace.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.messages.order.AttemptReservationResponse;
import dk.ku.dms.marketplace.messages.order.CheckoutRequest;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import dk.ku.dms.marketplace.messages.stock.AttemptReservationEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.states.OrderState;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.StatefulFunction;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.ValueSpec;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrderFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(OrderFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/order");

    // generate unique Identifier
    public static final ValueSpec<Integer> NEXT_ORDER_ID_STATE = ValueSpec.named("nextOrderId").withIntType();
    // static final ValueSpec<Integer> ORDERHISTORYIDSTATE = ValueSpec.named("orderHistoryId").withIntType();
    // store checkout info
//    static final ValueSpec<CustomerCheckoutInfoState> TEMPCKINFOSTATE = ValueSpec.named("tempCKInfoState").withCustomType(CustomerCheckoutInfoState.TYPE);
//    // tmp store async task state
//    static final ValueSpec<ReserveStockTaskState> ASYNCTASKSTATE = ValueSpec.named("asyncTaskState").withCustomType(ReserveStockTaskState.TYPE);
//
//    // store order info
    public static final ValueSpec<OrderState> ORDER_STATE = ValueSpec.named("order").withCustomType(OrderState.TYPE);

//    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
//            .withValueSpecs(ORDERIDSTATE, ASYNCTASKSTATE, TEMPCKINFOSTATE, ORDERSTATE, ORDERHISTORYIDSTATE)
//            .withSupplier(OrderFn::new)
//            .build();

    private static Connection conn;

    private final ObjectMapper objectMapper = new ObjectMapper();

//    static {
//        try {
//            conn = PostgreHelper.getConnection();
//            PostgreHelper.initLogTable(conn);
//            System.out.println("Connection established for OrderFn ...............");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // cart --> order, (checkout request)
            if (message.is(OrderMessages.CHECKOUT_REQUEST_TYPE)) {
                ReserveStockAsync(context, message);
            }
            // stock --> order, (checkout response)
            else if (message.is(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE)) {
                ReserveStockResult(context, message);
            }
//            // xxxx ---> order (update order status)
//            else if (message.is(PaymentNotification.TYPE)) {
//                PaymentNotification info = message.as(PaymentNotification.TYPE);
//                UpdateOrderStatus(context, info.getOrderId(), info.getOrderStatus());
//            }
//            else if (message.is(ShipmentNotification.TYPE))
//            {
//                ProcessShipmentNotification(context, message);
//            }
//            else if (message.is(Cleanup.TYPE))
//            {
//                onCleanup(context);
//            }
        } catch (Exception e) {
            LOG.error("OrderFn error: !!!!!!!!!!!!" + e.getMessage());
        }
        return context.done();
    }

//    ===============================================================================
//                                  helper functions
//    ===============================================================================

//    private ReserveStockTaskState getAtptResvTaskState(Context context) {
//        return context.storage().get(ASYNCTASKSTATE).orElse(new ReserveStockTaskState());
//    }
//
//    private CustomerCheckoutInfoState getTempCKInfoState(Context context) {
//        return context.storage().get(TEMPCKINFOSTATE).orElse(new CustomerCheckoutInfoState());
//    }
//
    private OrderState getOrderState(Context context) {
        return context.storage().get(ORDER_STATE).orElse(new OrderState());
    }
//
    private int generateNextOrderID(Context context) {
        int nextId = context.storage().get(NEXT_ORDER_ID_STATE).orElse(0) + 1;
        context.storage().set(NEXT_ORDER_ID_STATE, nextId);
        // different partitionId may have same orderId, so we add partitionId number at beginning
        return nextId;
    }
//
//    private int generateNextOrderHistoryID(Context context) {
//        int nextId = context.storage().get(ORDERHISTORYIDSTATE).orElse(0) + 1;
//        context.storage().set(ORDERHISTORYIDSTATE, nextId);
//        return nextId;
//    }



//    ====================================================================================
//    Attemp/Confirm/Cance  Reservation (two steps business logic)【send message to stock】
//    ====================================================================================

    private void ReserveStockAsync(Context context, Message message) {

        CheckoutRequest checkoutRequest = message.as(OrderMessages.CHECKOUT_REQUEST_TYPE);

        int orderId = generateNextOrderID(context);

        OrderState orderState = getOrderState(context);

        orderState.getCheckouts().put(orderId, checkoutRequest);

        // send message to stock
        for (CartItem item : checkoutRequest.getItems()) {
            AttemptReservationEvent attemptReservationEvent = new AttemptReservationEvent(orderId, item);
            Message attemptReservationMsg =
                    MessageBuilder.forAddress(StockFn.TYPE, String.valueOf(item.getSellerId())+'/'+item.getProductId())
                            .withCustomType(StockMessages.ATTEMPT_RESERVATION_TYPE, attemptReservationEvent)
                            .build();
            context.send(attemptReservationMsg);
        }

        context.storage().set(ORDER_STATE, orderState);

    }

//    ====================================================================================
//            handle attempt reservation response 【receive message from stock】
//    ====================================================================================
    private void ReserveStockResult(Context context, Message message) {

        AttemptReservationResponse resp = message.as(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE);

        if(resp.getStatus() != Enums.ItemStatus.IN_STOCK){
            return;
        }

        int orderId = resp.getOrderId();

        OrderState orderState = getOrderState(context);

        CheckoutRequest checkoutRequest = orderState.getCheckouts().get(orderId);

        // CartItem item = checkoutRequest.getItems().stream().filter(p-> p.getSellerId() == resp.getSellerId() && p.getProductId() == resp.getProductId()).findFirst().get();
        int idx = 0;
        CartItem item = null;
        for(CartItem p : checkoutRequest.getItems()){
            if(p.getSellerId() == resp.getSellerId() && p.getProductId() == resp.getProductId()){
                item = p;
                break;
            }
            idx++;
        }

        if(item == null) {
            LOG.error("Cart item not found!");
            return;
        }
        checkoutRequest.getItems().remove(idx);

        List<OrderItem> orderItems = orderState.getOrderItems().computeIfAbsent(orderId, k -> new ArrayList<>());

        float totalPrice = item.getUnitPrice() * item.getQuantity();
        OrderItem orderItem = new OrderItem(orderId, orderItems.size() + 1, item.getProductId(), item.getProductName(), item.getSellerId(), item.getUnitPrice(), item.getFreightValue(), item.getQuantity(), totalPrice,
                totalPrice - item.getVoucher(), LocalDateTime.now().plusDays(1) );

        orderItems.add(orderItem);

        if(checkoutRequest.getItems().size() == 0){
            generateOrder(context, checkoutRequest, orderState, orderId);
        }

        context.storage().set(ORDER_STATE, orderState);
    }

//    =================================================================================
//    After we handle the confirmation or cancellation of an order with stockFn,
//    Handing over to paymentFn for processing.
//    =================================================================================

    private void generateOrder(Context context, CheckoutRequest checkoutRequest, OrderState orderState, int orderId) {

        List<OrderItem> items =  orderState.getOrderItems().get(orderId);

        // TODO build order
        // Order order = orderState.getOrders().get(orderId);

        InvoiceIssued invoiceIssued = new InvoiceIssued(
                checkoutRequest.getCustomerCheckout(),
                orderId,
                // order.getInvoiceNumber(),
                "",
                items,
                // order.getTotalInvoice(),
                0,
                LocalDateTime.now(),
                checkoutRequest.getInstanceId() );

        Message invoiceIssuedMsg =
                MessageBuilder.forAddress(StockFn.TYPE, context.self().id())
                        .withCustomType(PaymentMessages.INVOICE_ISSUED_TYPE, invoiceIssued)
                        .build();

        context.send(invoiceIssuedMsg);

//        int orderId = generateNextOrderID(context);
//        Map<Integer, CartItem> items = successCheckout.getItems();
//
//        // calculate total freight_value
//        float total_freight_value = 0;
//        for (Map.Entry<Integer, CartItem> entry : items.entrySet()) {
//            CartItem item = entry.getValue();
//            total_freight_value += item.getFreightValue();
//        }
//
//        //  calculate total amount
//        float total_amount = 0;
//
//        for (Map.Entry<Integer, CartItem> entry : items.entrySet()) {
//            CartItem item = entry.getValue();
//            float amount = item.getUnitPrice() * item.getQuantity();
//            total_amount = total_amount + amount;
//        }
//
//        // total before discounts
//        float total_items = total_amount;
////
//        // apply vouchers per product, but only until total >= 0 for each item
//        Map<Integer, Float> totalPerItem = new HashMap<>();
//        float total_incentive = 0;
//        for (Map.Entry<Integer, CartItem> entry : items.entrySet()) {
//            CartItem item = entry.getValue();
//
//            float total_item = item.getUnitPrice() * item.getQuantity();
//            float vouchers = item.getVoucher();
//
//            if (total_item - vouchers > 0) {
//                total_amount = total_amount - vouchers;
//                total_incentive = total_incentive + vouchers;
//                total_item = total_item - vouchers;
//            } else {
//                total_amount = total_amount - total_item;
//                total_incentive = total_incentive + total_item;
//                total_item = 0;
//            }
//            totalPerItem.put(entry.getKey(), total_item);
//        }
//
//        // get state
//        OrderState orderState = getOrderState(context);
//
//        int customerId = successCheckout.getCustomerCheckout().getCustomerId();
//        LocalDateTime now = LocalDateTime.now();
//
//        StringBuilder invoiceNumber = new StringBuilder();
//        invoiceNumber.append(customerId)
//                .append("_").append(now.toString()).append("d")
//                .append("-").append(orderState.generateCustomerNextOrderID(customerId));
//
//        // add order and orderHistory to orderState
//        Order successOrder = new Order(orderId, customerId, Enums.OrderStatus.INVOICED, invoiceNumber.toString(), );
//        successOrder.setId(orderId);
//        successOrder.setCustomerId(customerId);
////      invoice is a request for payment, so it makes sense to use this status now
//        successOrder.setStatus(Enums.OrderStatus.INVOICED);
//        successOrder.setInvoiceNumber(invoiceNumber.toString());
//        successOrder.setPurchaseTimestamp(successCheckout.getCreatedAt());
//        successOrder.setCreated_at(now);
//        successOrder.setUpdated_at(now);
//        successOrder.setData(successCheckout.toString());
//        successOrder.setTotalAmount(total_amount);
//        successOrder.setTotalFreight(total_freight_value);
//        successOrder.setTotalItems(total_items);
//        successOrder.setTotalIncentive(total_incentive);
//        successOrder.setCountItems(successCheckout.getItems().size());
//
//        // add order
//        orderState.addOrder(orderId, successOrder);
//
//        // add history
//        OrderHistory orderHistory = new OrderHistory(
//                orderId,
//                now,
//                Enums.OrderStatus.INVOICED);
//        orderState.addOrderHistory(orderId, orderHistory);
//
//        // add orderItems and create invoice
//        List<OrderItem> invoiceItems = new ArrayList<>();
//        int order_item_id = 0;
//        for (Map.Entry<Integer, CartItem> entry : items.entrySet()) {
//
//            CartItem item = entry.getValue();
//            OrderItem oim = new OrderItem(
//                orderId,
//                order_item_id,
//                item.getProductId(),
//                item.getProductName(),
//                item.getSellerId(),
//                item.getUnitPrice(),
//                item.getFreightValue(),
//                item.getQuantity(),
//                item.getQuantity() * item.getUnitPrice(),
//                totalPerItem.get(entry.getKey()),
//                LocalDateTime.now().plusDays(3)
//            );
//
//            orderState.addOrderItem(oim);
//
//            // vouchers so payment can process
//            oim.setVouchers(item.getVoucher());
//            invoiceItems.add(oim);
//
//            order_item_id++;
//        }
//
//        context.storage().set(ORDERSTATE, orderState);
//
//
//        CustomerCheckout customerCheckout = successCheckout.getCustomerCheckout();
//        int orderID = successOrder.getId();
//        String invoiceNumber_ = invoiceNumber.toString();
//        String orderPartitionID = context.self().id();
//
//        Invoice invoice = new Invoice(
//                customerCheckout,
//                orderID,
//                invoiceNumber_,
//                invoiceItems,
//                successOrder.getTotalInvoice(),
//                now,
//                orderPartitionID
//        );
//
//        // send message to paymentFn to pay the invoice
////        int paymentPation = orderId % Constants.nPaymentPartitions;
//        int paymentPation = customerId;
//        Utils.sendMessage(context,
//                PaymentFn.TYPE,
//                String.valueOf(paymentPation),
//                InvoiceIssued.TYPE,
//                new InvoiceIssued(invoice, successCheckout.getCustomerCheckout().getInstanceId()));
//
//        // send sellerInvoices to each seller
//
//        Map<Integer, Invoice> sellerInvoices = new HashMap<>();
//
//        for (OrderItem item : invoiceItems) {
//            int sellerId = item.getSellerId();
//            if (!sellerInvoices.containsKey(sellerId)) {
//                Invoice sellerInvoice = new Invoice(
//                        customerCheckout,
//                        orderID,
//                        invoiceNumber_,
//                        new ArrayList<>(),
//                        0,
//                        now,
//                        orderPartitionID
//                );
//                sellerInvoices.put(sellerId, sellerInvoice);
//            }
//            sellerInvoices.get(sellerId).getItems().add(item);
//        }
//
//        for (Map.Entry<Integer, Invoice> entry : sellerInvoices.entrySet()) {
//            int sellerId = entry.getKey();
//            Invoice sellerInvoice = entry.getValue();
//            int sellerPartition = sellerId;
//            Utils.sendMessage(context,
//                    SellerFn.TYPE,
//                    String.valueOf(sellerPartition),
//                    InvoiceIssued.TYPE,
//                    new InvoiceIssued(sellerInvoice, successCheckout.getCustomerCheckout().getInstanceId()));
//        }
    }
//
//    private void ProcessShipmentNotification(Context context, Message message) throws SQLException, JsonProcessingException {
//
//        OrderState orderState = getOrderState(context);
//        Map<Integer, Order> orders = orderState.getOrders();
//        TreeMap<Integer, List<OrderHistory>> orderHistories = orderState.getOrderHistory();
//
//        ShipmentNotification shipmentNotification = message.as(ShipmentNotification.TYPE);
//        int orderId = shipmentNotification.getOrderId();
//
//        if (!orders.containsKey(orderId)) {
//            String str = new StringBuilder().append("Order ").append(orderId)
//                    .append(" cannot be found to update to status ").toString();
//            throw new RuntimeException(str);
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//
//        Enums.OrderStatus status = Enums.OrderStatus.READY_FOR_SHIPMENT;
//        if(shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.DELIVERY_IN_PROGRESS) {
//            status = Enums.OrderStatus.IN_TRANSIT;
//        } else if(shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.CONCLUDED) {
//            status = Enums.OrderStatus.DELIVERED;
//        }
//
//        // add history
//        OrderHistory orderHistory = new OrderHistory(
//                orderId,
//                now,
//                status);
//        orderState.addOrderHistory(orderId, orderHistory);
//
//        orders.get(orderId).setUpdated_at(now);
//        orders.get(orderId).setStatus(status);
//
//        if (status == Enums.OrderStatus.DELIVERED) {
//            orders.get(orderId).setDelivered_customer_date(shipmentNotification.getEventDate());
//
//            Order order = orders.get(orderId);
//            // log delivered entries and remove them from state
//
//            String type = "OrderFn";
//            String id_ = String.valueOf(order.getId());
//            String orderJson = objectMapper.writeValueAsString(order);
//
//            Statement st = conn.createStatement();
//            String sql = String.format("INSERT INTO public.log (\"type\",\"key\",\"value\") VALUES ('%s', '%s', '%s')", type, id_, orderJson);
//            st.execute(sql);
//
//            orders.remove(orderId);
//        }
//
//        context.storage().set(ORDERSTATE, orderState);
////        UpdateOrderStatus(context, orderId, status, eventTime);
//    }
//
//    private void UpdateOrderStatus(Context context, int orderId, Enums.OrderStatus status) {
//        OrderState orderState = getOrderState(context);
//        Map<Integer, Order> orders = orderState.getOrders();
//        TreeMap<Integer, List<OrderHistory>> orderHistories = orderState.getOrderHistory();
//
//        if (!orders.containsKey(orderId)) {
//            String str = new StringBuilder().append("Order ").append(orderId)
//                    .append(" cannot be found to update to status ").append(status.toString()).toString();
//            throw new RuntimeException(str);
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//
//        orders.get(orderId).setUpdated_at(now);
//        Enums.OrderStatus oldStatus = orders.get(orderId).getStatus();
//        orders.get(orderId).setStatus(status);
//
//        switch (status) {
//            case SHIPPED:
//                orders.get(orderId).setDelivered_carrier_date(now);
//                break;
//            case DELIVERED:
//                orders.get(orderId).setDelivered_customer_date(now);
//                break;
////            case CANCLED:
//            case PAYMENT_FAILED:
//            case PAYMENT_PROCESSED:
//                orders.get(orderId).setPaymentDate(now);
//                break;
//            default:
//                break;
//        }
//
//        context.storage().set(ORDERSTATE, orderState);
//
//        String log = getPartionText(context.self().id())
//                + "update order status, orderId: " + orderId + ", oldStatus: " + oldStatus + ", newStatus: " + status + "\n";
////        showLog(log);
//    }
}
package dk.ku.dms.marketplace.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.Order;
import dk.ku.dms.marketplace.entities.OrderHistory;
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
import dk.ku.dms.marketplace.utils.Enums.OrderStatus;
import dk.ku.dms.marketplace.utils.Utils;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.groupingBy;

public final class OrderFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(OrderFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/order");

    public static final ValueSpec<Integer> NEXT_ORDER_ID_STATE = ValueSpec.named("nextOrderId").withIntType();

    public static final ValueSpec<OrderState> ORDER_STATE = ValueSpec.named("order").withCustomType(OrderState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpecs(NEXT_ORDER_ID_STATE, ORDER_STATE)
            .withSupplier(OrderFn::new)
            .build();

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
                onCheckoutRequest(context, message);
            }
            // stock --> order, (checkout response)
            else if (message.is(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE)) {
                onReserveAttemptResponse(context, message);
            }
//            // xxxx ---> order (update order status)
//            else if (message.is(PaymentNotification.TYPE)) {
//                PaymentNotification info = message.as(PaymentNotification.TYPE);
//                UpdateOrderStatus(context, info.getOrderId(), info.getOrderStatus());
//            }
            else if (message.is(OrderMessages.SHIPMENT_NOTIFICATION_TYPE)) {
                onShipmentNotification(context, message);
            }
        } catch (Exception e) {
            LOG.error("OrderFn error: !!!!!!!!!!!!" + e.getMessage());
        }
        return context.done();
    }

    private OrderState getOrderState(Context context) {
        return context.storage().get(ORDER_STATE).orElse(new OrderState());
    }
//
    private int generateNextOrderId(Context context) {
        int nextId = context.storage().get(NEXT_ORDER_ID_STATE).orElse(0) + 1;
        context.storage().set(NEXT_ORDER_ID_STATE, nextId);
        // different partitionId may have same orderId, so we add partitionId number at beginning
        return nextId;
    }

//    ====================================================================================
//    Attemp/Confirm/Cance  Reservation (two steps business logic)【send message to stock】
//    ====================================================================================

    private void onCheckoutRequest(Context context, Message message) {

        CheckoutRequest checkoutRequest = message.as(OrderMessages.CHECKOUT_REQUEST_TYPE);

        int orderId = generateNextOrderId(context);

        OrderState orderState = getOrderState(context);

        orderState.getCheckouts().put(orderId, checkoutRequest);

        orderState.setUpRemainingAcks(orderId, checkoutRequest.getItems().size());

        // send message to stock
        int idx = 0;
        for (CartItem item : checkoutRequest.getItems()) {
            AttemptReservationEvent attemptReservationEvent = new AttemptReservationEvent(orderId, item, idx);
            Message attemptReservationMsg =
                    MessageBuilder.forAddress(StockFn.TYPE, String.valueOf(item.getSellerId())+'/'+item.getProductId())
                            .withCustomType(StockMessages.ATTEMPT_RESERVATION_TYPE, attemptReservationEvent)
                            .build();
            context.send(attemptReservationMsg);
            idx++;
        }

        context.storage().set(ORDER_STATE, orderState);

    }

//    ====================================================================================
//            handle attempt reservation response 【receive message from stock】
//    ====================================================================================
    private void onReserveAttemptResponse(Context context, Message message) {

        AttemptReservationResponse resp = message.as(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE);
        int orderId = resp.getOrderId();
        OrderState orderState = getOrderState(context);
        CheckoutRequest checkoutRequest = orderState.getCheckouts().get(orderId);

        if(resp.getStatus() == Enums.ItemStatus.IN_STOCK) {

            CartItem item = checkoutRequest.getItems().get(resp.getIdx());

            List<OrderItem> orderItems = orderState.getOrderItems().computeIfAbsent(orderId, k -> new ArrayList<>());

            float totalPrice = item.getUnitPrice() * item.getQuantity();
            OrderItem orderItem = new OrderItem(orderId, orderItems.size() + 1, item.getProductId(), item.getProductName(), item.getSellerId(), item.getUnitPrice(), item.getFreightValue(), item.getQuantity(), totalPrice,
                    totalPrice - item.getVoucher(), LocalDateTime.now().plusDays(1));

            orderItems.add(orderItem);
        }

        // all acks have been received
        if(orderState.decreaseRemainingItems(orderId) == 0){
            orderState.setDownRemainingAcks(orderId);

            // only proceed if at least one item has been reserved
            if(orderState.getOrderItems().containsKey(orderId)) {
                generateOrder(context, checkoutRequest, orderState, orderId);
            } else {
                // otherwise clean state for this order id
                orderState.cleanState(orderId);
            }
        }

        context.storage().set(ORDER_STATE, orderState);
    }

//    =================================================================================
//    After we handle the confirmation or cancellation of an order with stockFn,
//    Handing over to paymentFn for processing.
//    =================================================================================

    private void generateOrder(Context context, CheckoutRequest checkoutRequest, OrderState orderState, int orderId) {

    	LocalDateTime now = LocalDateTime.now();
    	
        List<OrderItem> itemsToCheckout = orderState.getOrderItems().get(orderId);
        
        float total_freight = 0;
        float total_amount = 0;
        for (OrderItem item : itemsToCheckout) {
        	total_freight += item.getFreightValue();
        	total_amount += item.getUnitPrice() * item.getQuantity();
        }
        
        float total_items = total_amount;
        
        Map<Map.Entry<Integer, Integer>, Float> totalPerItem = new HashMap<>();
        float total_incentive = 0;
        for (OrderItem item : itemsToCheckout) {
        	float total_item = item.getUnitPrice() * item.getQuantity();
        	
        	if (total_item - item.getVoucher() > 0)
        	{
        	    total_amount -= item.getVoucher();
        	    total_incentive += item.getVoucher();
        	    total_item -= item.getVoucher();
        	}
        	else
        	{
        	    total_amount -= total_item;
        	    total_incentive += total_item;
        	    total_item = 0;
        	}

        	totalPerItem.put(new AbstractMap.SimpleImmutableEntry<>(item.getSellerId(), item.getProductId()), total_item);
        }
        
        int customerId = checkoutRequest.getCustomerCheckout().getCustomerId();
        
        String invoiceNumber = Utils.GetInvoiceNumber(customerId, now, orderId);
        
        Order order = new Order(
            orderId, 
            customerId, 
            OrderStatus.INVOICED,
            invoiceNumber, 
            checkoutRequest.getTimestamp(),
            now, 
            now, 
            null, 
            null,
            null, 
            itemsToCheckout.size(), 
            total_amount, 
            total_freight, 
            total_incentive, 
            total_amount + total_freight, 
            total_items, 
            null);
        
        List<OrderItem> items = new ArrayList<>();
        
        int id = 1;
        for (OrderItem item : itemsToCheckout)
        {
            items.add(new OrderItem
            (
                orderId,
                id,
                item.getProductId(),
                item.getProductName(),
                item.getSellerId(),
                item.getUnitPrice(),
                item.getFreightValue(),
                item.getQuantity(),
                item.getUnitPrice() * item.getQuantity(),
                totalPerItem.get(new AbstractMap.SimpleImmutableEntry<>(item.getSellerId(), item.getProductId())),
                now.plusDays(3)
            ));
            id++;
        }
        
        OrderHistory orderHistory = new OrderHistory(orderId, now, OrderStatus.INVOICED);
        
        orderState.addOrder(orderId, order, items, orderHistory);
        
        Map<Integer, List<OrderItem>> itemsPerSeller = items.stream().collect(groupingBy(OrderItem::getSellerId));

        // send a message to each related seller (stock function)
        for (Map.Entry<Integer, List<OrderItem>> entry : itemsPerSeller.entrySet())
        {
        	InvoiceIssued invoiceCustom = new InvoiceIssued(
        			checkoutRequest.getCustomerCheckout(),
        			orderId,
        			invoiceNumber,
        			entry.getValue(),
        			order.getTotalInvoice(),
        			now,
        			checkoutRequest.getInstanceId());
        	
        	Message invoiceIssuedMsg =
                    MessageBuilder.forAddress(SellerFn.TYPE, String.valueOf(entry.getKey()))
                            .withCustomType(PaymentMessages.INVOICE_ISSUED_TYPE, invoiceCustom)
                            .build();

            context.send(invoiceIssuedMsg);
        }
        
        // send message to payment function
        InvoiceIssued invoiceIssued = new InvoiceIssued(
                checkoutRequest.getCustomerCheckout(),
                orderId,
                invoiceNumber,
                items,
                order.getTotalInvoice(),
                now,
                checkoutRequest.getInstanceId());
        
        Message invoiceIssuedMsg =
                MessageBuilder.forAddress(PaymentFn.TYPE, context.self().id())
                        .withCustomType(PaymentMessages.INVOICE_ISSUED_TYPE, invoiceIssued)
                        .build();

        context.send(invoiceIssuedMsg);
    }

    private void onShipmentNotification(Context context, Message message) { //throws SQLException, JsonProcessingException {
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
    }
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
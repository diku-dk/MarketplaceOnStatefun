package dk.ku.dms.marketplace.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.Order;
import dk.ku.dms.marketplace.entities.OrderHistory;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.messages.order.*;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import dk.ku.dms.marketplace.messages.stock.AttemptReservationEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.states.OrderState;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import dk.ku.dms.marketplace.utils.Enums.OrderStatus;
import dk.ku.dms.marketplace.utils.PostgresHelper;
import dk.ku.dms.marketplace.utils.Utils;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
            // payment ---> order (update order status)
            else if (message.is(OrderMessages.PAYMENT_NOTIFICATION_TYPE)) {
                onPaymentNotification(context, message);
            }
            else if (message.is(OrderMessages.SHIPMENT_NOTIFICATION_TYPE)) {
                onShipmentNotification(context, message);
            }
            else if (message.is(OrderMessages.GET_ORDERS_TYPE)) {
                onGetOrders(context);
            }
            else {
                LOG.error("Message unknown: "+message);
            }
        } catch (Exception e) {
            LOG.error("OrderFn error: !!!!!!!!!!!!" + e.getMessage());
        }
        return context.done();
    }

    private void onGetOrders(Context context) {
        OrderState orderState = getOrderState(context);

        List<Order> orders = new ArrayList<>(orderState.getOrders().values());

        StringBuilder b = new StringBuilder();
        orders.forEach(b::append);

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, b.toString()))
                        .build();
        context.send(egressMessage);
    }

    private OrderState getOrderState(Context context) {
        return context.storage().get(ORDER_STATE).orElse(OrderState.build());
    }

    private int generateNextOrderId(Context context) {
        int nextId = context.storage().get(NEXT_ORDER_ID_STATE).orElse(0) + 1;
        context.storage().set(NEXT_ORDER_ID_STATE, nextId);
        // different partitionId may have same orderId, so we add partitionId number at beginning
        return nextId;
    }

//    ====================================================================================
//    Attempt/Confirm/Cancel  Reservation (two steps business logic)【send message to stock】
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
            String id = String.valueOf(item.getSellerId())+'-'+item.getProductId();
            Message attemptReservationMsg =
                    MessageBuilder.forAddress(StockFn.TYPE, id)
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
            if(orderState.inStockItems.containsKey( orderId )){
                orderState.inStockItems.get(orderId).add(resp.getIdx());
            } else {
                List<Integer> list = new ArrayList<>();
                orderState.inStockItems.put(orderId, list);
                list.add(resp.getIdx());
            }
        }

        // all acks have been received
        if(orderState.decreaseRemainingItems(orderId) == 0){
            orderState.setDownRemainingAcks(orderId);

            // only proceed if at least one item has been reserved
            if(orderState.inStockItems.containsKey(orderId)) {
                this.generateOrder(context, checkoutRequest, orderState, orderId);

                // storing customer checkout is no longer necessary
                orderState.getCheckouts().remove(orderId);
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

        List<CartItem> itemsToCheckout = new ArrayList<>( orderState.inStockItems.get(orderId).size() );

        for(Integer idx : orderState.inStockItems.get(orderId)){
            itemsToCheckout.add(checkoutRequest.getItems().get(idx));
        }
        
        float total_freight = 0;
        float total_amount = 0;
        for (CartItem item : itemsToCheckout) {
        	total_freight += item.getFreightValue();
        	total_amount += item.getUnitPrice() * item.getQuantity();
        }
        
        float total_items = total_amount;
        
        Map<Map.Entry<Integer, Integer>, Float> totalPerItem = new HashMap<>();
        float total_incentive = 0;
        for (CartItem item : itemsToCheckout) {
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
            null, 
            null,
            null, 
            itemsToCheckout.size(), 
            total_amount, 
            total_freight, 
            total_incentive, 
            total_amount + total_freight, 
            total_items, 
            "");
        
        List<OrderItem> items = new ArrayList<>();
        
        int id = 1;
        for (CartItem item : itemsToCheckout)
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
                item.getVoucher(),
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

        OrderState orderState = getOrderState(context);
        ShipmentNotification shipmentNotification = message.as(OrderMessages.SHIPMENT_NOTIFICATION_TYPE);
        int orderId = shipmentNotification.getOrderId();
        Order order = orderState.getOrders().getOrDefault(orderId, null);

        if (order == null) {
            String str = "Order " + orderId +
                    " cannot be found to update to status in function "+context.self().id()+". Current state size is "+orderState.getOrders().size();
            throw new RuntimeException(str);
        }

        LocalDateTime now = LocalDateTime.now();

        Enums.OrderStatus status = Enums.OrderStatus.READY_FOR_SHIPMENT;
        if(shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.DELIVERY_IN_PROGRESS) {
            status = Enums.OrderStatus.IN_TRANSIT;
        } else if(shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.CONCLUDED) {
            status = Enums.OrderStatus.DELIVERED;
        }

        // add history
        OrderHistory orderHistory = new OrderHistory(
                orderId,
                now,
                status);
        orderState.getOrderHistory().get(orderId).add(orderHistory);

        order.setUpdatedAt(now);
        order.setStatus(status);

        if (status == Enums.OrderStatus.DELIVERED) {
            order.setDeliveredCustomerDate(shipmentNotification.getEventDate());
            
            if (Constants.logging)
            {
            	try
            	{
            		String funcName = "OrderFn";
                	String key = order.getCustomerId() + "-" + order.getId();
            		String value = this.objectMapper.writeValueAsString(order);
            		PostgresHelper.log(funcName, key, value);
            	}
            	catch (Exception e)
            	{
            		System.out.println(e.getMessage() + e.getStackTrace().toString());
            	}
            }
            
            orderState.cleanState(orderId);
        }

        context.storage().set(ORDER_STATE, orderState);
        
    }

    private void onPaymentNotification(Context context, Message message) {
        LocalDateTime now = LocalDateTime.now();
        OrderState orderState = getOrderState(context);
        PaymentNotification paymentNotification = message.as(OrderMessages.PAYMENT_NOTIFICATION_TYPE);
        int orderId = paymentNotification.getOrderId();
        // add history
        OrderHistory orderHistory = new OrderHistory(
                orderId,
                now,
                OrderStatus.PAYMENT_PROCESSED);
        orderState.getOrderHistory().get(orderId).add(orderHistory);
        Order order = orderState.getOrders().get(orderId);
        order.setStatus(OrderStatus.PAYMENT_PROCESSED);
        order.setUpdatedAt(now);
        context.storage().set(ORDER_STATE, orderState);
    }

}
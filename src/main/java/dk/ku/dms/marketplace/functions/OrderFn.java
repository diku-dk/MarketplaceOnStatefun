package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.common.Entity.*;
import dk.ku.dms.marketplace.common.Utils.PostgreHelper;
import dk.ku.dms.marketplace.common.Utils.Utils;
import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.constants.Enums;
import dk.ku.dms.marketplace.Types.MsgToCustomer.NotifyCustomer;
import dk.ku.dms.marketplace.Types.MsgToOrderFn.PaymentNotification;
import dk.ku.dms.marketplace.Types.MsgToOrderFn.ShipmentNotification;
import dk.ku.dms.marketplace.Types.MsgToPaymentFn.InvoiceIssued;
import dk.ku.dms.marketplace.Types.MsgToStock.ReserveStockEvent;
import dk.ku.dms.marketplace.Types.State.ReserveStockTaskState;
import dk.ku.dms.marketplace.Types.State.OrderState;
import dk.ku.dms.marketplace.Types.State.CustomerCheckoutInfoState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.Types;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class OrderFn implements StatefulFunction {
    static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNS_NAMESPACE, "order");

    Logger logger = Logger.getLogger("OrderFn");

    // generate unique Identifier
    static final ValueSpec<Integer> ORDERIDSTATE = ValueSpec.named("orderId").withIntType();
    static final ValueSpec<Integer> ORDERHISTORYIDSTATE = ValueSpec.named("orderHistoryId").withIntType();
    // store checkout info
    static final ValueSpec<CustomerCheckoutInfoState> TEMPCKINFOSTATE = ValueSpec.named("tempCKInfoState").withCustomType(CustomerCheckoutInfoState.TYPE);
    // tmp store async task state
    static final ValueSpec<ReserveStockTaskState> ASYNCTASKSTATE = ValueSpec.named("asyncTaskState").withCustomType(ReserveStockTaskState.TYPE);

    // store order info
    static final ValueSpec<OrderState> ORDERSTATE = ValueSpec.named("orderState").withCustomType(OrderState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpecs(ORDERIDSTATE, ASYNCTASKSTATE, TEMPCKINFOSTATE, ORDERSTATE, ORDERHISTORYIDSTATE)
            .withSupplier(OrderFn::new)
            .build();

    private static Connection conn;

    private final ObjectMapper objectMapper = new ObjectMapper();

    static {
        try {
            conn = PostgreHelper.getConnection();
            PostgreHelper.initLogTable(conn);
            System.out.println("Connection established for OrderFn ...............");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // cart --> order, (checkout request)
            if (message.is(Checkout.TYPE)) {
                ReserveStockAsync(context, message);
            }
            // stock --> order, (checkout response)
            else if (message.is(ReserveStockEvent.TYPE)) {
                ReserveStockResult(context, message);
            }
            // xxxx ---> order (update order status)
            else if (message.is(PaymentNotification.TYPE)) {
                PaymentNotification info = message.as(PaymentNotification.TYPE);
                UpdateOrderStatus(context, info.getOrderId(), info.getOrderStatus());
            }
            else if (message.is(ShipmentNotification.TYPE))
            {
                ProcessShipmentNotification(context, message);
            }
//            else if (message.is(Cleanup.TYPE))
//            {
//                onCleanup(context);
//            }
        } catch (Exception e) {
            System.out.println("OrderFn error: !!!!!!!!!!!!" + e.getMessage());
            e.printStackTrace();
        }
        return context.done();
    }

//    ===============================================================================
//                                  helper functions
//    ===============================================================================

    private ReserveStockTaskState getAtptResvTaskState(Context context) {
        return context.storage().get(ASYNCTASKSTATE).orElse(new ReserveStockTaskState());
    }

    private CustomerCheckoutInfoState getTempCKInfoState(Context context) {
        return context.storage().get(TEMPCKINFOSTATE).orElse(new CustomerCheckoutInfoState());
    }

    private OrderState getOrderState(Context context) {
        return context.storage().get(ORDERSTATE).orElse(new OrderState());
    }

    private int generateNextOrderID(Context context) {
        int nextId = context.storage().get(ORDERIDSTATE).orElse(0) + 1;
        context.storage().set(ORDERIDSTATE, nextId);
        // different partitionId may have same orderId, so we add partitionId number at beginning
        return nextId;
    }

    private int generateNextOrderHistoryID(Context context) {
        int nextId = context.storage().get(ORDERHISTORYIDSTATE).orElse(0) + 1;
        context.storage().set(ORDERHISTORYIDSTATE, nextId);
        return nextId;
    }

    private String getPartionText(String id) {
        return String.format("[ OrderFn partitionId %s ] ", id);
    }

    private void showLog(String log) {
        logger.info(log);
//        System.out.println(log);
    }

    private void showLogPrt(String log) {
//        logger.info(log);
        System.out.println(log);
    }

//    ====================================================================================
//    Attemp/Confirm/Cance  Reservation (two steps business logic)【send message to stock】
//    ====================================================================================

    private void ReserveStockAsync(Context context, Message message) {

        // get state and message
        ReserveStockTaskState resvTask_State = getAtptResvTaskState(context);
        CustomerCheckoutInfoState customerCkoutInfo_State = getTempCKInfoState(context);
        Checkout checkout = message.as(Checkout.TYPE);
        // get fields
        Map<Integer, BasketItem> items = checkout.getItems();
        int customerId = checkout.getCustomerCheckout().getCustomerId();
        int nItems = items.size();

        // change state
        customerCkoutInfo_State.addCheckout(customerId, checkout);
        resvTask_State.addNewTask(customerId, nItems);
        // save state
        context.storage().set(ASYNCTASKSTATE, resvTask_State);
        context.storage().set(TEMPCKINFOSTATE, customerCkoutInfo_State);

        // send message to stock
        for (Map.Entry<Integer, BasketItem> entry : items.entrySet()) {
            int stockPartitionId = (int) (entry.getValue().getProductId());
            Utils.sendMessage(context,
                    StockFn.TYPE,
                    String.valueOf(stockPartitionId),
                    ReserveStockEvent.TYPE,
                    new ReserveStockEvent(
                            customerId,
                            entry.getValue(),
                            Enums.ItemStatus.UNKNOWN));
        }
    }


//    ====================================================================================
//                  handle checkout response 【receive message from stock】
//    ====================================================================================
    private void printLog(String log) {
        System.out.println(log);
    }
    private void ReserveStockResult(Context context, Message message) {

        // get state and message
        ReserveStockEvent reserveStockEvent = message.as(ReserveStockEvent.TYPE);
        ReserveStockTaskState resvTask_State = getAtptResvTaskState(context);

        // get fields
        int customerId = reserveStockEvent.getCustomerId();

        // change state
        resvTask_State.addCompletedSubTask(customerId, reserveStockEvent);

        // when all sub-tasks are completed
        boolean isTaskComplete = resvTask_State.isTaskComplete(customerId);
        if (isTaskComplete) {
            // get state
            CustomerCheckoutInfoState customerCheckoutInfoState = getTempCKInfoState(context);

            // get fields
            Checkout checkout = customerCheckoutInfoState.getSingleCheckout(customerId);
            Map<Integer, BasketItem> itemsSuccessResv = resvTask_State.getSingleSuccessResvItems(customerId);
            Map<Integer, BasketItem> itemsFailedResv = resvTask_State.getSingleFailedResvItems(customerId);;
            Checkout checkoutSuccess = new Checkout(checkout.getCreatedAt(), checkout.getCustomerCheckout(), itemsSuccessResv);
            Checkout checkoutFailed = new Checkout(checkout.getCreatedAt(), checkout.getCustomerCheckout(), itemsFailedResv);

            if (itemsSuccessResv.size() == 0) {
                int tid = checkout.getCustomerCheckout().getInstanceId();
                // all the items are unavailable, send transaction mark to driver, notify customer
                Utils.notifyTransactionComplete(context,
                        Enums.TransactionType.checkoutTask.toString(),
                        String.valueOf(customerId),
                        customerId,
                        tid,
                        String.valueOf(customerId),
                        Enums.MarkStatus.NOT_ACCEPTED,
                        "order");
//                logger.info("[success] {tid=" + tid + "} checkout (fail), orderFn " + context.self().id());
                String log_ = getPartionText(context.self().id())
                        + "checkout fail, " + "tid : " + tid + "\n";
                printLog(log_);

                Utils.sendMessage(context,
                        CustomerFn.TYPE,
                        String.valueOf(customerId),
                        NotifyCustomer.TYPE,
                        new NotifyCustomer(customerId, null, Enums.NotificationType.notify_fail_checkout));
            } else {
                // notify the customer the failed items,
                Utils.sendMessage(context,
                        CustomerFn.TYPE,
                        String.valueOf(customerId),
                        Types.stringType(),
                        checkoutFailed.toString()
                        );

                // generate order accoring to the success items , and send order to paymentFn
                generateOrder(context, checkoutSuccess);
            }

            // change state
            customerCheckoutInfoState.removeSingleCheckout(customerId);
            resvTask_State.removeTask(customerId);

            // save state
            context.storage().set(TEMPCKINFOSTATE, customerCheckoutInfoState);
        }
        // HAVE TO PUT HERE
        context.storage().set(ASYNCTASKSTATE, resvTask_State);
    }

//    =================================================================================
//    After we handle the confirmation or cancellation of an order with stockFn,
//    Handing over to paymentFn for processing.
//    =================================================================================

    private void generateOrder(Context context, Checkout successCheckout) {
        int orderId = generateNextOrderID(context);
        Map<Integer, BasketItem> items = successCheckout.getItems();

        // calculate total freight_value
        float total_freight_value = 0;
        for (Map.Entry<Integer, BasketItem> entry : items.entrySet()) {
            BasketItem item = entry.getValue();
            total_freight_value += item.getFreightValue();
        }

        //  calculate total amount
        float total_amount = 0;

        for (Map.Entry<Integer, BasketItem> entry : items.entrySet()) {
            BasketItem item = entry.getValue();
            float amount = item.getUnitPrice() * item.getQuantity();
            total_amount = total_amount + amount;
        }

        // total before discounts
        float total_items = total_amount;
//
        // apply vouchers per product, but only until total >= 0 for each item
        Map<Integer, Float> totalPerItem = new HashMap<>();
        float total_incentive = 0;
        for (Map.Entry<Integer, BasketItem> entry : items.entrySet()) {
            BasketItem item = entry.getValue();

            float total_item = item.getUnitPrice() * item.getQuantity();
            float vouchers = item.getVouchers();

            if (total_item - vouchers > 0) {
                total_amount = total_amount - vouchers;
                total_incentive = total_incentive + vouchers;
                total_item = total_item - vouchers;
            } else {
                total_amount = total_amount - total_item;
                total_incentive = total_incentive + total_item;
                total_item = 0;
            }
            totalPerItem.put(entry.getKey(), total_item);
        }

        // get state
        OrderState orderState = getOrderState(context);

        int customerId = successCheckout.getCustomerCheckout().getCustomerId();
        LocalDateTime now = LocalDateTime.now();

        StringBuilder invoiceNumber = new StringBuilder();
        invoiceNumber.append(customerId)
                .append("_").append(now.toString()).append("d")
                .append("-").append(orderState.generateCustomerNextOrderID(customerId));

        // add order and orderHistory to orderState
        Order successOrder = new Order();
        successOrder.setId(orderId);
        successOrder.setCustomerId(customerId);
//      invoice is a request for payment, so it makes sense to use this status now
        successOrder.setStatus(Enums.OrderStatus.INVOICED);
        successOrder.setInvoiceNumber(invoiceNumber.toString());
        successOrder.setPurchaseTimestamp(successCheckout.getCreatedAt());
        successOrder.setCreated_at(now);
        successOrder.setUpdated_at(now);
        successOrder.setData(successCheckout.toString());
        successOrder.setTotalAmount(total_amount);
        successOrder.setTotalFreight(total_freight_value);
        successOrder.setTotalItems(total_items);
        successOrder.setTotalIncentive(total_incentive);
        successOrder.setCountItems(successCheckout.getItems().size());

        // add order
        orderState.addOrder(orderId, successOrder);

        // add history
        OrderHistory orderHistory = new OrderHistory(
                orderId,
                now,
                Enums.OrderStatus.INVOICED);
        orderState.addOrderHistory(orderId, orderHistory);

        // add orderItems and create invoice
        List<OrderItem> invoiceItems = new ArrayList<>();
        int order_item_id = 0;
        for (Map.Entry<Integer, BasketItem> entry : items.entrySet()) {

            BasketItem item = entry.getValue();
            OrderItem oim = new OrderItem(
                orderId,
                order_item_id,
                item.getProductId(),
                item.getProductName(),
                item.getSellerId(),
                item.getUnitPrice(),
                item.getFreightValue(),
                item.getQuantity(),
                item.getQuantity() * item.getUnitPrice(),
                totalPerItem.get(entry.getKey()),
                LocalDateTime.now().plusDays(3)
            );

            orderState.addOrderItem(oim);

            // vouchers so payment can process
            oim.setVouchers(item.getVouchers());
            invoiceItems.add(oim);

            order_item_id++;
        }

        context.storage().set(ORDERSTATE, orderState);


        CustomerCheckout customerCheckout = successCheckout.getCustomerCheckout();
        int orderID = successOrder.getId();
        String invoiceNumber_ = invoiceNumber.toString();
        String orderPartitionID = context.self().id();

        Invoice invoice = new Invoice(
                customerCheckout,
                orderID,
                invoiceNumber_,
                invoiceItems,
                successOrder.getTotalInvoice(),
                now,
                orderPartitionID
        );

        // send message to paymentFn to pay the invoice
//        int paymentPation = orderId % Constants.nPaymentPartitions;
        int paymentPation = customerId;
        Utils.sendMessage(context,
                PaymentFn.TYPE,
                String.valueOf(paymentPation),
                InvoiceIssued.TYPE,
                new InvoiceIssued(invoice, successCheckout.getCustomerCheckout().getInstanceId()));

        // send sellerInvoices to each seller

        Map<Integer, Invoice> sellerInvoices = new HashMap<>();

        for (OrderItem item : invoiceItems) {
            int sellerId = item.getSellerId();
            if (!sellerInvoices.containsKey(sellerId)) {
                Invoice sellerInvoice = new Invoice(
                        customerCheckout,
                        orderID,
                        invoiceNumber_,
                        new ArrayList<>(),
                        0,
                        now,
                        orderPartitionID
                );
                sellerInvoices.put(sellerId, sellerInvoice);
            }
            sellerInvoices.get(sellerId).getItems().add(item);
        }

        for (Map.Entry<Integer, Invoice> entry : sellerInvoices.entrySet()) {
            int sellerId = entry.getKey();
            Invoice sellerInvoice = entry.getValue();
            int sellerPartition = sellerId;
            Utils.sendMessage(context,
                    SellerFn.TYPE,
                    String.valueOf(sellerPartition),
                    InvoiceIssued.TYPE,
                    new InvoiceIssued(sellerInvoice, successCheckout.getCustomerCheckout().getInstanceId()));
        }
    }

    private void ProcessShipmentNotification(Context context, Message message) throws SQLException, JsonProcessingException {

        OrderState orderState = getOrderState(context);
        Map<Integer, Order> orders = orderState.getOrders();
        TreeMap<Integer, List<OrderHistory>> orderHistories = orderState.getOrderHistory();

        ShipmentNotification shipmentNotification = message.as(ShipmentNotification.TYPE);
        int orderId = shipmentNotification.getOrderId();

        if (!orders.containsKey(orderId)) {
            String str = new StringBuilder().append("Order ").append(orderId)
                    .append(" cannot be found to update to status ").toString();
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
        orderState.addOrderHistory(orderId, orderHistory);

        orders.get(orderId).setUpdated_at(now);
        orders.get(orderId).setStatus(status);

        if (status == Enums.OrderStatus.DELIVERED) {
            orders.get(orderId).setDelivered_customer_date(shipmentNotification.getEventDate());

            Order order = orders.get(orderId);
            // log delivered entries and remove them from state

            String type = "OrderFn";
            String id_ = String.valueOf(order.getId());
            String orderJson = objectMapper.writeValueAsString(order);

            Statement st = conn.createStatement();
            String sql = String.format("INSERT INTO public.log (\"type\",\"key\",\"value\") VALUES ('%s', '%s', '%s')", type, id_, orderJson);
            st.execute(sql);

            orders.remove(orderId);
        }

        context.storage().set(ORDERSTATE, orderState);
//        UpdateOrderStatus(context, orderId, status, eventTime);
    }

    private void UpdateOrderStatus(Context context, int orderId, Enums.OrderStatus status) {
        OrderState orderState = getOrderState(context);
        Map<Integer, Order> orders = orderState.getOrders();
        TreeMap<Integer, List<OrderHistory>> orderHistories = orderState.getOrderHistory();

        if (!orders.containsKey(orderId)) {
            String str = new StringBuilder().append("Order ").append(orderId)
                    .append(" cannot be found to update to status ").append(status.toString()).toString();
            throw new RuntimeException(str);
        }
        
        LocalDateTime now = LocalDateTime.now();

        orders.get(orderId).setUpdated_at(now);
        Enums.OrderStatus oldStatus = orders.get(orderId).getStatus();
        orders.get(orderId).setStatus(status);

        switch (status) {
            case SHIPPED:
                orders.get(orderId).setDelivered_carrier_date(now);
                break;
            case DELIVERED:
                orders.get(orderId).setDelivered_customer_date(now);
                break;
//            case CANCLED:
            case PAYMENT_FAILED:
            case PAYMENT_PROCESSED:
                orders.get(orderId).setPaymentDate(now);
                break;
            default:
                break;
        }

        context.storage().set(ORDERSTATE, orderState);

        String log = getPartionText(context.self().id())
                + "update order status, orderId: " + orderId + ", oldStatus: " + oldStatus + ", newStatus: " + status + "\n";
//        showLog(log);
    }
}
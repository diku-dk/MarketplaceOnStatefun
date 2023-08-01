package Marketplace.Funs;

import Common.Entity.*;
import Common.Utils.Utils;
import Marketplace.Constant.Constants;
import Marketplace.Constant.Enums;
import Marketplace.Types.MsgToCartFn.Cleanup;
import Marketplace.Types.MsgToCustomer.NotifyCustomer;
import Marketplace.Types.MsgToOrderFn.PaymentNotification;
import Marketplace.Types.MsgToPaymentFn.InvoiceIssued;
import Marketplace.Types.MsgToShipment.ProcessShipment;
import Marketplace.Types.MsgToStock.ConfirmStockEvent;
import Marketplace.Types.State.PaymentState;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class PaymentFn implements StatefulFunction {

    Logger logger = Logger.getLogger("PaymentFn");

    static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNS_NAMESPACE, "payment");

//    static final ValueSpec<PaymentAsyncTaskState> PAYMENT_ASYNC_TASK_STATE =
//            ValueSpec.named("paymentAsyncTaskState")
//                    .withCustomType(PaymentAsyncTaskState.TYPE);
    static final ValueSpec<PaymentState> PAYMENT_STATE = ValueSpec.named("paymentState").withCustomType(PaymentState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withSupplier(PaymentFn::new)
            .withValueSpecs(PAYMENT_STATE)
            .build();

//    PaymentService paymentService = new DummyPaymentServiceService();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // stock --> payment (request to payment)
            if (message.is(InvoiceIssued.TYPE)) {
                onProcessPayment(context, message);
            }
//            else if (message.is(Cleanup.TYPE))
//            {
//                onCleanup(context);
//            }
        } catch (Exception e) {
            System.out.println("PaymentFn Exception !!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
        return context.done();
    }

//    ========================================================================
//                             helper functions
//    ========================================================================

    private PaymentState getPaymentState(Context context) {
        return context.storage().get(PAYMENT_STATE).orElse(new PaymentState());
    }

    private String getPartionText(String id) {
        return String.format("[ PaymentFn partitionId %s ] ", id);
    }

    private void showLog(String log) {
        logger.info(log);
//        System.out.println(log);
    }

    private void showLogPrt(String log) {
//        logger.info(log);
        System.out.println(log);
    }

//    ========================================================================
//                             business logic
//    ========================================================================

    private void onProcessPayment(Context context, Message message) {

        PaymentState paymentState = getPaymentState(context);
//        PaymentAsyncTaskState paymentAsyncTaskState = getPaymentAsyncTaskState(context);
        InvoiceIssued invoiceIssued = message.as(InvoiceIssued.TYPE);

        Invoice invoice = invoiceIssued.getInvoice();
        int transactionID = invoiceIssued.getInstanceId();
        CustomerCheckout customerCheckout = invoice.getCustomerCheckout();

        PaymentRequest paymentRequest = new PaymentRequest(
                invoice.getTotalInvoice(),
                invoice.getInvoiceNumber(),
                customerCheckout.getCardHolderName(),
                customerCheckout.getCardNumber(),
                customerCheckout.getCardSecurityNumber(),
                customerCheckout.getCardExpiration()
        );

        String log = getPartionText(context.self().id())
                + " asking for payment for order: " + invoice.getOrderID();
        showLogPrt(log);

        long orderId = invoice.getOrderID();
        LocalDateTime now = LocalDateTime.now();

        String orderPartition = invoice.getOrderPartitionID();
        String uniqueOrderID = orderPartition + "_" + orderId;

        // ========================================================================

//        Call the asynchronous payment processing method
//        CompletableFuture<Boolean> approved = paymentService.ContactESP(customerCheckout, total);
//        approved.whenComplete(
//                (paymentResult, throwable) -> {
//                    // TODO: 6/12/2023 noFurtherModificationsAllowed
//                }
//        );
        boolean paymentResult = true; // assume always success

        String log_ = getPartionText(context.self().id())
                + "contact ESP finished, paymentResult for order: " + uniqueOrderID + " is " + paymentResult;
        showLogPrt(log_);

        Enums.OrderStatus orderStatus =
                paymentResult ? Enums.OrderStatus.PAYMENT_PROCESSED : Enums.OrderStatus.PAYMENT_FAILED;

        int seq = 1;

        boolean cc = (customerCheckout.getPaymentType().equals(Enums.PaymentType.CREDIT_CARD.toString()));

        // create payment tuple
        if (cc || customerCheckout.getPaymentType().equals(Enums.PaymentType.DEBIT_CARD.toString())) {
           OrderPayment cardPaymentLine = new OrderPayment(
                     uniqueOrderID,
                     seq,
                     customerCheckout.getInstallments(),
                     now,
                     invoice.getTotalInvoice(),
                     Enums.OrderStatus.PAYMENT_PROCESSED.toString()
              );
           paymentState.addOrderPayment(uniqueOrderID, cardPaymentLine);
           seq += 1;
        }

        context.storage().set(PAYMENT_STATE, paymentState);

        // send message to stock
        for (OrderItem item : invoice.getItems()) {
            long partition = (item.getProductId() % Constants.nStockPartitions);
            Utils.sendMessage(
                    context,
                    StockFn.TYPE,
                    String.valueOf(partition),
                    ConfirmStockEvent.TYPE,
                    new ConfirmStockEvent(
                            uniqueOrderID,
                            item.getProductId(),
                            item.getQuantity(),
                            orderStatus)
            );
        }

        long customerId = customerCheckout.getCustomerId();
        List<OrderItem> items = invoice.getItems();
        List<Long> sellerIds = new ArrayList<>();
        for (OrderItem item : items) {
            long sellerId = item.getSellerId();
            if (!sellerIds.contains(sellerId)) {
                sellerIds.add(sellerId);
            }
        }

        if (orderStatus == Enums.OrderStatus.PAYMENT_PROCESSED) {

            long shipmentPartition = orderId % Constants.nShipmentPartitions;

            Utils.sendMessage(context, OrderFn.TYPE, String.valueOf(orderPartition),
                    PaymentNotification.TYPE,
                    new PaymentNotification(orderId, Enums.OrderStatus.PAYMENT_PROCESSED)
            );

            Utils.sendMessage(context, CustomerFn.TYPE, String.valueOf(customerId % Constants.nCustomerPartitions),
                    NotifyCustomer.TYPE,
                    new NotifyCustomer(customerId, null, Enums.NotificationType.notify_success_payment)
            );

            Utils.sendMessage(context, ShipmentFn.TYPE, String.valueOf(shipmentPartition),
                    ProcessShipment.TYPE,
                    new ProcessShipment(invoice, transactionID)
            );

            for (Long sellerId : sellerIds) {
                Utils.sendMessage(context, SellerFn.TYPE, String.valueOf(sellerId),
                        PaymentNotification.TYPE,
                        new PaymentNotification(orderId, Enums.OrderStatus.PAYMENT_PROCESSED)
                );
            }

        } else {
            // payment failed
            Utils.sendMessage(context, CustomerFn.TYPE, String.valueOf(customerId % Constants.nCustomerPartitions),
                    NotifyCustomer.TYPE,
                    new NotifyCustomer(customerId, null, Enums.NotificationType.notify_failed_payment)
            );

            Utils.sendMessage(context, OrderFn.TYPE, String.valueOf(orderPartition),
                    PaymentNotification.TYPE,
                    new PaymentNotification(orderId, Enums.OrderStatus.PAYMENT_FAILED)
            );

            for (Long sellerId : sellerIds) {
                Utils.sendMessage(context, SellerFn.TYPE, String.valueOf(sellerId),
                        PaymentNotification.TYPE,
                        new PaymentNotification(orderId, Enums.OrderStatus.PAYMENT_FAILED)
                );
            }

            // this time the checkout is finished, send transaction mark to driver
            Utils.notifyTransactionComplete(context,
                    Enums.TransactionType.checkoutTask.toString(),
                    String.valueOf(customerId),
                    customerId,
                    transactionID,
                    String.valueOf(customerId),
                    "fail");
        }

        String log__ = getPartionText(context.self().id())
                + "send payment paymentResult to stock for order: " + uniqueOrderID
                + " sending finished \n";
        showLogPrt(log__);
    }

}

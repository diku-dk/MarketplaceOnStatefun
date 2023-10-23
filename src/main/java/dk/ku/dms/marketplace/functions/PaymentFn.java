package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.entities.CustomerCheckout;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.entities.OrderPayment;
import dk.ku.dms.marketplace.entities.OrderPaymentCard;
import dk.ku.dms.marketplace.messages.customer.CustomerMessages;
import dk.ku.dms.marketplace.messages.customer.NotifyCustomer;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.order.PaymentNotification;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import dk.ku.dms.marketplace.messages.shipment.PaymentConfirmed;
import dk.ku.dms.marketplace.messages.shipment.ShipmentMessages;
import dk.ku.dms.marketplace.messages.stock.PaymentStockEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import dk.ku.dms.marketplace.utils.Utils;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.StatefulFunction;
import org.apache.flink.statefun.sdk.java.StatefulFunctionSpec;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class PaymentFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/payment");

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withSupplier(PaymentFn::new)
            .build();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        LOG.info("Payment "+context.self().id()+" received!");
        try {
            // stock --> payment (request to payment)
            if (message.is(PaymentMessages.INVOICE_ISSUED_TYPE)) {
                onProcessPayment(context, message);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return context.done();
    }

    private void onProcessPayment(Context context, Message message) {

        InvoiceIssued invoiceIssued = message.as(PaymentMessages.INVOICE_ISSUED_TYPE);

        CustomerCheckout customerCheckout = invoiceIssued.getCustomerCheckout();

        LocalDateTime now = LocalDateTime.now();

        int orderId = invoiceIssued.getOrderId();

        int seq = 1;
        boolean cc = (customerCheckout.getPaymentType().equals(Enums.PaymentType.CREDIT_CARD.toString()));
//
        // create payment tuple
        List<OrderPayment> orderPayments = new ArrayList<OrderPayment>();
        OrderPaymentCard card = null;
        if (cc || customerCheckout.getPaymentType().equals(Enums.PaymentType.DEBIT_CARD.toString())) {
            OrderPayment cardPaymentLine = new OrderPayment(
                   orderId,
                    seq,
                    cc ? Enums.PaymentType.CREDIT_CARD : Enums.PaymentType.DEBIT_CARD,
                    customerCheckout.getInstallments(),
                    invoiceIssued.getTotalInvoice(),
                    Enums.PaymentStatus.succeeded
            );

            card = new OrderPaymentCard(orderId, seq, customerCheckout.getCardNumber(),
                    customerCheckout.getCardHolderName(), customerCheckout.getCardExpiration(), customerCheckout.getCardBrand());

            orderPayments.add(cardPaymentLine);
            seq++;
        }

        if (invoiceIssued.getCustomerCheckout().getPaymentType().contentEquals(Enums.PaymentType.BOLETO.toString()))
        {
            orderPayments.add(new OrderPayment(
                orderId,
                seq,
                Enums.PaymentType.BOLETO,
                1,
                invoiceIssued.getTotalInvoice(),
                Enums.PaymentStatus.succeeded
            ));
            seq++;
        }

        // then one line for each voucher
        for(OrderItem item : invoiceIssued.getItems())
        {
            if (item.getVoucher() > 0)
            {
                orderPayments.add(new OrderPayment(
                        orderId,
                        seq,
                        Enums.PaymentType.VOUCHER,
                        1,
                        item.getVoucher(),
                        Enums.PaymentStatus.succeeded
                ));
                seq++;
            }
        }

        // send message to stock
        for (OrderItem item : invoiceIssued.getItems()) {
            Message paymentStockMsg =
                    MessageBuilder.forAddress(StockFn.TYPE, String.valueOf(item.getSellerId())+'/'+item.getProductId())
                            .withCustomType(StockMessages.PAYMENT_STOCK_EVENT_TYPE, new PaymentStockEvent(item.getQuantity(), Enums.PaymentStatus.succeeded))
                            .build();
            context.send(paymentStockMsg);
        }

        List<Integer> sellerIds = invoiceIssued.getItems().stream().map(OrderItem::getSellerId).distinct().collect(Collectors.toList());

        PaymentNotification paymentNotification = new PaymentNotification(orderId, Integer.parseInt(context.self().id()), Enums.PaymentStatus.succeeded);

        for (int sellerId : sellerIds) {
            Message paymentSellerMsg =
                    MessageBuilder.forAddress(SellerFn.TYPE, String.valueOf(sellerId))
                            .withCustomType(OrderMessages.PAYMENT_NOTIFICATION_TYPE,
                                    paymentNotification)
                            .build();
            context.send(paymentSellerMsg);
        }

        Message paymentOrderMsg =
                MessageBuilder.forAddress(OrderFn.TYPE, context.self().id())
                        .withCustomType(OrderMessages.PAYMENT_NOTIFICATION_TYPE,
                                paymentNotification)
                        .build();
        context.send(paymentOrderMsg);

        Message paymentCustomerMsg =
                MessageBuilder.forAddress(CustomerFn.TYPE, context.self().id())
                        .withCustomType(CustomerMessages.TYPE,
                                new NotifyCustomer(Enums.CustomerNotificationType.notify_success_payment))
                        .build();
        context.send(paymentCustomerMsg);

        PaymentConfirmed paymentConfirmed = new PaymentConfirmed(customerCheckout, orderId, invoiceIssued.getTotalInvoice(), invoiceIssued.getItems(), now, customerCheckout.getInstanceId());

        Message paymentShipmentMsg =
                MessageBuilder.forAddress(ShipmentFn.TYPE,
                        String.valueOf(
                            Utils.getShipmentActorID(
                                    Integer.parseInt( context.self().id()), Constants.nShipmentPartitions) ) )
                        .withCustomType(ShipmentMessages.PAYMENT_CONFIRMED_TYPE,
                                paymentConfirmed)
                        .build();
        context.send(paymentShipmentMsg);
    }

}

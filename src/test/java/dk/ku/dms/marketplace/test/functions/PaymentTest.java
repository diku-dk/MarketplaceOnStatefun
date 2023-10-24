package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.messages.cart.CustomerCheckout;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.functions.PaymentFn;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.testing.SideEffects;
import org.apache.flink.statefun.sdk.java.testing.TestContext;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentTest {

    @Test
    public void testPayment() throws Throwable {

        Address self = new Address(PaymentFn.TYPE, "1");
        TestContext context = TestContext.forTarget(self);

        CustomerCheckout customerCheckout = new CustomerCheckout(1, "test","test","test","test","test",
                "test","test","BOLETO", "test", "test","test", "test",
                "test", 1, "1");

        OrderItem orderItem = new OrderItem(1,1,1,"test",1,100,0,1,100,100, 0, LocalDateTime.now().plusDays(1));
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        InvoiceIssued invoiceIssued = new InvoiceIssued(customerCheckout, 1, "1",
                orderItemList, 100, LocalDateTime.now(), "1");

        // Action
        PaymentFn function = new PaymentFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(PaymentMessages.INVOICE_ISSUED_TYPE, invoiceIssued)
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert(sentMessages.size() == 5);

//        assert(sentMessages.get(0).message().is(OrderMessages.CHECKOUT_REQUEST_TYPE));
//
//        CheckoutRequest resp = sentMessages.get(0).message().as(OrderMessages.CHECKOUT_REQUEST_TYPE);
//        assert (resp.getInstanceId().contentEquals("1"));

    }

}

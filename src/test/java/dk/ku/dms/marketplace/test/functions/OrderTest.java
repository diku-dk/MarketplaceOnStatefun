package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.CustomerCheckout;
import dk.ku.dms.marketplace.functions.CartFn;
import dk.ku.dms.marketplace.functions.OrderFn;
import dk.ku.dms.marketplace.functions.StockFn;
import dk.ku.dms.marketplace.messages.order.AttemptReservationResponse;
import dk.ku.dms.marketplace.messages.order.CheckoutRequest;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import dk.ku.dms.marketplace.messages.stock.AttemptReservationEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.states.OrderState;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.testing.SideEffects;
import org.apache.flink.statefun.sdk.java.testing.TestContext;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderTest {

    @Test
    public void testSendAttemptReservation() throws Throwable {

        // Arrange
        Address orderAddress = new Address(OrderFn.TYPE, "1");
        Address cartAddress = new Address(CartFn.TYPE, "1");

        TestContext context = TestContext.forTargetWithCaller(orderAddress, cartAddress);

        CartItem item = new CartItem(1,1, "testProductName", 1, 1, 1, 1, "0");

        CustomerCheckout customerCheckout = new CustomerCheckout(1, "test","test","test","test","test",
                "test","test","BOLETO", "test", "test","test", "test",
                "test", 1, "1");

        List<CartItem> items = new ArrayList<>( );
        items.add(item);

        CheckoutRequest checkoutRequest = new CheckoutRequest(LocalDateTime.now(), customerCheckout, items, "1");

        OrderFn function = new OrderFn();
        Message message = MessageBuilder
                .forAddress(orderAddress)
                .withCustomType(OrderMessages.CHECKOUT_REQUEST_TYPE, checkoutRequest)
                .build();
        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert(sentMessages.size() == 1);

        assert(sentMessages.get(0).message().is(StockMessages.ATTEMPT_RESERVATION_TYPE));

        AttemptReservationEvent resp = sentMessages.get(0).message().as(StockMessages.ATTEMPT_RESERVATION_TYPE);
        assert (resp.getOrderId() == 1);
        assert (resp.getCartItem().getQuantity() == 1);

    }

    @Test
    public void testSuccessAttemptReservationResponse() throws Throwable {

        Address orderAddress = new Address(OrderFn.TYPE, "1");
        Address stockAddress = new Address(StockFn.TYPE, "1/1");

        TestContext context = TestContext.forTargetWithCaller(orderAddress, stockAddress);

        OrderState orderState = new OrderState();
        CartItem item = new CartItem(1,1, "testProductName", 1, 1, 1, 1, "0");

        CustomerCheckout customerCheckout = new CustomerCheckout(1, "test","test","test","test","test",
                "test","test","BOLETO", "test", "test","test", "test",
                "test", 1, "1");

        List<CartItem> items = new ArrayList<>( );
        items.add(item);

        CheckoutRequest checkoutRequest = new CheckoutRequest(LocalDateTime.now(), customerCheckout, items, "1");

        orderState.getCheckouts().put(1, checkoutRequest);
        orderState.setUpRemainingAcks(1, 1);

        context.storage().set(OrderFn.ORDER_STATE, orderState);

        AttemptReservationResponse reservationResponse = new AttemptReservationResponse(
                1,item.getSellerId(), item.getProductId(), Enums.ItemStatus.IN_STOCK, 0);

        // Action
        OrderFn function = new OrderFn();
        Message message = MessageBuilder
                .forAddress(orderAddress)
                .withCustomType(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE, reservationResponse)
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert(sentMessages.size() == 2);

        assert(sentMessages.get(0).message().is(PaymentMessages.INVOICE_ISSUED_TYPE));

        InvoiceIssued invoiceIssued = sentMessages.get(0).message().as(PaymentMessages.INVOICE_ISSUED_TYPE);
        assert (invoiceIssued.getOrderId() == 1);

    }

    @Test
    public void testFailedAttemptReservationResponse() throws Throwable {

        Address orderAddress = new Address(OrderFn.TYPE, "1");
        Address stockAddress = new Address(StockFn.TYPE, "1/1");

        TestContext context = TestContext.forTargetWithCaller(orderAddress, stockAddress);

        OrderState orderState = new OrderState();
        CartItem item = new CartItem(1,1, "testProductName", 1, 1, 1, 1, "0");

        CustomerCheckout customerCheckout = new CustomerCheckout(1, "test","test","test","test","test",
                "test","test","BOLETO", "test", "test","test", "test",
                "test", 1, "1");

        List<CartItem> items = new ArrayList<>( );
        items.add(item);

        CheckoutRequest checkoutRequest = new CheckoutRequest(LocalDateTime.now(), customerCheckout, items, "1");

        orderState.getCheckouts().put(1, checkoutRequest);
        orderState.setUpRemainingAcks(1, 1);

        context.storage().set(OrderFn.ORDER_STATE, orderState);

        AttemptReservationResponse reservationResponse = new AttemptReservationResponse(
                1,item.getSellerId(), item.getProductId(), Enums.ItemStatus.OUT_OF_STOCK, 0);

        // Action
        OrderFn function = new OrderFn();
        Message message = MessageBuilder
                .forAddress(orderAddress)
                .withCustomType(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE, reservationResponse)
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert(sentMessages.size() == 0);

        assert(context.storage().get(OrderFn.ORDER_STATE).isPresent() && !context.storage().get(OrderFn.ORDER_STATE).get().getCheckouts().containsKey(1));

    }

}

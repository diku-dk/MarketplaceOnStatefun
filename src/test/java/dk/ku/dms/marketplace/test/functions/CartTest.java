package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.functions.CartFn;
import dk.ku.dms.marketplace.messages.cart.CartMessages;
import dk.ku.dms.marketplace.messages.cart.CustomerCheckout;
import dk.ku.dms.marketplace.messages.cart.GetCart;
import dk.ku.dms.marketplace.messages.order.CheckoutRequest;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.states.CartState;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.testing.SideEffects;
import org.apache.flink.statefun.sdk.java.testing.TestContext;
import org.junit.Test;

import java.util.List;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public class CartTest {

    @Test
    public void testSimpleAddItemToCart() throws Throwable {

        // Arrange
        Address self = new Address(CartFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        // set initial state
        context.storage().set(CartFn.CART_STATE,CartState.build());

        CartItem item = new CartItem(1,1, "testProductName", 1, 1, 1, 1, "0");

        // Action
        CartFn function = new CartFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(CartMessages.ADD_CART_ITEM_TYPE, item)
                .build();
        function.apply(context, message);

        // Assert State
        assert(context.storage().get(CartFn.CART_STATE).isPresent() && context.storage().get(CartFn.CART_STATE).get().getItems().get(0).toString().compareTo(item.toString()) == 0);
    }

    private static final int numItems = 2;

    @Test
    public void testSentCheckout() throws Throwable {

        // Arrange
        Address self = new Address(CartFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        // set initial state
        context.storage().set(CartFn.CART_STATE,CartState.build());

        // Actions
        CartFn function = new CartFn();

        for(int i = 1; i <= numItems; i++){
            CartItem item = new CartItem(1,i, "testProductName", 1, 1, 1, 1, "0");
            Message message = MessageBuilder
                    .forAddress(self)
                    .withCustomType(CartMessages.ADD_CART_ITEM_TYPE, item)
                    .build();
            function.apply(context, message);
        }

        CustomerCheckout customerCheckout = new CustomerCheckout(1, "test","test","test","test","test",
                "test","test","BOLETO", "test", "test","test", "test",
                "test", 1, "1");

        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(CartMessages.CUSTOMER_CHECKOUT_TYPE, customerCheckout)
                .build();
        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert(sentMessages.size() == 1);

        assert(sentMessages.get(0).message().is(OrderMessages.CHECKOUT_REQUEST_TYPE));

        CheckoutRequest resp = sentMessages.get(0).message().as(OrderMessages.CHECKOUT_REQUEST_TYPE);
        assert (resp.getInstanceId().contentEquals("1"));

        // Assert State
        assert(context.storage().get(CartFn.CART_STATE).isPresent() && context.storage().get(CartFn.CART_STATE).get().getItems().size() == 0 && context.storage().get(CartFn.CART_STATE).get().getStatus() == CartState.Status.OPEN);
    }

    @Test
    public void testGetCart() throws Throwable {

        // Arrange
        Address self = new Address(CartFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        // set initial state
        context.storage().set(CartFn.CART_STATE,CartState.build());

        // Actions
        CartFn function = new CartFn();

        for(int i = 1; i <= numItems; i++){
            CartItem item = new CartItem(1,i, "testProductName" + i, 1, 1, 1, 1, "0");
            Message message = MessageBuilder
                    .forAddress(self)
                    .withCustomType(CartMessages.ADD_CART_ITEM_TYPE, item)
                    .build();
            function.apply(context, message);
        }

        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(CartMessages.GET_CART_TYPE, new GetCart())
                .build();
        function.apply(context, message);

        // Assert Sent Messages
        List<SideEffects.EgressSideEffect> sent = context.getSentEgressMessages();
        assert(sent.size() > 0);

        byte[] byteArray = sent.get(0).message().egressMessageValueBytes().toByteArray();
        Messages.EgressRecord egressMsg = messageMapper.readValue(byteArray, Messages.EgressRecord.class);

        assert (egressMsg != null);

    }

}

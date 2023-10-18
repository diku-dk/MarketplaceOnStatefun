package Functions;

import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.functions.CartFn;
import dk.ku.dms.marketplace.messages.cart.CartMessages;
import dk.ku.dms.marketplace.states.CartState;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.testing.TestContext;
import org.junit.Test;

public class CustomerTest {

    @Test
    public void test() throws Throwable {

        // Arrange
        Address self = new Address(CartFn.TYPE, "1");
        // Address caller = new Address(..., ...);

        TestContext context = TestContext.forTarget(self);

        // set initial state
        context.storage().set(CartFn.CART_STATE,new CartState());

        CartItem item = new CartItem(1,1, "testProductName", 1, 1, 1, 1, "0");

        // Action
        CartFn function = new CartFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(CartMessages.ADD_CART_ITEM_TYPE, item )
                .build();
        function.apply(context, message);

        // Assert State
        assert(context.storage().get(CartFn.CART_STATE).get().getItems().get(0).toString().compareTo(item.toString()) == 0);
    }

}

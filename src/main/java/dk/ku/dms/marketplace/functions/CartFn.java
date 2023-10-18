package dk.ku.dms.marketplace.functions;

import java.util.concurrent.CompletableFuture;

import dk.ku.dms.marketplace.states.CartState;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.ku.dms.marketplace.messages.cart.CartMessages;

public class CartFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(CartFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/cart");

    public static final ValueSpec<CartState> CART_STATE = ValueSpec.named("cart").withCustomType(CartState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
                                                        .withValueSpec(CART_STATE)
                                                        .withSupplier(CartFn::new)
                                                        .build();


    private CartState getCartState(Context context) {
        return context.storage().get(CART_STATE).orElse(new CartState());
    }

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            if (message.is(CartMessages.ADD_CART_ITEM_TYPE)) {
                CartState cartState = getCartState(context);
                cartState.getItems().add( message.as(CartMessages.ADD_CART_ITEM_TYPE));
                context.storage().set(CART_STATE, cartState);
            } else if(message.is(CartMessages.CUSTOMER_CHECKOUT_TYPE)){
                CartState cartState = getCartState(context);
                cartState.setStatus(CartState.Status.CHECKOUT_SENT);



                onSeal(context);
            } else if(message.is(CartMessages.SEAL_TYPE)){
                onSeal(context);
            }
        } catch (Exception e) {
            LOG.error("");
        }

        return context.done();
    }

    private void onSeal(Context context) {
        CartState cartState = getCartState(context);
        cartState.setStatus(CartState.Status.OPEN);
        cartState.seal();
        context.storage().set(CART_STATE, cartState);
    }

}

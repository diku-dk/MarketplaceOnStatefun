package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.CustomerCheckout;
import dk.ku.dms.marketplace.messages.cart.CartMessages;
import dk.ku.dms.marketplace.messages.order.CheckoutRequest;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.states.CartState;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public final class CartFn implements StatefulFunction {

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
                cartState.getItems().add( message.as(CartMessages.ADD_CART_ITEM_TYPE) );
                context.storage().set(CART_STATE, cartState);
            } else if(message.is(CartMessages.CUSTOMER_CHECKOUT_TYPE)){
                CartState cartState = getCartState(context);
                CustomerCheckout customerCheckout = message.as(CartMessages.CUSTOMER_CHECKOUT_TYPE);
                cartState.setStatus(CartState.Status.CHECKOUT_SENT);
                CheckoutRequest checkoutRequest = new CheckoutRequest(LocalDateTime.now(), customerCheckout, cartState.getItems(), customerCheckout.getInstanceId());
                Message checkoutRequestMsg =
                        MessageBuilder.forAddress(OrderFn.TYPE, context.self().id())
                                .withCustomType(OrderMessages.CHECKOUT_REQUEST_TYPE, checkoutRequest)
                                .build();
                context.send(checkoutRequestMsg);
                onSeal(context, cartState);
            } else if(message.is(CartMessages.SEAL_TYPE)){
                CartState cartState = getCartState(context);
                onSeal(context, cartState);
            } else if(message.is(CartMessages.GET_CART_TYPE)){
                CartState cartState = getCartState(context);
                onGetCart(context, cartState);
            }
        } catch (Exception e) {
            LOG.error("ERROR: "+e.getMessage());
        }

        return context.done();
    }

    private void onSeal(Context context, CartState cartState) {
        cartState.setStatus(CartState.Status.OPEN);
        cartState.seal();
        context.storage().set(CART_STATE, cartState);
    }

    private void onGetCart(Context context, CartState cartState) {
        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, cartState.toString()))
                        .build();
        context.send(egressMessage);
    }

}

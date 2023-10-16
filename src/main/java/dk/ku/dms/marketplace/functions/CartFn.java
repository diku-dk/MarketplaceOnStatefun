package dk.ku.dms.marketplace.functions;

import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import dk.ku.dms.marketplace.common.Entity.BasketItem;
import dk.ku.dms.marketplace.common.Entity.CustomerCheckout;
import dk.ku.dms.marketplace.common.Utils.Utils;
import dk.ku.dms.marketplace.common.Entity.Checkout;
import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.Types.MsgToCartFn.*;
import dk.ku.dms.marketplace.Types.State.CartState;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;

public class CartFn implements StatefulFunction {

    static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNS_NAMESPACE, "cart");
    static final TypeName KFK_EGRESS = TypeName.typeNameOf("e-commerce.fns", "kafkaSink");

    static final ValueSpec<CartState> CARTSTATE = ValueSpec.named("cartState").withCustomType(CartState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(CARTSTATE)
            .withSupplier(CartFn::new)
            .build();

    Logger logger = Logger.getLogger("CartFn");

    private String getPartionText(String id) {
        return String.format("[ CartFn partitionId %s ] ", id);
    }
    private void showLog(String log) { logger.info(log);}
    private void showLogPrt(String log) { System.out.println(log); }

    private CartState getCartState(Context context) {
        return context.storage().get(CARTSTATE).orElse(new CartState());
    }

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            if (message.is(CustomerSession.TYPE)) {
                onCustomerSession(context, message);
            }
            // order ---> cart (send checkout result)
            else if (message.is(GetCart.TYPE)) { onGetCart(context); }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return context.done();
    }

    private void printLog(String log) {
        System.out.println(log);
    }

    private void onCustomerSession(Context context, Message message) {
        CustomerSession customerSession = message.as(CustomerSession.TYPE);
        String log = getPartionText(context.self().id())
                + "customer session [receive] \n";
        printLog(log);

        String type = customerSession.getType();
        if (type.equals("addToCart")) {
            onAddToCart(context, customerSession.getAddToCart());
        } else if (type.equals("checkout")) {
            onNotifyCheckout(context, customerSession.getCustomerCheckout());
        } else if (type.equals("clearCart")) {
            onCleanup(context);
        }
    }

    private void onAddToCart(Context context, AddToCart addToCart) {
        CartState cartState = getCartState(context);
//        AddToCart addToCart = message.as(AddToCart.TYPE);

        String log = getPartionText(context.self().id())
                + "add to cart [receive] \n";
        printLog(log);

        BasketItem item = new BasketItem(
                addToCart.getSellerId(),
                addToCart.getProductId(),
                addToCart.getProductName(),
                addToCart.getUnitPrice(),
                addToCart.getFreightValue(),
                addToCart.getQuantity(),
                addToCart.getVouchers(),
                addToCart.getVersion()
        );

        cartState.addItem(item.getProductId(), item);
        context.storage().set(CARTSTATE, cartState);

//        String log = String.format(getPartionText(context.self().id()) + "Item {%s} add to cart success\n", item.getProductId());
//        showLog(log);
        String log_ = getPartionText(context.self().id())
                + "add to cart [success] \n";
        printLog(log_);
    }


    private void onNotifyCheckout(Context context, CustomerCheckout customerCheckout) {
        CartState cartState = getCartState(context);
//        CustomerCheckout customerCheckout = message.as(CheckoutCart.TYPE).getCustomerCheckout();

        int transactionId = customerCheckout.getInstanceId();
//        logger.info("[receive] {tid=" + transactionId + "} checkout, cartFn " + context.self().id());
        String log_ = getPartionText(context.self().id())
                + "checkout [receive], " + "tid : " + transactionId + "\n";
        printLog(log_);

        String cartId = context.self().id();
        String customerId = String.valueOf(customerCheckout.getCustomerId());

        cartState.setCurrentTransactionId(transactionId);

//        String log_ = getPartionText(context.self().id())
//                + "checkout request received\n"
//                + "cartId: " + cartId + "\n"
//                + "customerId: " + customerId + "\n"
//                + "instanceId: " + customerCheckout.getInstanceId() + "\n";

        if (cartState.getStatus() == CartState.Status.CHECKOUT_SENT ){
            String log = getPartionText(context.self().id())
                    + "checkout is in process....please wait\n";
            showLog(log);
            throw new RuntimeException(log);
        }
        if (cartState.getItems().isEmpty()) {
            String log = getPartionText(context.self().id())
                    + "checkout fail as cart is empty\n";
            showLog(log);
            throw new RuntimeException(log);
        }
        if(!cartId.equals(customerId)) {
            String log = getPartionText(context.self().id())
                    + "checkout fail as customer id is not match\n"
                    + "cartId: " + cartId + "\n"
                    + "customerId: " + customerId + "\n"
                    + "instanceId: " + customerCheckout.getInstanceId() + "\n";

            showLog(log);
            throw new RuntimeException(log);
        }
        cartState.setStatus(CartState.Status.CHECKOUT_SENT);

        Checkout checkout = new Checkout(LocalDateTime.now(), customerCheckout, cartState.getItems());

        // order is chose randomly !!!
//        String orderPartitionId = String.valueOf((int) (Math.random() * Constants.nOrderPartitions));
        String orderPartitionId = context.self().id();
        Utils.sendMessage(context, OrderFn.TYPE, orderPartitionId, Checkout.TYPE, checkout);

        Seal(cartState, true);

        context.storage().set(CARTSTATE, cartState);
//
//        String log = getPartionText(context.self().id())
//                + "checkout message send........\n";
//        showLogPrt(log);
    }

    private void Seal(CartState cartState, boolean cleanItems) {
        cartState.setStatus(CartState.Status.OPEN);
        if (cleanItems) {
            cartState.clear();
        }
        cartState.setUpdateAt(LocalDateTime.now());
    }

    private void onCleanup(Context context) {
        CartState cartState = getCartState(context);
        // 删除cartState这个对象
        context.storage().remove(CARTSTATE);
//        logger.info(String.format("clear cart {%s} success", context.self().id()));
    }

    private void onGetCart(Context context) {
        CartState cartState = getCartState(context);

        String log = String.format(getPartionText(context.self().id())
                        + "get cart success\n"
                        + "cart status: {%s}\n"
                        + "cart content: {\n%s}\n"
                , cartState.getStatus(), cartState.getCartConent());
        showLog(log);
    }

}

package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.StockItem;
import dk.ku.dms.marketplace.egress.TransactionMark;
import dk.ku.dms.marketplace.messages.order.AttemptReservationResponse;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.stock.AttemptReservationEvent;
import dk.ku.dms.marketplace.messages.stock.PaymentStockEvent;
import dk.ku.dms.marketplace.messages.stock.ProductUpdatedEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class StockFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(StockFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/stock");

    public static final ValueSpec<StockItem> STOCK_STATE = ValueSpec.named("stock").withCustomType(StockItem.TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(STOCK_STATE)
            .withSupplier(StockFn::new)
            .build();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // seller ---> stock (set stock)
            if (message.is(StockMessages.SET_STOCK_ITEM_TYPE)) {
                StockItem item = message.as(StockMessages.SET_STOCK_ITEM_TYPE);
                context.storage().set(STOCK_STATE, item);
            }
            // product ---> stock (update product)
            else if (message.is(StockMessages.PRODUCT_UPDATED_TYPE)) {
                onUpdateProduct(context, message);
            }
            // order ---> stock (attempt reservation)
            else if (message.is(StockMessages.ATTEMPT_RESERVATION_TYPE)) {
                onAttemptReservation(context, message);
            }
            // payment ---> stock (payment result finally decided change stock or not
            else if (message.is(StockMessages.PAYMENT_STOCK_EVENT_TYPE)) {
                onHandlePaymentResult(context, message);
            } else if (message.is(StockMessages.GET_STOCK_ITEM_TYPE)) {
                onGetStockItem(context);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return context.done();
    }

    private void onGetStockItem(Context context) {
        StockItem stockItem =  context.storage().get(STOCK_STATE).orElse(null);
        if(stockItem == null){
            LOG.error("Stock item not present in state. ID = "+context.self().id());
            return;
        }

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, stockItem.toString()))
                        .build();
        context.send(egressMessage);
    }

    private void onUpdateProduct(Context context, Message message) {
        ProductUpdatedEvent productUpdated = message.as(StockMessages.PRODUCT_UPDATED_TYPE);

        int sellerId = productUpdated.getSellerId();
        String version = productUpdated.getInstanceId();

        StockItem stockItem =  context.storage().get(STOCK_STATE).orElse(null);

        if (stockItem == null) {
            TransactionMark mark = new TransactionMark(version,
                    Enums.TransactionType.UPDATE_PRODUCT, sellerId, Enums.MarkStatus.ERROR, "stock");

            final EgressMessage egressMessage =
                    EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                            .withCustomType(
                                    Messages.EGRESS_RECORD_JSON_TYPE,
                                    new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                            .build();

            context.send(egressMessage);
            return;
        }

        stockItem.setUpdatedAt(LocalDateTime.now());
        stockItem.setVersion(productUpdated.getInstanceId());
        context.storage().set(STOCK_STATE, stockItem);

        TransactionMark mark = new TransactionMark(version,
                Enums.TransactionType.UPDATE_PRODUCT, sellerId, Enums.MarkStatus.SUCCESS, "stock");

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                        .build();

        context.send(egressMessage);

    }

    private void onAttemptReservation(Context context, Message message) {

        AttemptReservationEvent attemptReservationEvent = message.as(StockMessages.ATTEMPT_RESERVATION_TYPE);
        CartItem cartItem = attemptReservationEvent.getCartItem();

        Enums.ItemStatus status = getItemStatus(context, cartItem.getQuantity(), cartItem.getVersion());

        final Optional<Address> caller = context.caller();
        if (caller.isPresent()) {
            AttemptReservationResponse resp =
                    new AttemptReservationResponse(attemptReservationEvent.getOrderId(), cartItem.getSellerId(), cartItem.getProductId(), status, attemptReservationEvent.getIdx());
            final Message request = MessageBuilder.forAddress(StockFn.TYPE, caller.get().id())
                                                    .withCustomType(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE, resp)
                                                    .build();
            context.send(request);
        } else {
            LOG.error("Caller unknown");
        }

    }

    private Enums.ItemStatus getItemStatus(Context context, int quantity, String version) {
        StockItem stockItem = context.storage().get(STOCK_STATE).orElse(null);

        if(stockItem == null){
            return Enums.ItemStatus.UNKNOWN;
        }

        if (stockItem.getVersion().compareTo(version) != 0) {
            return Enums.ItemStatus.UNAVAILABLE;
        }

        if (stockItem.getQtyReserved() + quantity > stockItem.getQtyAvailable()) {
            return Enums.ItemStatus.OUT_OF_STOCK;
        }

        stockItem.reserve(quantity);
        stockItem.setUpdatedAt(LocalDateTime.now());
        context.storage().set(STOCK_STATE, stockItem);

        return Enums.ItemStatus.IN_STOCK;
    }

    private void onHandlePaymentResult(Context context, Message message){

        StockItem stockItem =  context.storage().get(STOCK_STATE).orElse(null);
        if(stockItem == null){
            LOG.error("Stock item not present in state. ID = "+context.self().id());
            return;
        }

        PaymentStockEvent paymentResult = message.as(StockMessages.PAYMENT_STOCK_EVENT_TYPE);
        Enums.PaymentStatus orderStatus = paymentResult.getStatus();

        if (orderStatus == Enums.PaymentStatus.succeeded) {
            stockItem.confirmReservation(paymentResult.getQuantity());
        } else {
            stockItem.cancelReservation(paymentResult.getQuantity());
        }
        stockItem.setUpdatedAt(LocalDateTime.now());
        context.storage().set(STOCK_STATE, stockItem);
    }
}







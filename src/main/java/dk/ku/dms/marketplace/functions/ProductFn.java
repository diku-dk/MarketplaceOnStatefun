package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.egress.TransactionMark;
import dk.ku.dms.marketplace.entities.Product;
import dk.ku.dms.marketplace.messages.product.ProductMessages;
import dk.ku.dms.marketplace.messages.product.UpdatePrice;
import dk.ku.dms.marketplace.messages.stock.ProductUpdatedEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class ProductFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/product");

    public static final Type<Product> STATE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ProductState"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, Product.class));

    public static final ValueSpec<Product> PRODUCT_STATE = ValueSpec.named("product").withCustomType(STATE_TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(PRODUCT_STATE)
            .withSupplier(ProductFn::new)
            .build();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try{
            // driver --> product (update product)
            if (message.is(ProductMessages.UPSERT_PRODUCT_TYPE)) {
                onUpdateProduct(context, message);
            }
            // driver --> product (update price)
            else if (message.is(ProductMessages.UPDATE_PRICE_TYPE)) {
                onUpdatePrice(context, message);
            }
            else if(message.is(ProductMessages.GET_PRODUCT_TYPE)){
                onGetProduct(context);
            }
            else {
                LOG.error("Message unknown: "+message);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return context.done();
    }

    private void onGetProduct(Context context) {
        Optional<Product> opProd = context.storage().get(PRODUCT_STATE);
        if(opProd.isPresent()) {
            final EgressMessage egressMessage =
                    EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                            .withCustomType(
                                    Messages.EGRESS_RECORD_JSON_TYPE,
                                    new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, opProd.get().toString()))
                            .build();
            context.send(egressMessage);
        } else {
            LOG.error("Product not present in state. ID = "+context.self().id());
        }
    }

    private void onUpdateProduct(Context context, Message message) {
        Product product = message.as(ProductMessages.UPSERT_PRODUCT_TYPE);

        if(context.storage().get(PRODUCT_STATE).isPresent()) {

            context.storage().set(PRODUCT_STATE, product);

            String id = product.getSellerId() + "-" + product.getProductId();

            ProductUpdatedEvent productUpdated =
                    new ProductUpdatedEvent(product.getSellerId(), product.getProductId(), product.getVersion());

            Message updateProductMsg =
                    MessageBuilder.forAddress(StockFn.TYPE, id)
                            .withCustomType(StockMessages.PRODUCT_UPDATED_TYPE, productUpdated)
                            .build();

            context.send(updateProductMsg);
        } else {
            context.storage().set(PRODUCT_STATE, product);
        }
    }

    private void onUpdatePrice(Context context, Message message) {
        UpdatePrice updatePrice = message.as(ProductMessages.UPDATE_PRICE_TYPE);
        String tid = updatePrice.getInstanceId();
        int sellerId = updatePrice.getSellerId();
        Product product = context.storage().get(PRODUCT_STATE).orElse(null);
        if(product == null){
            TransactionMark mark = new TransactionMark(tid,
                    Enums.TransactionType.PRICE_UPDATE, sellerId, Enums.MarkStatus.ERROR, "product");

            final EgressMessage egressMessage =
                    EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                            .withCustomType(
                                    Messages.EGRESS_RECORD_JSON_TYPE,
                                    new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                            .build();

            context.send(egressMessage);
            return;
        }

        product.setPrice(updatePrice.getPrice());
        product.setUpdatedAt(LocalDateTime.now());
        context.storage().set(PRODUCT_STATE, product);

        TransactionMark mark = new TransactionMark(tid,
                Enums.TransactionType.PRICE_UPDATE, sellerId, Enums.MarkStatus.SUCCESS, "product");

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                        .build();

        context.send(egressMessage);
    }
}

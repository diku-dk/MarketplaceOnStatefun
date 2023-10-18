package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.entities.TransactionMark;
import dk.ku.dms.marketplace.entities.Product;
import dk.ku.dms.marketplace.messages.stock.ProductUpdatedEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.messages.product.ProductMessages;
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
import java.util.concurrent.CompletableFuture;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public class ProductFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(ProductFn.class);

    public static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNCTIONS_NAMESPACE, "product");

    public static final Type<Product> STATE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ProductState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Product.class));

    public static final ValueSpec<Product> PRODUCT_STATE = ValueSpec.named("product").withCustomType(STATE_TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(PRODUCT_STATE)
            .withSupplier(ProductFn::new)
            .build();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try{
            // driver --> product (add product)
            if (message.is(ProductMessages.ADD_PRODUCT_TYPE)) {
                Product product = message.as(ProductMessages.ADD_PRODUCT_TYPE);
                context.storage().set(PRODUCT_STATE, product);
            }
            // driver --> product (update product)
            else if (message.is(ProductMessages.UPDATE_PRODUCT_TYPE)) {
                onUpdateProduct(context, message);
            }
            // driver --> product (update price)
            else if (message.is(ProductMessages.UPDATE_PRICE_TYPE)) {
                onUpdatePrice(context, message);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return context.done();
    }

    private void onUpdateProduct(Context context, Message message) {
        Product product = message.as(ProductMessages.UPDATE_PRODUCT_TYPE);
        context.storage().set(PRODUCT_STATE, product);

        String id = product.getSellerId() + "/" + product.getProductId();

        ProductUpdatedEvent productUpdated =
                new ProductUpdatedEvent(product.getSellerId(), product.getProductId(), product.getVersion());

        Message updateProductMsg =
                MessageBuilder.forAddress(StockFn.TYPE, id)
                        .withCustomType(StockMessages.PRODUCT_UPDATED_TYPE, productUpdated)
                        .build();

        context.send(updateProductMsg);
    }

    private void onUpdatePrice(Context context, Message message) {
        ProductMessages.UpdatePrice updatePrice = message.as(ProductMessages.UPDATE_PRICE_TYPE);
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

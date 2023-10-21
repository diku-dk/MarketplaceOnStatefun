package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.Product;
import dk.ku.dms.marketplace.entities.TransactionMark;
import dk.ku.dms.marketplace.functions.ProductFn;
import dk.ku.dms.marketplace.messages.product.ProductMessages;
import dk.ku.dms.marketplace.messages.product.UpdatePrice;
import dk.ku.dms.marketplace.messages.stock.ProductUpdatedEvent;
import dk.ku.dms.marketplace.messages.stock.StockMessages;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.testing.SideEffects;
import org.apache.flink.statefun.sdk.java.testing.TestContext;
import org.junit.Test;

import java.util.List;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class ProductTest {

    @Test
    public void testPriceUpdate() throws Throwable {

        // Arrange
        Address self = new Address(ProductFn.TYPE, "1/1");

        TestContext context = TestContext.forTarget(self);

        Product product = new Product(1,1,"testName", "sku",
                "category", "description", 1, 1, "0");

        // set initial state
        context.storage().set(ProductFn.PRODUCT_STATE, product);

        UpdatePrice updatePrice = new UpdatePrice(1, 1, 10, "1");

        // Action
        ProductFn function = new ProductFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(ProductMessages.UPDATE_PRICE_TYPE, updatePrice)
                .build();

        function.apply(context, message);

        // Assert Sent Messages
        List<SideEffects.EgressSideEffect> sent = context.getSentEgressMessages();
        assert(sent.size() > 0);

        byte[] byteArray = sent.get(0).message().egressMessageValueBytes().toByteArray();
        Messages.EgressRecord egressMsg = mapper.readValue(byteArray, Messages.EgressRecord.class);
        TransactionMark mark = mapper.readValue(egressMsg.getPayload(), TransactionMark.class);

        assert(mark.getStatus() == Enums.MarkStatus.SUCCESS);
        assert(mark.getTid().compareTo("1") == 0);

        // Assert State
        assert(context.storage().get(ProductFn.PRODUCT_STATE).isPresent() && context.storage().get(ProductFn.PRODUCT_STATE).get().getPrice() == updatePrice.getPrice());

    }

    @Test
    public void testProductUpdate() throws Throwable {

        // Arrange
        Address self = new Address(ProductFn.TYPE, "1/1");

        TestContext context = TestContext.forTarget(self);

        Product product = new Product(1,1,"testName", "sku",
                "category", "description", 1, 1, "1");

        // set initial state
        context.storage().set(ProductFn.PRODUCT_STATE, product);

        Product newProduct = new Product(1,1,"testName", "sku",
                "category", "description", 1, 1, "2");

        // Action
        ProductFn function = new ProductFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(ProductMessages.UPSERT_PRODUCT_TYPE, newProduct)
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert (sentMessages.size() == 1);

        assert(sentMessages.get(0).message().is(StockMessages.PRODUCT_UPDATED_TYPE));

        ProductUpdatedEvent productUpdatedEvent = sentMessages.get(0).message().as(StockMessages.PRODUCT_UPDATED_TYPE);
        assert (productUpdatedEvent.getInstanceId().contentEquals("2"));

        // Assert State
        assert(context.storage().get(ProductFn.PRODUCT_STATE).isPresent() && context.storage().get(ProductFn.PRODUCT_STATE).get().getVersion().contentEquals("2"));

    }

}

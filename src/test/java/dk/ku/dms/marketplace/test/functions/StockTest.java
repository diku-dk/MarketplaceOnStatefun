package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.Product;
import dk.ku.dms.marketplace.entities.StockItem;
import dk.ku.dms.marketplace.entities.TransactionMark;
import dk.ku.dms.marketplace.functions.CartFn;
import dk.ku.dms.marketplace.functions.ProductFn;
import dk.ku.dms.marketplace.functions.StockFn;
import dk.ku.dms.marketplace.messages.product.ProductMessages;
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

public class StockTest {


    @Test
    public void testSetStockItem() throws Throwable {
        Address self = new Address(StockFn.TYPE, "1/1");
        TestContext context = TestContext.forTarget(self);

        StockItem stockItem = new StockItem(1,1,10, 0,
                0, 1, "test",  "1");

        // Action
        StockFn function = new StockFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(StockMessages.SET_STOCK_ITEM_TYPE, stockItem)
                .build();

        function.apply(context, message);

        // Assert State
        assert(context.storage().get(StockFn.STOCK_STATE).get().getVersion().contentEquals("1"));
    }

    @Test
    public void testProductUpdate() throws Throwable {

        // Arrange
        Address self = new Address(StockFn.TYPE, "1/1");
        Address caller = new Address(CartFn.TYPE, "1/1");

        TestContext context = TestContext.forTargetWithCaller(self, caller);

        StockItem stockItem = new StockItem(1,1,10, 0,
                0, 1, "test",  "1");

        // set initial state
        context.storage().set(StockFn.STOCK_STATE, stockItem);

        ProductUpdatedEvent productUpdatedEvent = new ProductUpdatedEvent(1,1, "2");

        // Action
        StockFn function = new StockFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(StockMessages.PRODUCT_UPDATED_TYPE, productUpdatedEvent)
                .build();

        function.apply(context, message);

        // Assert Sent Messages
        List<SideEffects.EgressSideEffect> sent = context.getSentEgressMessages();
        assert(sent.size() > 0);

        byte[] byteArray = sent.get(0).message().egressMessageValueBytes().toByteArray();
        Messages.EgressRecord egressMsg = mapper.readValue(byteArray, Messages.EgressRecord.class);
        TransactionMark mark = mapper.readValue(egressMsg.getPayload(), TransactionMark.class);

        assert(mark.getStatus() == Enums.MarkStatus.SUCCESS);
        assert(mark.getTid().compareTo("2") == 0);

        // Assert State
        assert(context.storage().get(StockFn.STOCK_STATE).get().getVersion().contentEquals("2"));

    }


}

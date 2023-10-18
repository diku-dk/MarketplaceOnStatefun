package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.StockItem;
import dk.ku.dms.marketplace.entities.TransactionMark;
import dk.ku.dms.marketplace.functions.OrderFn;
import dk.ku.dms.marketplace.functions.StockFn;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.stock.AttemptReservationEvent;
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
        assert(context.storage().get(StockFn.STOCK_STATE).isPresent() && context.storage().get(StockFn.STOCK_STATE).get().getVersion().contentEquals("1"));
    }

    @Test
    public void testProductUpdate() throws Throwable {

        // Arrange
        Address self = new Address(StockFn.TYPE, "1/1");

        TestContext context = TestContext.forTarget(self);

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
        assert(context.storage().get(StockFn.STOCK_STATE).isPresent() && context.storage().get(StockFn.STOCK_STATE).get().getVersion().contentEquals("2"));
    }

    @Test
    public void testFailedAttemptReservation() throws Throwable {

        // Arrange
        Address self = new Address(StockFn.TYPE, "1/1");
        Address caller = new Address(OrderFn.TYPE, "1");

        TestContext context = TestContext.forTargetWithCaller(self, caller);

        StockItem stockItem = new StockItem(1,1,10, 0,
                0, 1, "test",  "1");

        // set initial state
        context.storage().set(StockFn.STOCK_STATE, stockItem);


        CartItem item = new CartItem(1,1, "testProductName", 1, 1, 1, 1, "2");
        AttemptReservationEvent event = new AttemptReservationEvent(1,item);

        // Action
        StockFn function = new StockFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(StockMessages.ATTEMPT_RESERVATION_TYPE, event)
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert (sentMessages.size() == 1);

        assert(sentMessages.get(0).message().is(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE));

        OrderMessages.AttemptReservationResponse resp = sentMessages.get(0).message().as(OrderMessages.ATTEMPT_RESERVATION_RESPONSE_TYPE);
        assert (resp.getStatus() == Enums.ItemStatus.UNAVAILABLE);

    }


}

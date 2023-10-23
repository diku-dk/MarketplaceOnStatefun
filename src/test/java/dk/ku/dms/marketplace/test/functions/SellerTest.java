package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.CustomerCheckout;
import dk.ku.dms.marketplace.entities.OrderEntry;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.entities.TransactionMark;
import dk.ku.dms.marketplace.functions.OrderFn;
import dk.ku.dms.marketplace.functions.SellerFn;
import dk.ku.dms.marketplace.functions.ShipmentFn;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.order.ShipmentNotification;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import dk.ku.dms.marketplace.messages.seller.QueryDashboard;
import dk.ku.dms.marketplace.messages.seller.SellerMessages;
import dk.ku.dms.marketplace.states.SellerState;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.testing.SideEffects;
import org.apache.flink.statefun.sdk.java.testing.TestContext;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class SellerTest {

    @Test
    public void testInvoiceIssued() throws Throwable {

        // Arrange
        Address self = new Address(SellerFn.TYPE, "1");
        Address orderAddress = new Address(OrderFn.TYPE, "1");

        TestContext context = TestContext.forTargetWithCaller(self, orderAddress);

        SellerState sellerState = new SellerState();

        // set initial state
        context.storage().set(SellerFn.SELLER_STATE, sellerState);

        CustomerCheckout customerCheckout = new CustomerCheckout(1, "test","test","test","test","test",
                "test","test","BOLETO", "test", "test","test", "test",
                "test", 1, "1");

        OrderItem orderItem = new OrderItem(1,1,1,"test",1,100,0,1,100,100, 0, LocalDateTime.now().plusDays(1));
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        InvoiceIssued invoiceIssued = new InvoiceIssued(customerCheckout, 1, "1",
                orderItemList, 100, LocalDateTime.now(), "1");

        // Action
        SellerFn function = new SellerFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(PaymentMessages.INVOICE_ISSUED_TYPE, invoiceIssued)
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert(sentMessages.size() == 0);

        assert (context.storage().get(SellerFn.SELLER_STATE).isPresent() &&
                context.storage().get(SellerFn.SELLER_STATE).get().getOrderEntries().get("1-1").size() == 1 );

    }

    @Test
    public void testQueryDashboard() throws Throwable {

        // Arrange
        Address self = new Address(SellerFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        SellerState sellerState = new SellerState();
        List<OrderEntry> entries = new ArrayList<>();

        entries.add(new OrderEntry(1, 1, 1,
                "test", 1, 100, 100, 100, 0, 0, 100, Enums.OrderStatus.INVOICED));
        entries.add(new OrderEntry(1, 1, 2,
                "test2", 1, 100, 100, 100, 0, 0, 100, Enums.OrderStatus.INVOICED));

        sellerState.getOrderEntries().put("1-1", entries);

        // set initial state
        context.storage().set(SellerFn.SELLER_STATE, sellerState);

        // Action
        SellerFn function = new SellerFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(SellerMessages.QUERY_DASHBOARD_TYPE, new QueryDashboard("1"))
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

    }

    @Test
    public void testShipmentConcluded() throws Throwable {

        // Arrange
        Address self = new Address(SellerFn.TYPE, "1");
        Address shipmentAddress = new Address(ShipmentFn.TYPE, "1");

        TestContext context = TestContext.forTargetWithCaller(self, shipmentAddress);

        SellerState sellerState = new SellerState();
        List<OrderEntry> entries = new ArrayList<>();

        entries.add(new OrderEntry(1, 1, 1,
                "test", 1, 100, 100, 100, 0, 0, 100, Enums.OrderStatus.INVOICED));
        entries.add(new OrderEntry(1, 1, 2,
                "test2", 1, 100, 100, 100, 0, 0, 100, Enums.OrderStatus.INVOICED));

        sellerState.getOrderEntries().put("1-1", entries);

        // set initial state
        context.storage().set(SellerFn.SELLER_STATE, sellerState);

        ShipmentNotification notification = new ShipmentNotification(1, 1, Enums.ShipmentStatus.CONCLUDED, LocalDateTime.now());

        // Action
        SellerFn function = new SellerFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(OrderMessages.SHIPMENT_NOTIFICATION_TYPE, notification)
                .build();
        function.apply(context, message);

        // Assert Sent Messages
        List<SideEffects.EgressSideEffect> sent = context.getSentEgressMessages();
        assert(sent.size() == 0);

        assert(context.storage().get(SellerFn.SELLER_STATE).isPresent() && context.storage().get(SellerFn.SELLER_STATE).get().getOrderEntries().size() == 0);

    }

}

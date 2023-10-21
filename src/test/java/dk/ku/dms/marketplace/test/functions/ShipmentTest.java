package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.entities.Package;
import dk.ku.dms.marketplace.entities.*;
import dk.ku.dms.marketplace.functions.ShipmentFn;
import dk.ku.dms.marketplace.functions.ShipmentProxyFn;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.order.ShipmentNotification;
import dk.ku.dms.marketplace.messages.seller.SellerMessages;
import dk.ku.dms.marketplace.messages.shipment.PaymentConfirmed;
import dk.ku.dms.marketplace.messages.shipment.ShipmentMessages;
import dk.ku.dms.marketplace.messages.shipment.UpdateShipment;
import dk.ku.dms.marketplace.messages.shipment.UpdateShipmentAck;
import dk.ku.dms.marketplace.states.ShipmentProxyState;
import dk.ku.dms.marketplace.states.ShipmentState;
import dk.ku.dms.marketplace.utils.Constants;
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

public class ShipmentTest {

    @Test
    public void testShipment() throws Throwable {

        // Arrange
        Address self = new Address(ShipmentFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        CustomerCheckout customerCheckout = new CustomerCheckout(1, "test","test","test","test","test",
                "test","test","BOLETO", "test", "test","test", "test",
                "test", 1, "1");

        OrderItem orderItem = new OrderItem(1,1,1,"test",1,100,0,1,100,100, LocalDateTime.now().plusDays(1));
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        PaymentConfirmed paymentConfirmed = new PaymentConfirmed(customerCheckout, 1, 100, orderItemList, LocalDateTime.now(), "1");

        // Action
        ShipmentFn function = new ShipmentFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(ShipmentMessages.PAYMENT_CONFIRMED_TYPE, paymentConfirmed)
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

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert (sentMessages.size() == 1);

        assert(sentMessages.get(0).message().is(OrderMessages.SHIPMENT_NOTIFICATION_TYPE));

        ShipmentNotification shipmentNotification = sentMessages.get(0).message().as(OrderMessages.SHIPMENT_NOTIFICATION_TYPE);
        assert (shipmentNotification.getOrderId() == 1);

    }

    @Test
    public void testDelivery() throws Throwable {

        // Arrange
        Address self = new Address(ShipmentFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        ShipmentState shipmentState = new ShipmentState();

        Shipment shipment = new Shipment(1, 1, 1, 1, 1, LocalDateTime.now(),
                Enums.ShipmentStatus.APPROVED, "test", "test", "test", "test", "test", "test" );

        Package package_ = new Package(1,1,1,1,1,1,1,"test", LocalDateTime.now(), Enums.PackageStatus.SHIPPED);
        List<Package> packages = new ArrayList<>();
        packages.add(package_);

        shipmentState.getShipments().put(shipment.getShipmentId(),shipment);
        shipmentState.getPackages().put(shipment.getShipmentId(), packages);

        // set initial state
        context.storage().set(ShipmentFn.SHIPMENT_STATE, shipmentState);

        // Action
        ShipmentFn function = new ShipmentFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(ShipmentMessages.UPDATE_SHIPMENT_TYPE, new UpdateShipment("1"))
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert (sentMessages.size() == 3);

        assert(sentMessages.get(0).message().is(OrderMessages.SHIPMENT_NOTIFICATION_TYPE));
        assert(sentMessages.get(1).message().is(SellerMessages.DELIVERY_NOTIFICATION_TYPE));
        assert(sentMessages.get(2).message().is(OrderMessages.SHIPMENT_NOTIFICATION_TYPE));

        assert(context.storage().get(ShipmentFn.SHIPMENT_STATE).isPresent()
                && context.storage().get(ShipmentFn.SHIPMENT_STATE).get().getShipments().get(1).getStatus() == Enums.ShipmentStatus.CONCLUDED);

    }

    @Test
    public void testInitProxy() throws Throwable {

        // Arrange
        Address self = new Address(ShipmentProxyFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        // Action
        ShipmentProxyFn function = new ShipmentProxyFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(ShipmentMessages.UPDATE_SHIPMENT_TYPE, new UpdateShipment("1"))
                .build();

        function.apply(context, message);

        List<SideEffects.SendSideEffect> sentMessages = context.getSentMessages();

        assert (sentMessages.size() == Constants.nShipmentPartitions);

    }

    @Test
    public void testFinishDeliveryProxy() throws Throwable {

        // Arrange
        Address self = new Address(ShipmentProxyFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        ShipmentProxyState state = new ShipmentProxyState();
        state.tidList.put("1", 1);

        // set initial state
        context.storage().set(ShipmentProxyFn.PROXY_STATE, state);

        // Action
        ShipmentProxyFn function = new ShipmentProxyFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(ShipmentMessages.UPDATE_SHIPMENT_ACK_TYPE, new UpdateShipmentAck("1"))
                .build();

        function.apply(context, message);

        // Assert Sent Messages
        List<SideEffects.EgressSideEffect> sent = context.getSentEgressMessages();
        assert(sent.size() > 0);

        assert(context.storage().get(ShipmentProxyFn.PROXY_STATE).isPresent()
                && !context.storage().get(ShipmentProxyFn.PROXY_STATE).get().tidList.containsKey("1"));


    }

}

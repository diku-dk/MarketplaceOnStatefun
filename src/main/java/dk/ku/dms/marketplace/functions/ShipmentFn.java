package dk.ku.dms.marketplace.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.egress.TransactionMark;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.entities.Package;
import dk.ku.dms.marketplace.entities.Shipment;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.order.ShipmentNotification;
import dk.ku.dms.marketplace.messages.seller.DeliveryNotification;
import dk.ku.dms.marketplace.messages.seller.SellerMessages;
import dk.ku.dms.marketplace.messages.shipment.GetShipments;
import dk.ku.dms.marketplace.messages.shipment.PaymentConfirmed;
import dk.ku.dms.marketplace.messages.shipment.ShipmentMessages;
import dk.ku.dms.marketplace.messages.shipment.UpdateShipmentAck;
import dk.ku.dms.marketplace.states.ShipmentState;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import dk.ku.dms.marketplace.utils.PostgresHelper;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class ShipmentFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/shipment");

    static final ValueSpec<Integer> NEXT_SHIPMENT_ID_STATE = ValueSpec.named("nextShipmentId").withIntType();
    public static final ValueSpec<ShipmentState> SHIPMENT_STATE = ValueSpec.named("shipmentState").withCustomType(ShipmentState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpecs(SHIPMENT_STATE, NEXT_SHIPMENT_ID_STATE)
            .withSupplier(ShipmentFn::new)
            .build();

      private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            if (message.is(ShipmentMessages.PAYMENT_CONFIRMED_TYPE)) {
                onProcessShipment(context, message);
            }
            else if (message.is(ShipmentMessages.UPDATE_SHIPMENT_TYPE)) {
                onUpdateShipment(context, message);
            }
            else if(message.is(ShipmentMessages.GET_SHIPMENTS_TYPE)){
                onGetShipments(context,message);
            }
            else {
                LOG.error("Message unknown: "+message);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return context.done();
    }

    private int generateNextShipmentId(Context context) {
        int nextId = context.storage().get(NEXT_SHIPMENT_ID_STATE).orElse(0) + 1;
        context.storage().set(NEXT_SHIPMENT_ID_STATE, nextId);
        return nextId;
    }

    private ShipmentState getShipmentState(Context context) {
        return context.storage().get(SHIPMENT_STATE).orElse(ShipmentState.build());
    }

    private void onGetShipments(Context context, Message message) {
        GetShipments msg = message.as(ShipmentMessages.GET_SHIPMENTS_TYPE);
        ShipmentState state = getShipmentState(context);
        List<Shipment> shipments = state.getShipments().values().stream().filter(shipment -> shipment.getCustomerId() == msg.getCustomerId()).collect(Collectors.toList());

        StringBuilder b = new StringBuilder();
        shipments.forEach(b::append);

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, b.toString()))
                        .build();

        context.send(egressMessage);
    }

    private void onProcessShipment(Context context, Message message) {
        PaymentConfirmed paymentConfirmed = message.as(ShipmentMessages.PAYMENT_CONFIRMED_TYPE);
        LocalDateTime now = LocalDateTime.now();

        int shipmentId = generateNextShipmentId(context);
        Shipment shipment = new Shipment(
                shipmentId,
                paymentConfirmed.getOrderId(),
                paymentConfirmed.getCustomerCheckout().getCustomerId(),
                paymentConfirmed.getItems().size(),
                (float)  paymentConfirmed.getItems().stream().mapToDouble(OrderItem::getFreightValue).sum(),
                now,
                Enums.ShipmentStatus.APPROVED,
                paymentConfirmed.getCustomerCheckout().getFirstName(),
                paymentConfirmed.getCustomerCheckout().getLastName(),
                paymentConfirmed.getCustomerCheckout().getStreet(),
                paymentConfirmed.getCustomerCheckout().getZipCode(),
                paymentConfirmed.getCustomerCheckout().getCity(),
                paymentConfirmed.getCustomerCheckout().getState()
        );

        int packageId = 1;
        List<Package> packages = new ArrayList<>();
        for (OrderItem orderItem : paymentConfirmed.getItems()) {
            Package pkg = new Package(
                    shipmentId,
                    paymentConfirmed.getOrderId(),
                    packageId,
                    orderItem.getSellerId(),
                    orderItem.getProductId(),
                    orderItem.getQuantity(),
                    orderItem.getFreightValue(),
                    orderItem.getProductName(),
                    now,
                    Enums.PackageStatus.shipped
            );
            packages.add(pkg);
            packageId++;
        }

        ShipmentState shipmentState = getShipmentState(context);
        shipmentState.getShipments().put(shipmentId, shipment);
        shipmentState.getPackages().put(shipmentId, packages);
        context.storage().set(SHIPMENT_STATE, shipmentState);

        ShipmentNotification shipmentNotification = new ShipmentNotification(
                paymentConfirmed.getOrderId(),
                paymentConfirmed.getCustomerCheckout().getCustomerId(),
                Enums.ShipmentStatus.APPROVED,
                now
        );

        List<Integer> sellerIds = paymentConfirmed.getItems().stream().map(OrderItem::getSellerId).distinct().collect(Collectors.toList());
        for (int sellerId : sellerIds) {
            Message paymentSellerMsg =
                    MessageBuilder.forAddress(SellerFn.TYPE, String.valueOf(sellerId))
                            .withCustomType(OrderMessages.SHIPMENT_NOTIFICATION_TYPE,
                                    shipmentNotification)
                            .build();
            context.send(paymentSellerMsg);
        }

        Message shipmentOrderMsg =
                MessageBuilder.forAddress(OrderFn.TYPE, String.valueOf(paymentConfirmed.getCustomerCheckout().getCustomerId()))
                        .withCustomType(OrderMessages.SHIPMENT_NOTIFICATION_TYPE,
                                shipmentNotification)
                        .build();
        context.send(shipmentOrderMsg);

        TransactionMark mark = new TransactionMark(paymentConfirmed.getInstanceId(),
                Enums.TransactionType.CUSTOMER_SESSION, paymentConfirmed.getCustomerCheckout().getCustomerId(),
                Enums.MarkStatus.SUCCESS, "shipment");

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                        .build();

        context.send(egressMessage);

    }

    private void onUpdateShipment(Context context, Message message) { // throws SQLException, JsonProcessingException {

        ShipmentState shipmentState = getShipmentState(context);

        // the minimum shipment ID for each seller.
        Map<Integer, Integer> q = shipmentState.getOldestOpenShipmentPerSeller();

        for (Map.Entry<Integer, Integer> kv : q.entrySet()) {
            List<Package> sellerPackages = shipmentState.getShippedPackagesByShipmentIdAndSellerId(kv.getKey(), kv.getValue());
            updatePackageDelivery(context, shipmentState, sellerPackages, kv.getKey(), kv.getValue());
        }

        context.storage().set(SHIPMENT_STATE, shipmentState);

        UpdateShipment updateShipment = message.as(ShipmentMessages.UPDATE_SHIPMENT_TYPE);
        // send ack to caller (proxy)
        if(context.caller().isPresent()) {
            Message updateShipmentMsg =
                    MessageBuilder.forAddress(ShipmentProxyFn.TYPE, context.caller().get().id())
                            .withCustomType(ShipmentMessages.UPDATE_SHIPMENT_ACK_TYPE, new UpdateShipmentAck(updateShipment.getTid()))
                            .build();
            context.send(updateShipmentMsg);
        } else {
            LOG.error("Caller is not present!");
        }
    }

    private void updatePackageDelivery(Context context, ShipmentState shipmentState,
                                       List<Package> sellerPackages, int sellerId, int shipmentId) { // throws SQLException, JsonProcessingException {

        Shipment shipment = shipmentState.getShipments().get(shipmentId);
        LocalDateTime now = LocalDateTime.now();

        long countDelivered = shipmentState.getTotalDeliveredPackagesForShipment(shipmentId);

        for (Package p : sellerPackages) {
            p.setPackageStatus(Enums.PackageStatus.delivered);
            p.setDeliveredTime(now);

            DeliveryNotification deliveryNotification = new DeliveryNotification(
                    shipment.getCustomerId(),
                    p.getOrderId(),
                    p.getPackageId(),
                    p.getSellerId(),
                    p.getProductId(),
                    p.getProductName(),
                    Enums.PackageStatus.delivered,
                    now
            );

            // notify customer
            Message deliveryCustomerNotificationMsg =
                    MessageBuilder.forAddress(CustomerFn.TYPE, String.valueOf(shipment.getCustomerId()))
                            .withCustomType(SellerMessages.DELIVERY_NOTIFICATION_TYPE, deliveryNotification)
                            .build();
            context.send(deliveryCustomerNotificationMsg);

            // notify seller
            Message deliverySellerNotificationMsg =
                    MessageBuilder.forAddress(SellerFn.TYPE, String.valueOf(p.getSellerId()))
                            .withCustomType(SellerMessages.DELIVERY_NOTIFICATION_TYPE, deliveryNotification)
                            .build();
            context.send(deliverySellerNotificationMsg);
        }

        if (shipment.getStatus() == Enums.ShipmentStatus.APPROVED) {
            shipment.setStatus(Enums.ShipmentStatus.DELIVERY_IN_PROGRESS);

            ShipmentNotification shipmentNotification = new ShipmentNotification(
                    shipment.getOrderId(),
                    shipment.getCustomerId(),
                    shipment.getStatus(),
                    now
            );

            Message shipmentOrderNotificationMsg =
                    MessageBuilder.forAddress(OrderFn.TYPE, String.valueOf(shipment.getCustomerId()))
                            .withCustomType(OrderMessages.SHIPMENT_NOTIFICATION_TYPE, shipmentNotification)
                            .build();
            context.send(shipmentOrderNotificationMsg);
        }

        if (shipment.getPackageCount() == countDelivered + sellerPackages.size()) {
            shipment.setStatus(Enums.ShipmentStatus.CONCLUDED);
            ShipmentNotification shipmentNotification = new ShipmentNotification(
                    shipment.getOrderId(),
                    shipment.getCustomerId(),
                    shipment.getStatus(),
                    now
            );

            // FIXME should notify all sellers included in the shipment [leave like this for now]
            Message shipmentSellerNotificationMsg =
                    MessageBuilder.forAddress(SellerFn.TYPE, String.valueOf(sellerPackages.get(0).getSellerId()) )
                            .withCustomType(OrderMessages.SHIPMENT_NOTIFICATION_TYPE, shipmentNotification)
                            .build();
            context.send(shipmentSellerNotificationMsg);

            Message shipmentNotificationMsg =
                    MessageBuilder.forAddress(OrderFn.TYPE, String.valueOf(shipment.getCustomerId()))
                            .withCustomType(OrderMessages.SHIPMENT_NOTIFICATION_TYPE, shipmentNotification)
                            .build();
            context.send(shipmentNotificationMsg);

            if (Constants.logging)
            {
            	try
            	{
            		String funcName = "ShipmentFn";
                	String key = shipment.getCustomerId() + "-" + shipment.getOrderId();
                	AbstractMap.SimpleEntry<Shipment, List<Package>> info = new AbstractMap.SimpleEntry<>(shipment, shipmentState.getPackages(shipmentId));
                	String value = objectMapper.writeValueAsString(info);
            		PostgresHelper.log(funcName, key, value);
            	}
            	catch (Exception e)
            	{
            		System.out.println(e.getMessage() + e.getStackTrace().toString());
            	}
            }
            
            // clean state
            shipmentState.getShipments().remove(shipmentId);
            shipmentState.getPackages().remove(shipmentId);
        }
    }
}

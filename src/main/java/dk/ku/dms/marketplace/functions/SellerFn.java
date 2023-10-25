package dk.ku.dms.marketplace.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.egress.TransactionMark;
import dk.ku.dms.marketplace.entities.*;
import dk.ku.dms.marketplace.messages.order.OrderMessages;
import dk.ku.dms.marketplace.messages.order.PaymentNotification;
import dk.ku.dms.marketplace.messages.order.ShipmentNotification;
import dk.ku.dms.marketplace.messages.payment.InvoiceIssued;
import dk.ku.dms.marketplace.messages.payment.PaymentMessages;
import dk.ku.dms.marketplace.messages.seller.DeliveryNotification;
import dk.ku.dms.marketplace.messages.seller.SellerMessages;
import dk.ku.dms.marketplace.states.SellerState;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import dk.ku.dms.marketplace.utils.PostgreHelper;

import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static dk.ku.dms.marketplace.states.SellerState.SELLER_STATE_TYPE;
import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class SellerFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(SellerFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/seller");

    public static final Type<Seller> ENTITY_STATE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "SellerEntityState"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, Seller.class));

    static final ValueSpec<Seller> SELLER_ENTITY_STATE = ValueSpec.named("SellerEntityState").withCustomType(ENTITY_STATE_TYPE);
    public static final ValueSpec<SellerState> SELLER_STATE = ValueSpec.named("SellerState").withCustomType(SELLER_STATE_TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpecs(SELLER_ENTITY_STATE, SELLER_STATE)
            .withSupplier(SellerFn::new)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // client ---> seller (init seller type)
            if (message.is(SellerMessages.SET_SELLER_TYPE)) {
                onSetSeller(context, message);
            }
            // order ---> seller
            else if (message.is(PaymentMessages.INVOICE_ISSUED_TYPE)) {
                onInvoiceIssued(context, message);
            }
            // driver ----> query dashboard
            else if (message.is(SellerMessages.QUERY_DASHBOARD_TYPE)) {
                onQueryDashboard(context, message);
            }
            else if (message.is(OrderMessages.PAYMENT_NOTIFICATION_TYPE)) {
                onPaymentNotification(context, message);
            }
            else if (message.is(OrderMessages.SHIPMENT_NOTIFICATION_TYPE)) {
                onShipmentNotification(context, message);
            }
            else if (message.is(SellerMessages.DELIVERY_NOTIFICATION_TYPE)) {
                onDeliveryNotification(context, message);
            }
            else {
                LOG.error("Message unknown: "+message);
            }
        } catch (Exception e) {
            LOG.error("SellerFn apply error !!!!!!!!!!!!!!!");
        }

        return context.done();
    }

    private SellerState getSellerState(Context context) {
        return context.storage().get(SELLER_STATE).orElse(SellerState.build());
    }

    private void onSetSeller(Context context, Message message) {
        Seller seller = message.as(SellerMessages.SET_SELLER_TYPE);
        context.storage().set(SELLER_ENTITY_STATE, seller);
    }

    private static String buildUniqueOrderIdentifier(InvoiceIssued invoiceIssued)
    {
        return String.valueOf(invoiceIssued.getCustomerCheckout().getCustomerId()) + '-' + invoiceIssued.getOrderId();
    }

    private static String buildUniqueOrderIdentifier(PaymentNotification paymentNotification)
    {
        return String.valueOf(paymentNotification.getCustomerId()) + '-' + paymentNotification.getOrderId();
    }

    private static String buildUniqueOrderIdentifier(ShipmentNotification shipmentNotification)
    {
        return String.valueOf(shipmentNotification.getCustomerId()) + '-' + shipmentNotification.getOrderId();
    }

    private static String buildUniqueOrderIdentifier(DeliveryNotification deliveryNotification)
    {
        return String.valueOf(deliveryNotification.getCustomerId()) + '-' + deliveryNotification.getOrderId();
    }

    private void onInvoiceIssued(Context context, Message message) {

        SellerState sellerState = getSellerState(context);
        InvoiceIssued invoiceIssued = message.as(PaymentMessages.INVOICE_ISSUED_TYPE);

        List<OrderItem> orderItems = invoiceIssued.getItems();
        int sellerId = Integer.parseInt(context.self().id());

        List<OrderEntry> list = new ArrayList<>(invoiceIssued.getItems().size());
        sellerState.getOrderEntries().put( buildUniqueOrderIdentifier(invoiceIssued), list);

        for (OrderItem orderItem : orderItems) {
            OrderEntry orderEntry = new OrderEntry(
                    invoiceIssued.getOrderId(),
                    sellerId,
                    // packageId =
                    orderItem.getProductId(),
                    orderItem.getProductName(),
                    orderItem.getQuantity(),
                    orderItem.getTotalAmount(),
                    orderItem.getTotalPrice(),
                    orderItem.getTotalAmount() + orderItem.getFreightValue(),
                    orderItem.getVoucher(),
                    orderItem.getFreightValue(),
                    // shipment_date
                    // delivery_date
                    orderItem.getUnitPrice(),
                    Enums.OrderStatus.INVOICED
                    // product_category = ? should come from product
            );
            list.add(orderEntry);
        }

        context.storage().set(SELLER_STATE, sellerState);
    }

    private void onQueryDashboard(Context context, Message message) throws JsonProcessingException {
        SellerState sellerState = getSellerState(context);

        List<OrderEntry> orderEntries = sellerState.getOrderEntries().entrySet().stream().flatMap(f->f.getValue().stream()).collect(Collectors.toList());
        TransactionMark mark;
        int sellerId = Integer.parseInt(context.self().id());
        String tid = message.as(SellerMessages.QUERY_DASHBOARD_TYPE).getTid();
        if(orderEntries.size() > 0) {
            OrderSellerView orderSellerView = new OrderSellerView(
                    sellerId,
                    sellerState.getOrderEntries().size(),
                    orderEntries.size(),
                    (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalAmount).sum(),
                    (float) orderEntries.stream().mapToDouble(OrderEntry::getFreightValue).sum(),
                    (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalIncentive).sum(),
                    (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalInvoice).sum(),
                    (float) orderEntries.stream().mapToDouble(OrderEntry::getTotalItems).sum()
            );

            SellerDashboard sellerDashboard = new SellerDashboard(
                    orderSellerView,
                    orderEntries
            );

            // must also account for the publishing of the result somewhere... not just the transaction mark payload
            byte[] res = objectMapper.writeValueAsBytes(sellerDashboard);

            mark = new TransactionMark(tid,
                    Enums.TransactionType.QUERY_DASHBOARD, sellerId, Enums.MarkStatus.SUCCESS, Arrays.toString(res));
        } else {
            mark = new TransactionMark(tid,
                    Enums.TransactionType.QUERY_DASHBOARD, sellerId, Enums.MarkStatus.SUCCESS, "seller");
        }

        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                        .build();

        context.send(egressMessage);
    }

    private void onPaymentNotification(Context context, Message message) {
        SellerState sellerState = getSellerState(context);
        PaymentNotification paymentNotification = message.as(OrderMessages.PAYMENT_NOTIFICATION_TYPE);

        String id = buildUniqueOrderIdentifier(paymentNotification);

        List<OrderEntry> entries = sellerState.getOrderEntries().get(id);
        for(OrderEntry entry : entries){
            entry.setOrderStatus(Enums.OrderStatus.PAYMENT_PROCESSED);
        }

        context.storage().set(SELLER_STATE, sellerState);
    }

    private void onShipmentNotification(Context context, Message message) {
        SellerState sellerState = getSellerState(context);
        ShipmentNotification shipmentNotification = message.as(OrderMessages.SHIPMENT_NOTIFICATION_TYPE);
        String id = buildUniqueOrderIdentifier(shipmentNotification);
        List<OrderEntry> entries = sellerState.getOrderEntries().get(id);

        for(OrderEntry entry : entries){
            if (shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.APPROVED) {
                entry.setOrderStatus(Enums.OrderStatus.READY_FOR_SHIPMENT);
                entry.setShipmentDate(shipmentNotification.getEventDate());
                entry.setDeliveryStatus(Enums.PackageStatus.ready_to_ship);
            } else if (shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.DELIVERY_IN_PROGRESS) {
                entry.setOrderStatus(Enums.OrderStatus.IN_TRANSIT);
                entry.setDeliveryStatus(Enums.PackageStatus.shipped);
            } else if (shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.CONCLUDED) {
                entry.setOrderStatus(Enums.OrderStatus.DELIVERED);
            }
        }
        
        if (shipmentNotification.getShipmentStatus() == Enums.ShipmentStatus.CONCLUDED)
        {
        	if (Constants.logging)
            {
            	try
            	{
            		String funcName = "SellerFn";
                	String key = id;
                	String value = objectMapper.writeValueAsString(entries);
            		PostgreHelper.log(funcName, key, value);
            	}
            	catch (Exception e)
            	{
            		System.out.println(e.getMessage() + e.getStackTrace().toString());
            	}
            }
        	
        	sellerState.getOrderEntries().remove(id);
        }

        context.storage().set(SELLER_STATE, sellerState);
    }

    private void onDeliveryNotification(Context context, Message message) {
        SellerState sellerState = getSellerState(context);
        DeliveryNotification deliveryNotification = message.as(SellerMessages.DELIVERY_NOTIFICATION_TYPE);
        String id = buildUniqueOrderIdentifier(deliveryNotification);

        Optional<OrderEntry> opEntry = sellerState.getOrderEntries().get(id).stream()
                .filter(p-> p.getProductId() == deliveryNotification.getProductId()).findFirst();

        if(opEntry.isPresent()){
            OrderEntry orderEntry = opEntry.get();
            orderEntry.setDeliveryStatus(deliveryNotification.getPackageStatus());
            orderEntry.setDeliveryDate(deliveryNotification.getDeliveryDate());
            orderEntry.setPackageId(deliveryNotification.getPackageId());

        }

        context.storage().set(SELLER_STATE, sellerState);
    }

}

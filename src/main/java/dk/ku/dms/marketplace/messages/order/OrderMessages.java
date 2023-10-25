package dk.ku.dms.marketplace.messages.order;

import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class OrderMessages {

    public static final Type<GetOrders> GET_ORDERS_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetOrders"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, GetOrders.class));

    public static final Type<AttemptReservationResponse> ATTEMPT_RESERVATION_RESPONSE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AttemptReservationResponse"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, AttemptReservationResponse.class));

    public static final Type<CheckoutRequest> CHECKOUT_REQUEST_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CheckoutRequest"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, CheckoutRequest.class));

    public static final Type<PaymentNotification> PAYMENT_NOTIFICATION_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentNotification"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, PaymentNotification.class));

    public static final Type<ShipmentNotification> SHIPMENT_NOTIFICATION_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ShipmentNotification"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, ShipmentNotification.class));

}

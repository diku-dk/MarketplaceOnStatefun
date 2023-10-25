package dk.ku.dms.marketplace.messages.shipment;

import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class ShipmentMessages {

    public static final Type<GetShipments> GET_SHIPMENTS_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetShipments"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, GetShipments.class));

    public static final Type<PaymentConfirmed> PAYMENT_CONFIRMED_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentConfirmed"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, PaymentConfirmed.class));

    public static final Type<UpdateShipment> UPDATE_SHIPMENT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdateShipment"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, UpdateShipment.class));

    public static final Type<UpdateShipmentAck> UPDATE_SHIPMENT_ACK_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdateShipmentAck"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, UpdateShipmentAck.class));

}

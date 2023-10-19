package dk.ku.dms.marketplace.messages.shipment;

import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class ShipmentMessages {

    public static final Type<PaymentConfirmed> PAYMENT_CONFIRMED_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentConfirmed"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, PaymentConfirmed.class));

}

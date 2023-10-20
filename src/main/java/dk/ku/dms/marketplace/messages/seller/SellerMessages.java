package dk.ku.dms.marketplace.messages.seller;

import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class SellerMessages {

    public static final Type<DeliveryNotification> DELIVERY_NOTIFICATION_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "DeliveryNotification"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, DeliveryNotification.class));

}

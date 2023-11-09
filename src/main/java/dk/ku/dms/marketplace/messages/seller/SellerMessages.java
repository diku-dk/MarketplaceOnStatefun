package dk.ku.dms.marketplace.messages.seller;

import dk.ku.dms.marketplace.entities.Seller;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class SellerMessages {

    public static final Type<Seller> SET_SELLER_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "SetSeller"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, Seller.class));
    public static final Type<DeliveryNotification> DELIVERY_NOTIFICATION_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "DeliveryNotification"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, DeliveryNotification.class));

    public static final Type<QueryDashboard> QUERY_DASHBOARD_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "QueryDashboard"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, QueryDashboard.class));

    public static final Type<ErrorCollect> ERROR_COLLECT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ErrorCollect"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, ErrorCollect.class));
}

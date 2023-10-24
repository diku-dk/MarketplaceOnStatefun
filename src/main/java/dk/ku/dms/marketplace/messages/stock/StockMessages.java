package dk.ku.dms.marketplace.messages.stock;

import dk.ku.dms.marketplace.entities.StockItem;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class StockMessages {

    public static final Type<AttemptReservationEvent> ATTEMPT_RESERVATION_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AttemptReservation"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, AttemptReservationEvent.class));

    public static final Type<PaymentStockEvent> PAYMENT_STOCK_EVENT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentStockEvent"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, PaymentStockEvent.class));

    public static final Type<ProductUpdatedEvent> PRODUCT_UPDATED_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ProductUpdated"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, ProductUpdatedEvent.class));

    public static final Type<StockItem> SET_STOCK_ITEM_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "SetStockItem"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, StockItem.class));

    public static final Type<GetStockItem> GET_STOCK_ITEM_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetStockItem"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, GetStockItem.class));

}

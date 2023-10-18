package dk.ku.dms.marketplace.messages.stock;

import dk.ku.dms.marketplace.entities.StockItem;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public class StockMessages {

    public static final Type<AttemptReservationEvent> ATTEMPT_RESERVATION_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AttemptReservation"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, AttemptReservationEvent.class));

    public static final Type<PaymentStockEvent> PAYMENT_STOCK_EVENT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentStockEvent"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, PaymentStockEvent.class));

    public static final Type<ProductUpdatedEvent> PRODUCT_UPDATED_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ProductUpdated"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ProductUpdatedEvent.class));

    public static final Type<StockItem> SET_STOCK_ITEM_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "SetStockItem"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, StockItem.class));

}

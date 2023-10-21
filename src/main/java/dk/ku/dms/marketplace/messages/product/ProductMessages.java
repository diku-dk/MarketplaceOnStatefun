package dk.ku.dms.marketplace.messages.product;

import dk.ku.dms.marketplace.entities.Product;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class ProductMessages {

    public static final Type<Product> UPSERT_PRODUCT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpsertProduct"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Product.class));

    public static final Type<UpdatePrice> UPDATE_PRICE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdatePrice"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, UpdatePrice.class));


}

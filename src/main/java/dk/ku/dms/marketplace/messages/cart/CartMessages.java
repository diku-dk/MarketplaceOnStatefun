package dk.ku.dms.marketplace.messages.cart;

import com.fasterxml.jackson.annotation.JsonCreator;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.CustomerCheckout;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class CartMessages {

    public static final Type<Seal> SEAL_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "Seal"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Seal.class));

    public static final Type<CartItem> ADD_CART_ITEM_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AddCartItem"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CartItem.class));

    public static final Type<CustomerCheckout> CUSTOMER_CHECKOUT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CustomerCheckout"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CustomerCheckout.class));

    public static final class Seal {

        @JsonCreator
        public Seal() {
        }

        @Override
        public String toString() {
            return "Seal{}";
        }
    }
}

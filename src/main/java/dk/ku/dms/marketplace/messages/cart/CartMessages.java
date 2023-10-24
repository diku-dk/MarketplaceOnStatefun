package dk.ku.dms.marketplace.messages.cart;

import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class CartMessages {

    public static final Type<Seal> SEAL_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "Seal"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, Seal.class));

    public static final Type<CartItem> ADD_CART_ITEM_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AddCartItem"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, CartItem.class));

    public static final Type<CustomerCheckout> CUSTOMER_CHECKOUT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CustomerCheckout"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, CustomerCheckout.class));

    public static final Type<GetCart> GET_CART_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetCart"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, GetCart.class));

}

package dk.ku.dms.marketplace.types.MsgToCartFn;

import dk.ku.dms.marketplace.common.Entity.CustomerCheckout;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Getter
@Setter
public class CustomerSession {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<CustomerSession> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CustomerSession"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CustomerSession.class));

    @JsonProperty("Type")
    private String type;
    @JsonProperty("CartItem")
    private AddToCart addToCart;
    @JsonProperty("CustomerCheckout")
    private CustomerCheckout customerCheckout;
}
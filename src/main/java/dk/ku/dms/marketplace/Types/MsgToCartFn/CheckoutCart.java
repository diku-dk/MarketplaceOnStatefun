package dk.ku.dms.marketplace.Types.MsgToCartFn;

import dk.ku.dms.marketplace.common.Entity.CustomerCheckout;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;


@Getter
@Setter
public class CheckoutCart {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<CheckoutCart> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CheckoutCart"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CheckoutCart.class));

    @JsonProperty("customerCheckout")
    private CustomerCheckout customerCheckout;

    public CheckoutCart() {
    }

    @JsonCreator
    public CheckoutCart(@JsonProperty("customerCheckout") CustomerCheckout customerCheckout) {
        this.customerCheckout = customerCheckout;
    }
}

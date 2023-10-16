package dk.ku.dms.marketplace.Types.State;

import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.common.Entity.Checkout;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;
import java.util.Map;

//checkout info
@Getter
@Setter
public class CustomerCheckoutInfoState {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<CustomerCheckoutInfoState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "OrderTempInfoState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CustomerCheckoutInfoState.class));

    @JsonProperty("checkout")
    private Map<Integer, Checkout> checkout = new HashMap<>();

    @JsonIgnore
    public void addCheckout(int customerId, Checkout checkout) {
        this.checkout.put(customerId, checkout);
    }

    @JsonIgnore
    public void removeSingleCheckout(int customerId) {
        this.checkout.remove(customerId);
    }

    @JsonIgnore
    public Checkout getSingleCheckout(int customerId) {
        return this.checkout.get(customerId);
    }
}

package dk.ku.dms.marketplace.types.MsgToPaymentFn;

import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Setter
@Getter
@ToString
public class FailOrder {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<FailOrder> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "FailOrder"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, FailOrder.class));

    @JsonProperty("customerId")
    private int customerId;

    @JsonProperty("orderId")
    private int orderId;

    public FailOrder() {
    }

    @JsonCreator
    public FailOrder(@JsonProperty("customerId") int customerId,
                     @JsonProperty("orderId") int orderId) {
        this.customerId = customerId;
        this.orderId = orderId;
    }
}

package dk.ku.dms.marketplace.messages.MsgToPaymentFn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public class FailOrder {

    

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

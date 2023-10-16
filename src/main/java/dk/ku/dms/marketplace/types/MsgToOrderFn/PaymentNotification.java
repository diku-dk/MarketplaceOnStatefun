package dk.ku.dms.marketplace.types.MsgToOrderFn;

import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.constants.Enums;
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
public class PaymentNotification {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<PaymentNotification> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentNotification"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, PaymentNotification.class));

    @JsonProperty("orderId")
    private int orderId;

    @JsonProperty("orderStatus")
    private Enums.OrderStatus orderStatus;

    @JsonCreator
    public PaymentNotification(@JsonProperty("orderId") int orderId,
                            @JsonProperty("orderStatus") Enums.OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }
}

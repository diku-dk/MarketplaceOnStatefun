package dk.ku.dms.marketplace.Types.State;

import dk.ku.dms.marketplace.common.Entity.OrderPayment;
import dk.ku.dms.marketplace.constants.Constants;
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

@Getter
@Setter
public class PaymentState {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<PaymentState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, PaymentState.class));

    @JsonProperty("orderPayments")
    private Map<String, OrderPayment> orderPayments = new HashMap<>();

    @JsonIgnore
    public void addOrderPayment(String uniqueOrderId, OrderPayment orderPayment) {
        orderPayments.put(uniqueOrderId, orderPayment);
    }
}

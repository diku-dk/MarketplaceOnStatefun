package dk.ku.dms.marketplace.types.MsgToStock;

import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.constants.Enums;
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
public class ConfirmStockEvent {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ConfirmStockEvent> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "PaymentResv"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ConfirmStockEvent.class));

    @JsonProperty("productId")
    int productId;

    @JsonProperty("quantity")
    int quantity;

    @JsonProperty("uniqueOrderId")
    String uniqueOrderId;

    @JsonProperty("OrderStatus")
    private Enums.OrderStatus OrderStatus;

    @JsonCreator
    public ConfirmStockEvent(@JsonProperty("uniqueOrderId") String uniqueOrderId,
                             @JsonProperty("productId") int productId,
                             @JsonProperty("quantity") int quantity,
                             @JsonProperty("OrderStatus") Enums.OrderStatus OrderStatus

    ) {
        this.uniqueOrderId = uniqueOrderId;
        this.productId = productId;
        this.quantity = quantity;
        this.OrderStatus = OrderStatus;
    }
}

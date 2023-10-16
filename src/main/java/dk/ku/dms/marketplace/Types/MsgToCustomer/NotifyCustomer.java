package dk.ku.dms.marketplace.Types.MsgToCustomer;

import dk.ku.dms.marketplace.common.Entity.Order;
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

@Setter
@Getter
public class NotifyCustomer {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<NotifyCustomer> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "NotifyCustomer"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, NotifyCustomer.class));

    @JsonProperty("customerId") private int customerId;
    @JsonProperty("order") private Order order;
    @JsonProperty("notifyType") private Enums.NotificationType notifyType;
    @JsonProperty("numDeliveries") private int numDeliveries = 0;

    @JsonCreator
    public NotifyCustomer(@JsonProperty("customerId") int customerId,
                          @JsonProperty("order") Order order,
                          @JsonProperty("notifyType") Enums.NotificationType notifyType) {
        this.customerId = customerId;
        this.order = order;
        this.notifyType = notifyType;
    }
}

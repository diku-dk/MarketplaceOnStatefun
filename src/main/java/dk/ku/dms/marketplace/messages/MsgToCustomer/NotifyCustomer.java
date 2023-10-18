package dk.ku.dms.marketplace.messages.MsgToCustomer;

import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public class NotifyCustomer {
    

    public static final Type<NotifyCustomer> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "NotifyCustomer"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, NotifyCustomer.class));

    @JsonProperty("customerId") private int customerId;
    @JsonProperty("notifyType") private Enums.NotificationType notifyType;

    @JsonCreator
    public NotifyCustomer(@JsonProperty("customerId") int customerId,
                          @JsonProperty("notifyType") Enums.NotificationType notifyType) {
        this.customerId = customerId;
        this.notifyType = notifyType;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Enums.NotificationType getNotifyType() {

        return notifyType;
    }
}

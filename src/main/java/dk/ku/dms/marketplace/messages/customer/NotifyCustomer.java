package dk.ku.dms.marketplace.messages.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
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
    @JsonProperty("notifyType") private Enums.CustomerNotificationType notifyType;

    @JsonCreator
    public NotifyCustomer(@JsonProperty("notifyType") Enums.CustomerNotificationType notifyType) {
        this.notifyType = notifyType;
    }

    public Enums.CustomerNotificationType getNotifyType() {

        return notifyType;
    }
}

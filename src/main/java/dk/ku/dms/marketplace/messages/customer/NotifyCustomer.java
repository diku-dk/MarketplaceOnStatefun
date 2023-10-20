package dk.ku.dms.marketplace.messages.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Enums;

public final class NotifyCustomer {
    @JsonProperty("notifyType")
    private final Enums.CustomerNotificationType notifyType;

    @JsonCreator
    public NotifyCustomer(@JsonProperty("notifyType") Enums.CustomerNotificationType notifyType) {
        this.notifyType = notifyType;
    }

    public Enums.CustomerNotificationType getNotifyType() {
        return notifyType;
    }
}

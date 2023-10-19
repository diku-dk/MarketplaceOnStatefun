package dk.ku.dms.marketplace.messages.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Enums;

public final class PaymentNotification {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("status")
    private final Enums.PaymentStatus status;

    @JsonCreator
    public PaymentNotification(@JsonProperty("orderId") int orderId,
                               @JsonProperty("status") Enums.PaymentStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    @JsonIgnore
    public int getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public Enums.PaymentStatus getStatus() {
        return status;
    }
}

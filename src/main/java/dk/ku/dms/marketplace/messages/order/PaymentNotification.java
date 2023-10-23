package dk.ku.dms.marketplace.messages.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Enums;

public final class PaymentNotification {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("customerId")
    private final int customerId;

    @JsonProperty("status")
    private final Enums.PaymentStatus status;

    @JsonCreator
    public PaymentNotification(@JsonProperty("orderId") int orderId,
                               @JsonProperty("customerId") int customerId,
                               @JsonProperty("status") Enums.PaymentStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
    }

    @JsonIgnore
    public int getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public int getCustomerId() {
        return customerId;
    }

    @JsonIgnore
    public Enums.PaymentStatus getStatus() {
        return status;
    }
}

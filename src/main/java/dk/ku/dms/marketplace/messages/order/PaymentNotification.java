package dk.ku.dms.marketplace.messages.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Enums;

public final class PaymentNotification {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("orderStatus")
    private final Enums.PaymentStatus status;

    @JsonCreator
    public PaymentNotification(@JsonProperty("orderId") int orderId,
                            @JsonProperty("status") Enums.PaymentStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public Enums.PaymentStatus getStatus() {
        return status;
    }
}

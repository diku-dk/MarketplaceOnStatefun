package dk.ku.dms.marketplace.messages.stock;

import dk.ku.dms.marketplace.utils.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PaymentStockEvent {

    @JsonProperty("quantity")
    private final int quantity;

    @JsonProperty("status")
    private final Enums.PaymentStatus status;

    @JsonCreator
    public PaymentStockEvent(@JsonProperty("quantity") int quantity,
                             @JsonProperty("OrderStatus") Enums.PaymentStatus status

    ) {
        this.quantity = quantity;
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public Enums.PaymentStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "PaymentStockEvent{" +
                "quantity=" + quantity +
                ", status=" + status +
                '}';
    }
}

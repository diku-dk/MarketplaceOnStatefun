package dk.ku.dms.marketplace.messages.stock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.entities.CartItem;

public final class AttemptReservationEvent {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("cartItem")
    private final CartItem cartItem;

    @JsonCreator
    public AttemptReservationEvent(@JsonProperty("orderId") int orderId,
                               @JsonProperty("cartItem") CartItem cartItem)
    {
        this.orderId = orderId;
        this.cartItem = cartItem;
    }

    @JsonIgnore
    public int getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public CartItem getCartItem() {
        return cartItem;
    }

    @Override
    public String toString() {
        return "AttemptReservationEvent{" +
                "orderId=" + orderId +
                ", cartItem=" + cartItem.toString() +
                '}';
    }
}

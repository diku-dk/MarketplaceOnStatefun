package dk.ku.dms.marketplace.messages.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Enums;

public final class AttemptReservationResponse {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("sellerId")
    private final int sellerId;

    @JsonProperty("productId")
    private final int productId;

    @JsonProperty("status")
    private final Enums.ItemStatus status;

    @JsonProperty("idx")
    private final int idx;

    @JsonCreator
    public AttemptReservationResponse(@JsonProperty("orderId") int orderId,
                                      @JsonProperty("sellerId") int sellerId,
                                      @JsonProperty("productId") int productId,
                                      @JsonProperty("status") Enums.ItemStatus status,
                                      @JsonProperty("idx") int idx)
    {
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.status = status;
        this.idx = idx;
    }

    @JsonIgnore
    public int getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public int getSellerId() {
        return sellerId;
    }

    @JsonIgnore
    public int getProductId() {
        return productId;
    }

    @JsonIgnore
    public Enums.ItemStatus getStatus() {
        return status;
    }

    @JsonIgnore
    public int getIdx() {
        return idx;
    }

}

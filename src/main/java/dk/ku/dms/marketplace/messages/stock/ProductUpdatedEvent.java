package dk.ku.dms.marketplace.messages.stock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ProductUpdatedEvent {

    @JsonProperty("sellerId")
    private final int sellerId;

    @JsonProperty("productId")
    private final int productId;

    @JsonProperty("instanceId")
    private final String instanceId;

    @JsonCreator
    public ProductUpdatedEvent(@JsonProperty("sellerId") int sellerId,
                               @JsonProperty("productId") int productId,
                               @JsonProperty("instanceId") String instanceId)
    {
        this.sellerId = sellerId;
        this.productId = productId;
        this.instanceId = instanceId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public int getProductId() {
        return productId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return "ProductUpdated{" +
                "sellerId=" + sellerId +
                ", productId=" + productId +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}


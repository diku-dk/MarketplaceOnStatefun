package dk.ku.dms.marketplace.messages.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdatePrice {

    @JsonProperty("sellerId")
    private final int sellerId;
    @JsonProperty("productId")
    private final int productId;
    @JsonProperty("price")
    private final float price;
    @JsonProperty("instanceId")
    private final String instanceId;

    @JsonCreator
    public UpdatePrice(@JsonProperty("sellerId") int sellerId,
                       @JsonProperty("productId") int productId,
                       @JsonProperty("price") float price,
                       @JsonProperty("instanceId") String instanceId) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.price = price;
        this.instanceId = instanceId;
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
    public float getPrice() {
        return price;
    }

    @JsonIgnore
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return "UpdatePrice{" +
                "sellerId=" + sellerId +
                ", productId=" + productId +
                ", price=" + price +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}

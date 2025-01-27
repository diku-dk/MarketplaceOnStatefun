package dk.ku.dms.marketplace.messages.seller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Enums;

import java.time.LocalDateTime;

public final class DeliveryNotification {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("customerId")
    private final int customerId;

    @JsonProperty("packageId")
    private final int packageId;

    @JsonProperty("sellerId")
    private final int sellerId;

    @JsonProperty("productId")
    private final int productId;

    @JsonProperty("productName")
    private final String productName;

    @JsonProperty("status")
    private final Enums.PackageStatus packageStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("deliveryDate")
    private final LocalDateTime deliveryDate;

    @JsonCreator
    public DeliveryNotification(@JsonProperty("customerID") int customerID,
                                @JsonProperty("orderId") int orderId,
                                @JsonProperty("packageId") int packageId,
                                @JsonProperty("sellerId") int sellerId,
                                @JsonProperty("productId") int productId,
                                @JsonProperty("productName") String productName,
                                @JsonProperty("status") Enums.PackageStatus packageStatus,
                                @JsonProperty("deliveryDate") LocalDateTime deliveryDate) {
        this.orderId = orderId;
        this.customerId = customerID;
        this.sellerId = sellerId;
        this.packageId = packageId;
        this.productId = productId;
        this.productName = productName;
        this.packageStatus = packageStatus;
        this.deliveryDate = deliveryDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getPackageId() {
        return packageId;
    }

    public int getProductId() {
        return productId;
    }

    public Enums.PackageStatus getPackageStatus() {
        return packageStatus;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
}


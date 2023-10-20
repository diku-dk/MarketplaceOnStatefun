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
    private int orderId;

    @JsonProperty("customerId")
    private int customerId;

    @JsonProperty("packageId")
    private int packageId;

    @JsonProperty("sellerId")
    private int sellerId;

    @JsonProperty("productId")
    private int productId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("Status")
    private Enums.PackageStatus packageStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("eventDate")
    private LocalDateTime deliveryDate;

    @JsonCreator
    public DeliveryNotification(@JsonProperty("customerID") int customerID,
                                @JsonProperty("orderId") int orderId,
                                @JsonProperty("packageId") int packageId,
                                @JsonProperty("sellerId") int sellerId,
                                @JsonProperty("productID") int productId,
                                @JsonProperty("productName") String productName,
                                @JsonProperty("Status") Enums.PackageStatus packageStatus,
                                @JsonProperty("eventDate") LocalDateTime deliveryDate) {
        this.orderId = orderId;
        this.customerId = customerID;
        this.sellerId = sellerId;
        this.packageId = packageId;
        this.productId = productId;
        this.productName = productName;
        this.packageStatus = packageStatus;
        this.deliveryDate = deliveryDate;
    }
}


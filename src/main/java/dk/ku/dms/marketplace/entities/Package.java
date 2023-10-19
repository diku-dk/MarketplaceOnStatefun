package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Enums;

import java.time.LocalDateTime;

public final class Package {

    @JsonProperty("packageId")
    private final int packageId;
    @JsonProperty("orderId")
    private final int orderId;
    @JsonProperty("shipmentId")
    private final int shipmentId;
    // FK
    // product identification
    @JsonProperty("sellerId")
    private final int sellerId;
    @JsonProperty("productId")
    private final int productId;
    @JsonProperty("freightValue")
    private final float freightValue;
    @JsonProperty("quantity")
    private final int quantity;
    @JsonProperty("productName")
    private final String productName;
    @JsonProperty("packageStatus")
    private Enums.PackageStatus packageStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivered_time")
    private LocalDateTime delivered_time;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("shipping_date")
    private final LocalDateTime shipping_date;

    @JsonCreator
    public Package(
            @JsonProperty("shipmentId") int shipmentId,
            @JsonProperty("orderId") int orderId,
            @JsonProperty("packageId") int packageId,
            @JsonProperty("sellerId") int sellerId,
            @JsonProperty("productId") int productId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("freightValue") float freightValue,
            @JsonProperty("productName") String productName,
            @JsonProperty("shipping_date") LocalDateTime shipping_date,
            @JsonProperty("packageStatus") Enums.PackageStatus packageStatus) {
        this.packageId = packageId;
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.quantity = quantity;
        this.freightValue = freightValue;
        this.productName = productName;
        this.shipping_date = shipping_date;
        this.packageStatus = packageStatus;
    }

    public int getSellerId() {
        return this.sellerId;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public Enums.PackageStatus getPackageStatus() {
        return packageStatus;
    }
}

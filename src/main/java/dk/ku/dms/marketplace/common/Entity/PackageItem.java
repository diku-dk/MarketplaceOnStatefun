package dk.ku.dms.marketplace.common.Entity;

import dk.ku.dms.marketplace.constants.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class PackageItem {
    @JsonProperty("packageId") private int packageId;
    @JsonProperty("orderId") private int orderId;
    @JsonProperty("shipmentId") private int shipmentId;

    // FK
    // product identification
    @JsonProperty("sellerId")  public int sellerId;
    @JsonProperty("productId") public int productId;
    @JsonProperty("freightValue") public float freightValue;
    @JsonProperty("quantity") public int quantity;
    @JsonProperty("productName") public String productName;
    @JsonProperty("packageStatus") public Enums.PackageStatus packageStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivered_time")
    private LocalDateTime delivered_time;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("shipping_date")
    private LocalDateTime shipping_date;

    @JsonCreator
    public PackageItem(
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

}

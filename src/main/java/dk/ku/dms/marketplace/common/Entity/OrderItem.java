package dk.ku.dms.marketplace.common.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderItem {
    @JsonProperty("orderId") private int orderId;
    @JsonProperty("orderItemId") private int orderItemId;
    @JsonProperty("productId") private int productId;
    @JsonProperty("productName") private String productName;
    @JsonProperty("sellerId") private int sellerId;
    @JsonProperty("unitPrice") private float unitPrice;
    @JsonProperty("freightValue") private float freightValue;
    @JsonProperty("quantity") private int quantity;
    @JsonProperty("totalPrice") private float totalPrice; // without freight
    @JsonProperty("totalAmount") private float totalAmount;
    @JsonProperty("vouchers") private float vouchers;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("shippingLimitDate") private LocalDateTime shippingLimitDate;

    public OrderItem() {
    }

    @JsonCreator
    public OrderItem(@JsonProperty("orderId") int orderId,
                     @JsonProperty("orderItemId") int orderItemId,
                     @JsonProperty("productId") int productId,
                     @JsonProperty("productName") String productName,
                     @JsonProperty("sellerId") int sellerId,
                     @JsonProperty("unitPrice") float unitPrice,
                     @JsonProperty("freightValue") float freightValue,
                     @JsonProperty("quantity") int quantity,
                     @JsonProperty("totalPrice") float totalPrice,
                     @JsonProperty("totalAmount") float totalAmount,
                     @JsonProperty("shippingLimitDate") LocalDateTime shippingLimitDate) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.sellerId = sellerId;
        this.unitPrice = unitPrice;
        this.freightValue = freightValue;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.totalAmount = totalAmount;
        this.shippingLimitDate = shippingLimitDate;
    }
}

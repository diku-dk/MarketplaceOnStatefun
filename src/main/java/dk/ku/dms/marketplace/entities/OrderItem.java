package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public final class OrderItem {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("orderItemId")
    private final int orderItemId;

    @JsonProperty("productId")
    private final int productId;

    @JsonProperty("productName")
    private final String productName;

    @JsonProperty("sellerId")
    private final int sellerId;

    @JsonProperty("unitPrice")
    private final float unitPrice;

    @JsonProperty("freightValue")
    private final float freightValue;

    @JsonProperty("quantity")
    private final int quantity;

    @JsonProperty("totalPrice")
    private final float totalPrice; // without freight

    @JsonProperty("totalAmount")
    private final float totalAmount;

    @JsonProperty("voucher")
    private float voucher;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("shippingLimitDate")
    private final LocalDateTime shippingLimitDate;

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
                     @JsonProperty("voucher") float voucher,
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
        this.voucher = voucher;
    }

    @JsonIgnore
    public float getFreightValue() {
    	return freightValue;
    }

    @JsonIgnore
	public int getOrderId() {
		return orderId;
	}

    @JsonIgnore
	public int getOrderItemId() {
		return orderItemId;
	}

    @JsonIgnore
	public int getProductId() {
		return productId;
	}

    @JsonIgnore
	public String getProductName() {
		return productName;
	}

    @JsonIgnore
	public float getUnitPrice() {
		return unitPrice;
	}

    @JsonIgnore
	public int getQuantity() {
		return quantity;
	}

    @JsonIgnore
	public float getTotalPrice() {
		return totalPrice;
	}

    @JsonIgnore
	public float getTotalAmount() {
		return totalAmount;
	}

    @JsonIgnore
	public float getVoucher() {
		return voucher;
	}

    @JsonIgnore
	public LocalDateTime getShippingLimitDate() {
		return shippingLimitDate;
	}

    @JsonIgnore
	public int getSellerId() {
		return sellerId;
	}
}

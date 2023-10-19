package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;


public final class OrderItem {
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

    @JsonProperty("voucher") private float voucher;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("shippingLimitDate") private LocalDateTime shippingLimitDate;

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

	public void setVouchers(float voucher) {
        this.voucher = voucher;
    }

    public int getSellerId() {
        return sellerId;
    }
    
    public float getFreightValue() {
    	return freightValue;
    }

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(int orderItemId) {
		this.orderItemId = orderItemId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public float getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}

	public float getVoucher() {
		return voucher;
	}

	public void setVoucher(float voucher) {
		this.voucher = voucher;
	}

	public LocalDateTime getShippingLimitDate() {
		return shippingLimitDate;
	}

	public void setShippingLimitDate(LocalDateTime shippingLimitDate) {
		this.shippingLimitDate = shippingLimitDate;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public void setFreightValue(float freightValue) {
		this.freightValue = freightValue;
	}
    
    
}

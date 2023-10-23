package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Enums;

import java.time.LocalDateTime;

public final class OrderEntry {

    @JsonProperty("seller_id")
    private final int seller_id;
    @JsonProperty("order_id")
    private final int order_id;

    @JsonProperty("package_id")
    private int package_id;

    @JsonProperty("product_id")
    private final int product_id;
    @JsonProperty("product_name")
    private final String product_name;

    @JsonProperty("product_category")
    private final String product_category = "";
    @JsonProperty("unit_price")
    private final float unit_price;
    @JsonProperty("quantity")
    private final int quantity;
    @JsonProperty("totalItems")
    private final float totalItems;
    @JsonProperty("totalAmount")
    private final float totalAmount;

    @JsonProperty("totalInvoice")
    private final float totalInvoice;
    @JsonProperty("totalIncentive")
    private final float totalIncentive;
    @JsonProperty("freight_value")
    private final float freight_value;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("shipment_date")
    private LocalDateTime shipment_date;
//
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivery_date")
    private LocalDateTime delivery_date;

    @JsonProperty("order_status")
    private Enums.OrderStatus order_status;

    @JsonProperty("delivery_status")
    private Enums.PackageStatus delivery_status;

    @JsonCreator
    public OrderEntry(
            @JsonProperty("order_id") int order_id,
            @JsonProperty("seller_id") int seller_id,
//            @JsonProperty("package_id") long package_id,
            @JsonProperty("product_id") int product_id,
            @JsonProperty("product_name") String product_name,

            @JsonProperty("quantity") int quantity,
            @JsonProperty("totalAmount") float totalAmount,
            @JsonProperty("totalInvoice") float totalInvoice,
            @JsonProperty("totalItems") float totalItems,
            @JsonProperty("totalIncentive") float totalIncentive,
            @JsonProperty("freight_value") float freight_value,

            @JsonProperty("unit_price") float unit_price,
            @JsonProperty("order_status") Enums.OrderStatus order_status

    ) {
        this.seller_id = seller_id;
        this.order_id = order_id;
//        this.package_id = package_id;
        this.product_id = product_id;
        this.product_name = product_name;
//        this.product_category = product_category;
        this.unit_price = unit_price;
        this.quantity = quantity;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.totalInvoice = totalInvoice;
        this.totalIncentive = totalIncentive;
        this.freight_value = freight_value;
//        this.shipment_date = shipment_date;
//        this.delivery_date = delivery_date;
        this.order_status = order_status;
//        this.delivery_status = delivery_status;
    }

    public int getSellerId() {
        return seller_id;
    }

    public int getOrderId() {
        return order_id;
    }

    public int getPackageId() {
        return package_id;
    }

    public int getProductId() {
        return product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public String getProductCategory() {
        return product_category;
    }

    public float getUnitPrice() {
        return unit_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getTotalItems() {
        return totalItems;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public float getTotalInvoice() {
        return totalInvoice;
    }

    public float getTotalIncentive() {
        return totalIncentive;
    }

    public float getFreightValue() {
        return freight_value;
    }

    public LocalDateTime getShipmentDate() {
        return shipment_date;
    }

    public LocalDateTime getDeliveryDate() {
        return delivery_date;
    }

    public Enums.OrderStatus getOrderStatus() {
        return order_status;
    }

    public Enums.PackageStatus getDeliveryStatus() {
        return delivery_status;
    }

    public void setPackageId(int package_id) {
        this.package_id = package_id;
    }

    public void setShipmentDate(LocalDateTime shipment_date) {
        this.shipment_date = shipment_date;
    }

    public void setDeliveryDate(LocalDateTime delivery_date) {
        this.delivery_date = delivery_date;
    }

    public void setOrderStatus(Enums.OrderStatus order_status) {
        this.order_status = order_status;
    }

    public void setDeliveryStatus(Enums.PackageStatus delivery_status) {
        this.delivery_status = delivery_status;
    }
}

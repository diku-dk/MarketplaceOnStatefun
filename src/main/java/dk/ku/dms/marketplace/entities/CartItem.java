package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CartItem {

    @JsonProperty("SellerId")
    private final int sellerId;

    @JsonProperty("ProductId")
    private final int productId;

    @JsonProperty("ProductName")
    private final String productName;

    @JsonProperty("UnitPrice")
    private final float unitPrice;

    @JsonProperty("FreightValue")
    private final float freightValue;

    @JsonProperty("Quantity")
    private final int quantity;

    @JsonProperty("Voucher")
    private final float voucher;

    @JsonProperty("Version")
    private final String version;

    @JsonCreator
    public CartItem(@JsonProperty("SellerId") int sellerId,
                    @JsonProperty("ProductId") int productId,
                    @JsonProperty("ProductName") String productName,
                    @JsonProperty("UnitPrice") float unitPrice,
                    @JsonProperty("FreightValue") float freightValue,
                    @JsonProperty("Quantity") int quantity,
                    @JsonProperty("Voucher") float voucher,
                    @JsonProperty("Version") String version
                      ) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.freightValue = freightValue;
        this.quantity = quantity;
        this.voucher = voucher;
        this.version = version;
    }

    @JsonIgnore
    public int getProductId() {
        return this.productId;
    }

    @JsonIgnore
    public float getFreightValue() {
        return freightValue;
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
    public float getVoucher() {
        return this.voucher;
    }

    @JsonIgnore
    public String getProductName() {
        return productName;
    }

    @JsonIgnore
    public int getSellerId() {
        return sellerId;
    }

    @JsonIgnore
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "sellerId=" + sellerId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unitPrice=" + unitPrice +
                ", freightValue=" + freightValue +
                ", quantity=" + quantity +
                ", voucher=" + voucher +
                ", version='" + version + '\'' +
                '}';
    }
}

package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CartItem {

    private final int sellerId;
    private final int productId;

    private final String productName;
    private final float unitPrice;
    private final float freightValue;

    private final int quantity;
    private final float voucher;
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

    public int getProductId() {
        return this.productId;
    }

    public float getFreightValue() {
        return freightValue;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getVoucher() {
        return this.voucher;
    }

    public String getProductName() {
        return productName;
    }

    public int getSellerId() {
        return sellerId;
    }

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

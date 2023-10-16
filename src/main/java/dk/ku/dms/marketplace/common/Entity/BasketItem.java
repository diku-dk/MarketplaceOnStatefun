package dk.ku.dms.marketplace.common.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity not present in olist original data set
 * Thus, the basket item entity is derived from
 * the needs to process the order.
 * This could include the freight value...
 */


@Setter
@Getter
@ToString
public class BasketItem {

    @JsonProperty("SellerId") private int sellerId;
    @JsonProperty("ProductId") private int productId;

    @JsonProperty("ProductName") private String productName;
    @JsonProperty("UnitPrice") private float unitPrice;
    @JsonProperty("FreightValue") private float freightValue; //运费

    @JsonProperty("Quantity") private int quantity;
    @JsonProperty("Voucher") private float vouchers;
    @JsonProperty("Version") private int version;

    public BasketItem() {
    }

    @JsonCreator
    public BasketItem(@JsonProperty("SellerId") int sellerId,
                      @JsonProperty("ProductId") int productId,
                      @JsonProperty("ProductName") String productName,
                      @JsonProperty("UnitPrice") float unitPrice,
                      @JsonProperty("FreightValue") float freightValue,
                      @JsonProperty("Quantity") int quantity,
                      @JsonProperty("Voucher") float vouchers,
                      @JsonProperty("Version") int version
                      ) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.freightValue = freightValue;
        this.quantity = quantity;
        this.vouchers = vouchers;
        this.version = version;
    }

}

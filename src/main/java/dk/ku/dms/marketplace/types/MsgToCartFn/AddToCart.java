package dk.ku.dms.marketplace.types.MsgToCartFn;

import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;


@Getter
@Setter
public class AddToCart {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<AddToCart> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AddToCart"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, AddToCart.class));

//    @JsonProperty("customerId")
//    private Long customerId;

    @JsonProperty("SellerId") private int sellerId;
    @JsonProperty("ProductId") private int productId;

    @JsonProperty("ProductName") private String productName;
    @JsonProperty("UnitPrice") private float unitPrice;
    @JsonProperty("FreightValue") private float freightValue; //运费

    @JsonProperty("Quantity") private int quantity;
    @JsonProperty("Voucher") private float vouchers;

    @JsonProperty("Version") private int version;

    public AddToCart() {
    }

    @JsonCreator
    public AddToCart(@JsonProperty("SellerId") int sellerId,
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

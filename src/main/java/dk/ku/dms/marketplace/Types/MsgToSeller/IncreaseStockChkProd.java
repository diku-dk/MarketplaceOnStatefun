package dk.ku.dms.marketplace.Types.MsgToSeller;

import dk.ku.dms.marketplace.common.Entity.Product;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Setter
@Getter
public class IncreaseStockChkProd {
    //    also send this message to stock
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<IncreaseStockChkProd> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "IncreaseStockCheck"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, IncreaseStockChkProd.class));

    @JsonProperty("increaseStock")
    private IncreaseStock increaseStock;
    @JsonProperty("product")
    private Product product;

    public IncreaseStockChkProd() {
    }

    @JsonCreator
    public IncreaseStockChkProd(@JsonProperty("increaseStock") IncreaseStock increaseStock,
                                @JsonProperty("product")  Product product) {
        this.increaseStock = increaseStock;
        this.product = product;
    }
}
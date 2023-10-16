package dk.ku.dms.marketplace.Types.MsgToSeller;

import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.Map;

@Setter
@Getter
public class UpdatePrice {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<UpdatePrice> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdatePrice"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, UpdatePrice.class));

    @JsonProperty("updateID2Price")
    private Map<Integer, Double> updateID2Price;

//    @JsonProperty("product_id")
//    private Long product_id;
//    @JsonProperty("price")
//    private Double price;

    public UpdatePrice() {
    }

    @JsonCreator
    public UpdatePrice(@JsonProperty("updateID2Price") Map<Integer, Double> updateID2Price) {
        this.updateID2Price = updateID2Price;
    }
//    @JsonCreator
//    public UpdatePrice(@JsonProperty("product_id") Long product_id,
//                       @JsonProperty("price") Double price) {
//        this.product_id = product_id;
//        this.price = price;
//    }
}

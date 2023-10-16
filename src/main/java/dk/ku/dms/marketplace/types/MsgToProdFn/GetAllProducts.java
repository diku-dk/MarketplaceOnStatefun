package dk.ku.dms.marketplace.types.MsgToProdFn;

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
public class GetAllProducts {
//    get all the products of a seller
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<GetAllProducts> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetAllProducts"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, GetAllProducts.class));

    @JsonProperty("seller_id")
    private long seller_id;

    GetAllProducts() {
    }

    @JsonCreator
    public GetAllProducts(@JsonProperty("seller_id") long seller_id) {
        this.seller_id = seller_id;
    }
}

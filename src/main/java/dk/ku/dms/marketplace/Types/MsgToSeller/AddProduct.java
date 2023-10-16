package dk.ku.dms.marketplace.Types.MsgToSeller;

import dk.ku.dms.marketplace.common.Entity.Product;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Setter
@Getter
@ToString
public class AddProduct {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<AddProduct> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AddProduct"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, AddProduct.class));

    @JsonProperty("product")
    private Product product;

    public AddProduct() {
    }

    @JsonCreator
    public AddProduct(@JsonProperty("product") Product product) {
        this.product = product;
    }
}

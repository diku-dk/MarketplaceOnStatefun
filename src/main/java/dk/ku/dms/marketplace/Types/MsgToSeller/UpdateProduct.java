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

@Setter
@Getter
// think it as a new product
public class UpdateProduct {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<UpdateProduct> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdateProduct"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, UpdateProduct.class));

    @JsonProperty("seller_id")
    private int seller_id;
    @JsonProperty("product_id")
    private int product_id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("sku")
    private String sku;
    @JsonProperty("category")
    private String category;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("freight_value")
    private Double freight_value;
    @JsonProperty("status")
    private String status = "approved";
    @JsonProperty("version")
    private int version;

//    @JsonProperty("instanceId")
//    private int instanceId;

    public UpdateProduct() {
    }

    @JsonCreator
    public UpdateProduct(@JsonProperty("seller_id") int seller_id,
                         @JsonProperty("product_id") int product_id,
                         @JsonProperty("name") String name,
                         @JsonProperty("sku") String sku,
                         @JsonProperty("category") String category,
                         @JsonProperty("description") String description,
                         @JsonProperty("price") Double price,
                         @JsonProperty("freight_value") Double freight_value,
                         @JsonProperty("status") String status,
                         @JsonProperty("version") int version
//                         @JsonProperty("instanceId") int instanceId
    ) {
        this.seller_id = seller_id;
        this.product_id = product_id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.description = description;
        this.price = price;
        this.freight_value = freight_value;
        this.status = status;
        this.version = version;
//        this.instanceId = instanceId;
    }
}

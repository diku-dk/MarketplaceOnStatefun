//package Marketplace.Types.MsgToProdFn;
//
//import Marketplace.Constant.Constants;
//import Marketplace.Types.MsgToSeller.GetSeller;
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.flink.statefun.sdk.java.TypeName;
//import org.apache.flink.statefun.sdk.java.types.SimpleType;
//import org.apache.flink.statefun.sdk.java.types.Type;
//
//@Setter
//@Getter
//public class GetProduct {
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    public static final Type<GetProduct> TYPE =
//            SimpleType.simpleImmutableTypeFrom(
//                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetProduct"),
//                    mapper::writeValueAsBytes,
//                    bytes -> mapper.readValue(bytes, GetProduct.class));
//
//    @JsonProperty("product_id")
//    private long product_id;
//
//    GetProduct() {
//    }
//
//    @JsonCreator
//    public GetProduct(@JsonProperty("product_id") long product_id) {
//        this.product_id = product_id;
//    }
//}

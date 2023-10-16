//package Marketplace.Types.MsgToCartFn;
//
//import Marketplace.Constant.Constants;
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.flink.statefun.sdk.java.TypeName;
//import org.apache.flink.statefun.sdk.java.types.SimpleType;
//import org.apache.flink.statefun.sdk.java.types.Type;
//
//@Getter
//@Setter
//public class CheckoutCartResult {
//        private static final ObjectMapper mapper = new ObjectMapper();
//
//        public static final Type<CheckoutCartResult> TYPE =
//                SimpleType.simpleImmutableTypeFrom(
//                        TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CheckoutResult"),
//                        mapper::writeValueAsBytes,
//                        bytes -> mapper.readValue(bytes, CheckoutCartResult.class));
//
//        @JsonProperty("isSuccess")
//        private boolean isSuccess;
//
//        @JsonCreator
//        public CheckoutCartResult(@JsonProperty("isSuccess") boolean isSuccess) {
//            this.isSuccess = isSuccess;
//        }
//
//}

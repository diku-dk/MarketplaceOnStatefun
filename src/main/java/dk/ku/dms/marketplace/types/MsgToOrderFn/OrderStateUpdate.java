//package Marketplace.Types.MsgToOrderFn;
//
//import Marketplace.Constant.Constants;
//import Marketplace.Constant.Enums;
//import Marketplace.Types.MsgToPaymentFn.FailOrder;
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
//public class OrderStateUpdate {
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    public static final Type<OrderStateUpdate> TYPE =
//            SimpleType.simpleImmutableTypeFrom(
//                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "OrderStateUpdate"),
//                    mapper::writeValueAsBytes,
//                    bytes -> mapper.readValue(bytes, OrderStateUpdate.class));
//
//    @JsonProperty("orderId")
//    private long orderId;
//
//    @JsonProperty("orderStatus")
//    private Enums.OrderStatus orderStatus;
//
//    @JsonCreator
//    public OrderStateUpdate(@JsonProperty("orderId") long orderId,
//                            @JsonProperty("orderStatus") Enums.OrderStatus orderStatus) {
//        this.orderId = orderId;
//        this.orderStatus = orderStatus;
//    }
//}

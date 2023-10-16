//package Marketplace.Types.MsgToSeller;
//
//import Common.Entity.Product;
//import Marketplace.Constant.Constants;
//import Marketplace.Constant.Enums;
//import Marketplace.Constant.Enums.TaskType;
//import Marketplace.Constant.Enums.SendType;
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
//public class TaskFinish {
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    public static final Type<TaskFinish> TYPE =
//            SimpleType.simpleImmutableTypeFrom(
//                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "TaskFinish"),
//                    mapper::writeValueAsBytes,
//                    bytes -> mapper.readValue(bytes, TaskFinish.class));
//
//    @JsonProperty("taskType")
//    private Enums.TaskType taskType;
//
//    @JsonProperty("senderType")
//    private SendType senderType;
//
//    @JsonProperty("productId")
//    private Long productId;
//
//    @JsonProperty("productsOfSeller")
//    private Product[] productsOfSeller;
//
//    @JsonCreator
//    public TaskFinish(@JsonProperty("taskType") Enums.TaskType taskType,
//                      @JsonProperty("senderType") SendType senderType,
//                        @JsonProperty("productId") Long productId
//                      ) {
//        this.taskType = taskType;
//        this.senderType = senderType;
//        this.productId = productId;
//        this.productsOfSeller = null;
//    }
//}

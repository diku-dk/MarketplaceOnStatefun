//package dk.ku.dms.marketplace.messages.MsgToShipment;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import dk.ku.dms.marketplace.entities.Invoice;
//import dk.ku.dms.marketplace.utils.Constants;
//import org.apache.flink.statefun.sdk.java.TypeName;
//import org.apache.flink.statefun.sdk.java.types.SimpleType;
//import org.apache.flink.statefun.sdk.java.types.Type;
//
//import static dk.ku.dms.marketplace.utils.Constants.mapper;
//
//
//public class ProcessShipment {
//
//
//    public static final Type<ProcessShipment> TYPE =
//            SimpleType.simpleImmutableTypeFrom(
//                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ProcessShipment"),
//                    mapper::writeValueAsBytes,
//                    bytes -> mapper.readValue(bytes, ProcessShipment.class));
//
//    @JsonProperty("invoice")
//    private Invoice invoice;
//
//    @JsonProperty("instanceId")
//    private int instanceId;
//
//    @JsonCreator
//    public ProcessShipment(@JsonProperty("invoice") Invoice invoice,
//                           @JsonProperty("instanceId") int instanceId) {
//        this.invoice = invoice;
//        this.instanceId = instanceId;
//    }
//}

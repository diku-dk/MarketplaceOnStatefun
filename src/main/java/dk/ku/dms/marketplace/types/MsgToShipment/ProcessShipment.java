package dk.ku.dms.marketplace.types.MsgToShipment;

import dk.ku.dms.marketplace.common.Entity.Invoice;
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
public class ProcessShipment {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ProcessShipment> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ProcessShipment"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ProcessShipment.class));

    @JsonProperty("invoice")
    private Invoice invoice;

    @JsonProperty("instanceId")
    private int instanceId;

    @JsonCreator
    public ProcessShipment(@JsonProperty("invoice") Invoice invoice,
                           @JsonProperty("instanceId") int instanceId) {
        this.invoice = invoice;
        this.instanceId = instanceId;
    }
}

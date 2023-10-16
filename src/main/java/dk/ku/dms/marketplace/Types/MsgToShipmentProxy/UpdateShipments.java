package dk.ku.dms.marketplace.Types.MsgToShipmentProxy;

import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Getter
@Setter
public class UpdateShipments {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<UpdateShipments> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdateShipments"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, UpdateShipments.class));

    @JsonProperty("tid")
    private int tid;

    @JsonCreator
    public UpdateShipments(@JsonProperty("tid") int tid) {
        this.tid = tid;
    }
}

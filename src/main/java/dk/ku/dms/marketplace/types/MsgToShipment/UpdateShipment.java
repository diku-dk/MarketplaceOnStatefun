package dk.ku.dms.marketplace.types.MsgToShipment;

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
public class UpdateShipment {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<UpdateShipment> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdateShipment"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, UpdateShipment.class));

    @JsonProperty("tid")
    private int tid;

    @JsonCreator
    public UpdateShipment(@JsonProperty("tid") int tid) {
        this.tid = tid;
    }
}

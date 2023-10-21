package dk.ku.dms.marketplace.states;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;

public final class ShipmentProxyState {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ShipmentProxyState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ShipmentProxyState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ShipmentProxyState.class));

    @JsonProperty("tidList")
    public final HashMap<String, Integer> tidList;

    public ShipmentProxyState() {
        this.tidList = new HashMap<>();
    }
}

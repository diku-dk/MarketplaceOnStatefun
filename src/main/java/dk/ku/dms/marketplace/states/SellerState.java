package dk.ku.dms.marketplace.states;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.entities.OrderEntry;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SellerState {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<SellerState> SELLER_STATE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "SellerState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, SellerState.class));

    @JsonProperty("orderEntries")
    private final Map<String, List<OrderEntry>> orderEntries;

    @JsonCreator
    public SellerState() {
        this.orderEntries = new HashMap<>();
    }

    @JsonIgnore
    public Map<String, List<OrderEntry>> getOrderEntries() {
        return this.orderEntries;
    }

}
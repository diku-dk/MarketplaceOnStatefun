package dk.ku.dms.marketplace.Types.MsgToShipment;

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
public class GetPendingPackages {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<GetPendingPackages> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetPendingPackages"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, GetPendingPackages.class));

    @JsonProperty("sellerID")
    private int sellerID;

    @JsonCreator
    public GetPendingPackages(@JsonProperty("sellerID") int sellerID) {
        this.sellerID = sellerID;
    }
}

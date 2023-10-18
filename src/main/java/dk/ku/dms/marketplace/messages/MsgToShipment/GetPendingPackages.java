package dk.ku.dms.marketplace.messages.MsgToShipment;

import dk.ku.dms.marketplace.utils.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;


public class GetPendingPackages {
    

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

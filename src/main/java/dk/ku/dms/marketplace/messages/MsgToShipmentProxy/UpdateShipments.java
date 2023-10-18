package dk.ku.dms.marketplace.messages.MsgToShipmentProxy;

import dk.ku.dms.marketplace.utils.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;


public class UpdateShipments {
    

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

package dk.ku.dms.marketplace.messages.MsgToSeller;

import dk.ku.dms.marketplace.entities.Seller;
import dk.ku.dms.marketplace.utils.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;


public class InitSeller {

    

    public static final Type<InitSeller> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "InitSeller"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, InitSeller.class));

    @JsonProperty("seller")
    private Seller seller;

    public InitSeller() {
    }

    @JsonCreator
    public InitSeller(@JsonProperty("seller") Seller seller) {
        this.seller = seller;
    }
}

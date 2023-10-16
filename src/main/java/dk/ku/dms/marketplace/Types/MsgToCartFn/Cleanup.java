package dk.ku.dms.marketplace.Types.MsgToCartFn;

import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

public class Cleanup {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<Cleanup> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "Cleanup"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Cleanup.class));

    Cleanup() {
    }
}

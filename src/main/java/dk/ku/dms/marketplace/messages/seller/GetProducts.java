package dk.ku.dms.marketplace.messages.seller;

import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public class GetProducts {
    

    public static final Type<GetProducts> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetProducts"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, GetProducts.class));

    GetProducts() {
    }

}

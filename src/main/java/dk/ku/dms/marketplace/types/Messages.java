package dk.ku.dms.marketplace.types;

import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.types.Entity.TmpUserLogin;
import dk.ku.dms.marketplace.types.Entity.TmpUserPofile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

public final class Messages {
    private Messages() {}

    private static final ObjectMapper mapper = new ObjectMapper(); // JSON

//    private static final String TYPES_NAMESPACE = "e-commerce.types";

    public static final Type<TmpUserLogin> USER_LOGIN_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UserLogin"),
                mapper::writeValueAsBytes,
                bytes -> mapper.readValue(bytes, TmpUserLogin.class)
    );

    public static final Type<TmpUserPofile> USER_PROFILE_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UserProfile"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, TmpUserPofile.class)
            );


    public static final Type<EgressRecord> EGRESS_RECORD_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.EGRESS_NAMESPACE, "EgressRecord"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, EgressRecord.class)
            );

}

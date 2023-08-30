package Marketplace.Types.MsgToCustomer;

import Marketplace.Constant.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.Getter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;
//
@Getter
@Setter
public class GetCustomer {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<GetCustomer> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "GetCustomer"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, GetCustomer.class));

    @JsonProperty("customerId")
     private long customerId;

    GetCustomer() {
    }

    @JsonCreator
    public GetCustomer(@JsonProperty("customerId") long customerId) {
        this.customerId = customerId;
    }
}





package dk.ku.dms.marketplace.Types.MsgToCustomer;

import dk.ku.dms.marketplace.common.Entity.Customer;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;


@Setter
@Getter
public class InitCustomer {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<InitCustomer> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "InitCustomer"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, InitCustomer.class));

    @JsonProperty("customer")
    private Customer customer;

    public InitCustomer() {
    }

    @JsonCreator
    public InitCustomer(@JsonProperty("customer") Customer customer) {
        this.customer = customer;
    }
}

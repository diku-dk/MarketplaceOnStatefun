package dk.ku.dms.marketplace.Types.State;

import dk.ku.dms.marketplace.common.Entity.Customer;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;

@Setter
@Getter
public class CustomerState {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<CustomerState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CustomerState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CustomerState.class));

//    @JsonProperty("customer")
//    public Customer customer;

    @JsonProperty("customers")
    public HashMap<Integer, Customer> customers;

    @JsonIgnore
    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    @JsonIgnore
    public Customer getCustomerById(int customer_id) {
        return customers.get(customer_id);
    }

    public CustomerState() {
        customers = new HashMap<>();
    }
}

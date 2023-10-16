package dk.ku.dms.marketplace.common.Entity;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
public class Checkout {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<Checkout> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "Checkout"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Checkout.class));

    @JsonProperty("createdAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonProperty("customerCheckout") private CustomerCheckout customerCheckout;
    @JsonProperty("items") private Map<Integer, BasketItem> items;

    @JsonCreator
    public Checkout (@JsonProperty("createdAt") LocalDateTime createdAt,
                     @JsonProperty("customerCheckout") CustomerCheckout customerCheckout,
                     @JsonProperty("items") Map<Integer, BasketItem> items) {
        this.createdAt = createdAt;
        this.customerCheckout = customerCheckout;
        this.items = items;
    }
}

package dk.ku.dms.marketplace.types.Entity;
import dk.ku.dms.marketplace.common.Entity.BasketItem;
import dk.ku.dms.marketplace.common.Entity.CustomerCheckout;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class Checkout {
    @JsonProperty("createdAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    @JsonProperty("customerCheckout")
    private CustomerCheckout customerCheckout;
    @JsonProperty("items")
    private Map<Integer, BasketItem> items;
}

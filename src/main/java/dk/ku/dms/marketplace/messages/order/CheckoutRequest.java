package dk.ku.dms.marketplace.messages.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.CustomerCheckout;

import java.time.LocalDateTime;
import java.util.List;

public final class CheckoutRequest {

    @JsonProperty("timestamp")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime timestamp;

    @JsonProperty("customerCheckout")
    private final CustomerCheckout customerCheckout;

    @JsonProperty("items")
    private final List<CartItem> items;

    @JsonProperty("instanceId")
    private final String instanceId;

    @JsonCreator
    public CheckoutRequest(@JsonProperty("timestamp") LocalDateTime timestamp,
                           @JsonProperty("customerCheckout") CustomerCheckout customerCheckout,
                           @JsonProperty("items") List<CartItem> items,
                           @JsonProperty("instanceId") String instanceId) {
        this.timestamp = timestamp;
        this.customerCheckout = customerCheckout;
        this.items = items;
        this.instanceId = instanceId;
    }

    @JsonIgnore
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    public CustomerCheckout getCustomerCheckout() {
        return customerCheckout;
    }

    @JsonIgnore
    public List<CartItem> getItems() {
        return items;
    }

    @JsonIgnore
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return "CheckoutRequest{" +
                "timestamp=" + timestamp +
                ", customerCheckout=" + customerCheckout +
                ", items=" + items +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}

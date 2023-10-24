package dk.ku.dms.marketplace.messages.shipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.messages.cart.CustomerCheckout;
import dk.ku.dms.marketplace.entities.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public final class PaymentConfirmed {

    @JsonProperty("customerCheckout")
    private final CustomerCheckout customerCheckout;

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("totalAmount")
    private final float totalAmount;

    @JsonProperty("items")
    private final List<OrderItem> items;

    @JsonProperty("date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime date;

    @JsonProperty("instanceId")
    private String instanceId;

    @JsonCreator
    public PaymentConfirmed(@JsonProperty("customerCheckout") CustomerCheckout customerCheckout, @JsonProperty("orderId") int orderId, @JsonProperty("totalAmount") float totalAmount,
                            @JsonProperty("items") List<OrderItem> items, @JsonProperty("date") LocalDateTime date, @JsonProperty("instanceId") String instanceId) {
        this.customerCheckout = customerCheckout;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.items = items;
        this.date = date;
        this.instanceId = instanceId;
    }

    @JsonIgnore
    public CustomerCheckout getCustomerCheckout() {
        return customerCheckout;
    }

    @JsonIgnore
    public int getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public float getTotalAmount() {
        return totalAmount;
    }

    @JsonIgnore
    public List<OrderItem> getItems() {
        return items;
    }

    @JsonIgnore
    public LocalDateTime getDate() {
        return date;
    }

    @JsonIgnore
    public String getInstanceId() {
        return instanceId;
    }
}

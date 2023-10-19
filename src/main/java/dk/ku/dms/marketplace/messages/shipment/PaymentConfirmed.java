package dk.ku.dms.marketplace.messages.shipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.entities.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentConfirmed {

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
    public PaymentConfirmed(int orderId, float totalAmount, List<OrderItem> items, LocalDateTime date, String instanceId) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.items = items;
        this.date = date;
        this.instanceId = instanceId;
    }

}

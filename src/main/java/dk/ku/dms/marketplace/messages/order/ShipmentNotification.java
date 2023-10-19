package dk.ku.dms.marketplace.messages.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Enums;

import java.time.LocalDateTime;

public final class ShipmentNotification {

    @JsonProperty("orderId")
    private final int orderId;

    @JsonProperty("Status")
    private final Enums.ShipmentStatus shipmentStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("eventDate")
    private final LocalDateTime eventDate;

    @JsonProperty("customerID")
    private final int customerId;

    @JsonCreator
    public ShipmentNotification(@JsonProperty("orderId") int orderId,
                                @JsonProperty("customerID") int customerId,
                                @JsonProperty("Status") Enums.ShipmentStatus shipmentStatus,
                                @JsonProperty("eventDate") LocalDateTime eventDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.shipmentStatus = shipmentStatus;
        this.eventDate = eventDate;
    }

    @JsonIgnore
    public int getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public Enums.ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    @JsonIgnore
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    @JsonIgnore
    public int getCustomerId() {
        return customerId;
    }
}

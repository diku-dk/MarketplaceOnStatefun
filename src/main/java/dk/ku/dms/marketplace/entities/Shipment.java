package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Enums;

import java.time.LocalDateTime;

public final class Shipment {
    @JsonProperty("shipmentId")
    private final int shipmentId;
    @JsonProperty("orderId")
    private final int orderId;
    @JsonProperty("customerId")
    private final int customerId;
    @JsonProperty("packageCnt")
    private final int packageCount;
    @JsonProperty("totalFreight")
    private final float totalFreight;
    @JsonProperty("firstName")
    private final String firstName;
    @JsonProperty("lastName")
    private final String lastName;
    @JsonProperty("street")
    private final String street;
    @JsonProperty("zipCode")
    private final String zipCode;
    @JsonProperty("status")
    private Enums.ShipmentStatus status;
    @JsonProperty("city")
    private final String city;
    @JsonProperty("state")
    private final String state;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("requestDate")
    private final LocalDateTime requestDate;

    @JsonCreator
    public Shipment(
            @JsonProperty("shipmentId") int shipmentId,
            @JsonProperty("orderId") int orderId,
            @JsonProperty("customerId") int customerId,
            @JsonProperty("packageCnt") int packageCount,
            @JsonProperty("totalFreight") float totalFreight,
            @JsonProperty("requestDate") LocalDateTime requestDate,
            @JsonProperty("status") Enums.ShipmentStatus status,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("street") String street,
            @JsonProperty("zipCode") String zipCode,
            @JsonProperty("city") String city,
            @JsonProperty("state") String state
    ) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.packageCount = packageCount;
        this.totalFreight = totalFreight;
        this.customerId = customerId;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.requestDate = requestDate;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
    }

    @JsonIgnore
    public int getShipmentId() {
        return shipmentId;
    }

    @JsonIgnore
    public int getPackageCount() {
        return packageCount;
    }

    @JsonIgnore
    public Enums.ShipmentStatus getStatus() {
        return this.status;
    }

    @JsonIgnore
    public void setStatus(Enums.ShipmentStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public int getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "shipmentId=" + shipmentId +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                ", packageCount=" + packageCount +
                ", totalFreight=" + totalFreight +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", status=" + status +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", requestDate=" + requestDate +
                '}';
    }
}

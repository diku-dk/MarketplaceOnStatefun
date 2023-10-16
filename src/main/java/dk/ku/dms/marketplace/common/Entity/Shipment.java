package dk.ku.dms.marketplace.common.Entity;

import dk.ku.dms.marketplace.constants.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class Shipment {
    @JsonProperty("shipmentId") private int shipmentId;
    @JsonProperty("orderId") private int orderId;
    @JsonProperty("customerId") private int customerId;
    @JsonProperty("packageCnt") private int packageCnt;
    @JsonProperty("totalFreight") private float totalFreight;
    @JsonProperty("orderPartition") private int orderPartition;

    @JsonProperty("firstName") private String firstName;
    @JsonProperty("lastName") private String lastName;
    @JsonProperty("street") private String street;
    @JsonProperty("zipCode") private String zipCode;
    @JsonProperty("status") Enums.ShipmentStatus status;
    @JsonProperty("city") private String city;
    @JsonProperty("state") private String state;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("requestDate") private LocalDateTime requestDate;

    @JsonCreator
    public Shipment(
            @JsonProperty("shipmentId") int shipmentId,
            @JsonProperty("orderId") int orderId,
            @JsonProperty("customerId") int customerId,
            @JsonProperty("packageCnt") int packageCnt,

            @JsonProperty("totalFreight") float totalFreight,
            @JsonProperty("requestDate") LocalDateTime requestDate,
            @JsonProperty("status") Enums.ShipmentStatus status,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("street") String street,

//            @JsonProperty("orderId") long orderId,
            @JsonProperty("orderPartition") int orderPartition,

            @JsonProperty("zipCode") String zipCode,
            @JsonProperty("city") String city,
            @JsonProperty("state") String state
    ) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.packageCnt = packageCnt;
        this.orderPartition = orderPartition;
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
}

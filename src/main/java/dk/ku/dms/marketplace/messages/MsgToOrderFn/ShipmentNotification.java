package dk.ku.dms.marketplace.messages.MsgToOrderFn;

import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;

import static dk.ku.dms.marketplace.utils.Constants.mapper;


public class ShipmentNotification {
    

    public static final Type<ShipmentNotification> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ShipmentNotification"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ShipmentNotification.class));

    @JsonProperty("orderId")
    private int orderId;

    @JsonProperty("Status")
    private Enums.ShipmentStatus shipmentStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("eventDate")
    private LocalDateTime eventDate;

    @JsonProperty("customerID")
    private int customerID;

    @JsonCreator
    public ShipmentNotification(@JsonProperty("orderId") int orderId,
                                @JsonProperty("customerID") int customerID,
                                @JsonProperty("Status") Enums.ShipmentStatus shipmentStatus,
                                @JsonProperty("eventDate") LocalDateTime eventDate) {
        this.orderId = orderId;
        this.customerID = customerID;
        this.shipmentStatus = shipmentStatus;
        this.eventDate = eventDate;
    }
}

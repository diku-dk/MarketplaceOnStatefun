package Marketplace.Types.MsgToOrderFn;

import Marketplace.Constant.Constants;
import Marketplace.Constant.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentNotification {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ShipmentNotification> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ShipmentNotification"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ShipmentNotification.class));

    @JsonProperty("orderId")
    private long orderId;

    @JsonProperty("Status")
    private Enums.ShipmentStatus shipmentStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("eventDate")
    private LocalDateTime eventDate;

    @JsonProperty("customerID")
    private long customerID;

    @JsonCreator
    public ShipmentNotification(@JsonProperty("orderId") long orderId,
                                @JsonProperty("customerID") long customerID,
                                @JsonProperty("Status") Enums.ShipmentStatus shipmentStatus,
                                @JsonProperty("eventDate") LocalDateTime eventDate) {
        this.orderId = orderId;
        this.customerID = customerID;
        this.shipmentStatus = shipmentStatus;
        this.eventDate = eventDate;
    }
}

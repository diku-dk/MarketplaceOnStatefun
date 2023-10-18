package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Enums;

import java.time.LocalDateTime;



public class OrderHistory {

//    @JsonProperty("id") private long id;
    @JsonProperty("orderId") private int orderId;
    @JsonProperty("status") private Enums.OrderStatus status;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at") private LocalDateTime created_at;

    @JsonCreator
    public OrderHistory() {
    }

    @JsonCreator
    public OrderHistory(@JsonProperty("orderId") int orderId,
                        @JsonProperty("created_at") LocalDateTime created_at,
                        @JsonProperty("status") Enums.OrderStatus status) {
        this.orderId = orderId;
        this.created_at = created_at;
        this.status = status;
    }
}

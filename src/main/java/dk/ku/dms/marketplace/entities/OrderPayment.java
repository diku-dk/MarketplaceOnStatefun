package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;


public class OrderPayment {
    @JsonProperty("orderId") private String orderId;

    // e.g.  1 - credit card  2 - coupon
    @JsonProperty("sequential") private int sequential;

    // number of times the credit card is charged (usually once a month)
    @JsonProperty("installments") public int installments;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("createdDate") private LocalDateTime createdDate;

    // respective to this line (ie. coupon)
    @JsonProperty("value") private float value;

    // vouchers dont need to have this field filled
    @JsonProperty("status") private String status;

    @JsonCreator
    public OrderPayment(
            @JsonProperty("orderId") String orderId,
            @JsonProperty("sequential") int sequential,
            @JsonProperty("installments") int installments,
            @JsonProperty("createdDate") LocalDateTime createdDate,
            @JsonProperty("value") float value,
            @JsonProperty("status") String status) {
        this.orderId = orderId;
        this.sequential = sequential;
        this.installments = installments;
        this.createdDate = createdDate;
        this.value = value;
        this.status = status;
    }
}

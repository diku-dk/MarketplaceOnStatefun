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
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Order {

    @JsonProperty("id")
    private int id;
    @JsonProperty("customerId")
    private int customerId;
    @JsonProperty("status")
    private Enums.OrderStatus status;
    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("purchaseTimestamp")
    private LocalDateTime purchaseTimestamp;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at")
    private LocalDateTime created_at;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updated_at;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("paymentDate")
    private LocalDateTime paymentDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivered_carrier_date")
    private LocalDateTime delivered_carrier_date;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivered_customer_date")
    private LocalDateTime delivered_customer_date;

    // dev
    @JsonProperty("countITems")
    private int countItems;
    @JsonProperty("totalAmount")
    private float totalAmount;
    @JsonProperty("totalFreight")
    private float totalFreight;
    @JsonProperty("totalIncentive")
    private float totalIncentive;
    @JsonProperty("totalInvoice")
    private float totalInvoice;
    @JsonProperty("totalItems")
    private float totalItems;
    @JsonProperty("data")
    private String data;

    @JsonCreator
    public Order() {
    }
}

package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Enums;

import java.time.LocalDateTime;



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
    @JsonProperty("countItems")
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

    public Order(int id, int customerId, Enums.OrderStatus status, String invoiceNumber, LocalDateTime purchaseTimestamp, LocalDateTime created_at, LocalDateTime updated_at, LocalDateTime paymentDate, LocalDateTime delivered_carrier_date, LocalDateTime delivered_customer_date, int countItems, float totalAmount, float totalFreight, float totalIncentive, float totalInvoice, float totalItems, String data) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.invoiceNumber = invoiceNumber;
        this.purchaseTimestamp = purchaseTimestamp;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.paymentDate = paymentDate;
        this.delivered_carrier_date = delivered_carrier_date;
        this.delivered_customer_date = delivered_customer_date;
        this.countItems = countItems;
        this.totalAmount = totalAmount;
        this.totalFreight = totalFreight;
        this.totalIncentive = totalIncentive;
        this.totalInvoice = totalInvoice;
        this.totalItems = totalItems;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public float getTotalInvoice() {
        return totalInvoice;
    }
}

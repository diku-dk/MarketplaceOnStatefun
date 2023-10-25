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

public final class Order {

    @JsonProperty("id")
    private final int id;
    @JsonProperty("customerId")
    private final int customerId;
    @JsonProperty("status")
    private Enums.OrderStatus status;

    @JsonProperty("invoiceNumber")
    private final String invoiceNumber;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("purchaseTimestamp")
    private final LocalDateTime purchaseTimestamp;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at")
    private final LocalDateTime created_at;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updated_at;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("paymentDate")
    private final LocalDateTime paymentDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivered_carrier_date")
    private final LocalDateTime delivered_carrier_date;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivered_customer_date")
    private LocalDateTime delivered_customer_date;

    @JsonProperty("countItems")
    private final int countItems;

    @JsonProperty("totalAmount")
    private final float totalAmount;
    @JsonProperty("totalFreight")
    private final float totalFreight;
    @JsonProperty("totalIncentive")
    private final float totalIncentive;
    @JsonProperty("totalInvoice")
    private final float totalInvoice;
    @JsonProperty("totalItems")
    private final float totalItems;
    @JsonProperty("data")
    private final String data;

    @JsonCreator
    public Order(@JsonProperty("id") int id, @JsonProperty("customerId") int customerId, @JsonProperty("status") Enums.OrderStatus status, @JsonProperty("invoiceNumber") String invoiceNumber, @JsonProperty("purchaseTimestamp") LocalDateTime purchaseTimestamp,
                 @JsonProperty("paymentDate") LocalDateTime paymentDate, @JsonProperty("delivered_carrier_date") LocalDateTime delivered_carrier_date,
                 @JsonProperty("delivered_customer_date") LocalDateTime delivered_customer_date, @JsonProperty("countItems") int countItems, @JsonProperty("totalAmount") float totalAmount, @JsonProperty("totalFreight") float totalFreight,
                 @JsonProperty("totalIncentive") float totalIncentive, @JsonProperty("totalInvoice") float totalInvoice, @JsonProperty("totalItems") float totalItems, @JsonProperty("data") String data) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.invoiceNumber = invoiceNumber;
        this.purchaseTimestamp = purchaseTimestamp;
        this.created_at = LocalDateTime.now();
        this.updated_at = this.created_at;
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

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonIgnore
    public float getTotalInvoice() {
        return totalInvoice;
    }

    @JsonIgnore
    public Enums.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Enums.OrderStatus status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updated_at = updatedAt;
    }

    public void setDeliveredCustomerDate(LocalDateTime eventDate) {
        this.delivered_customer_date = eventDate;
    }
    
    public int getCustomerId() {
    	return customerId;
    }

}

package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;


import java.time.LocalDateTime;
import java.util.List;


public class Invoice {
    @JsonProperty("customerCheckout")
    private CustomerCheckout customerCheckout;
    @JsonProperty("orderID")
    private int orderID;
    @JsonProperty("invoiceNumber")
    private String invoiceNumber;
    @JsonProperty("items")
    private List<OrderItem> items;
    // because checkout chose a random partition to send the order
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("issueDate")
    private LocalDateTime issueDate;
    @JsonProperty("totalInvoice")
    private float totalInvoice;
    @JsonProperty("orderPartitionID")
    private String orderPartitionID;
//    @JsonProperty("instanceId")
//    private int instanceId;

    public Invoice() {
    }

    @JsonCreator
    public Invoice(@JsonProperty("customerCheckout") CustomerCheckout customerCheckout,
                   @JsonProperty("orderID") int orderID,
                   @JsonProperty("invoiceNumber") String invoiceNumber,
                   @JsonProperty("items") List<OrderItem> items,
                   @JsonProperty("totalInvoice") float totalInvoice,
                   @JsonProperty("issueDate") LocalDateTime issueDate,
                   @JsonProperty("orderPartitionID") String orderPartitionID
//                   @JsonProperty("instanceId") int instanceId
    ) {
        this.customerCheckout = customerCheckout;
        this.orderID = orderID;
        this.invoiceNumber = invoiceNumber;
        this.issueDate = issueDate;
        this.totalInvoice = totalInvoice;
        this.items = items;
        this.orderPartitionID = orderPartitionID;
//        this.instanceId = instanceId;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}

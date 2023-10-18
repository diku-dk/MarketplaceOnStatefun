package dk.ku.dms.marketplace.messages.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.entities.CustomerCheckout;
import dk.ku.dms.marketplace.entities.OrderItem;

import java.time.LocalDateTime;
import java.util.List;


public final class InvoiceIssued {
    @JsonProperty("customerCheckout")
    private final CustomerCheckout customerCheckout;

    @JsonProperty("orderId")
    private final int orderId;
    @JsonProperty("invoiceNumber")
    private final String invoiceNumber;
    @JsonProperty("items")
    private final List<OrderItem> items;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("issueDate")
    private final LocalDateTime issueDate;
    @JsonProperty("totalInvoice")
    private final float totalInvoice;

    @JsonProperty("instanceId")
    private final String instanceId;

    @JsonCreator
    public InvoiceIssued(@JsonProperty("customerCheckout") CustomerCheckout customerCheckout,
                           @JsonProperty("orderID") int orderId,
                           @JsonProperty("invoiceNumber") String invoiceNumber,
                           @JsonProperty("items") List<OrderItem> items,
                           @JsonProperty("totalInvoice") float totalInvoice,
                           @JsonProperty("issueDate") LocalDateTime issueDate,
                           @JsonProperty("instanceId") String instanceId
    ) {
        this.customerCheckout = customerCheckout;
        this.orderId = orderId;
        this.invoiceNumber = invoiceNumber;
        this.issueDate = issueDate;
        this.totalInvoice = totalInvoice;
        this.items = items;
        this.instanceId = instanceId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public int getOrderId() {
        return orderId;
    }
}

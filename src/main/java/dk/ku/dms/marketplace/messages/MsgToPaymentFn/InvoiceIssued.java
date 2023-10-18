package dk.ku.dms.marketplace.messages.MsgToPaymentFn;

import dk.ku.dms.marketplace.entities.Invoice;
import dk.ku.dms.marketplace.utils.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;


public class InvoiceIssued {

    

    public static final Type<InvoiceIssued> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "InvoiceIssued"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, InvoiceIssued.class));

    @JsonProperty("invoice")
    private Invoice invoice;

    @JsonProperty("instanceId")
    private int instanceId;

    public InvoiceIssued() {
    }

    @JsonCreator
    public InvoiceIssued(@JsonProperty("invoice") Invoice invoice,
                         @JsonProperty("instanceId") int instanceId) {
        this.invoice = invoice;
        this.instanceId = instanceId;
    }
}

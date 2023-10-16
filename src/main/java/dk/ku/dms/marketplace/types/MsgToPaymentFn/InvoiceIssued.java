package dk.ku.dms.marketplace.types.MsgToPaymentFn;

import dk.ku.dms.marketplace.common.Entity.Invoice;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Setter
@Getter
public class InvoiceIssued {

    private static final ObjectMapper mapper = new ObjectMapper();

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

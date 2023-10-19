package dk.ku.dms.marketplace.messages.payment;

import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public class PaymentMessages {

    public static final Type<InvoiceIssued> INVOICE_ISSUED_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "InvoiceIssued"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, InvoiceIssued.class));



}
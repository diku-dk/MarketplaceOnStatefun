package dk.ku.dms.marketplace.egress;

import org.apache.flink.statefun.sdk.java.TypeName;

public final class Identifiers {

    private Identifiers() {}

    public static final TypeName RECEIPT_EGRESS =
            TypeName.typeNameFromString("io.statefun.playground/egress");
    public static final String RECEIPT_TOPICS = "receipts";
    // static final String TEST_RECEIPT_TOPICS = "testreceipts";
}

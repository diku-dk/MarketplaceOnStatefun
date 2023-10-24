package dk.ku.dms.marketplace.messages.customer;

import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class CustomerMessages {

    public static final Type<NotifyCustomer> NOTIFY_CUSTOMER_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "NotifyCustomer"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, NotifyCustomer.class));

}

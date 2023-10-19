package dk.ku.dms.marketplace.utils;

import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;
import java.util.Optional;

public class Utils {

    static final TypeName KFK_EGRESS = TypeName.typeNameOf("e-commerce.fns", "kafkaSink");



    public static <T> void sendMessage(Context context, TypeName addressType, String addressId, Type<T> messageType, T messageContent) {
        Message msg = MessageBuilder.forAddress(addressType, addressId)
                .withCustomType(messageType, messageContent)
                .build();
        context.send(msg);
    }

    public static <T> void sendMessageToCaller(Context context, Type<T> messageType, T messageContent) {
        final Optional<Address> caller = context.caller();
        if (caller.isPresent()) {
            context.send(
                    MessageBuilder.forAddress(caller.get())
                            .withCustomType(messageType, messageContent)
                            .build());
        } else {
            throw new IllegalStateException("There should always be a caller");
        }
    }
    
    public static String GetInvoiceNumber(int customerId, LocalDateTime timestamp, int orderId)
    {
    	return new StringBuilder()
    			.append(customerId).append('-')
    			.append(timestamp.toString()).append('-')
    			.append(orderId).toString();
    }
}

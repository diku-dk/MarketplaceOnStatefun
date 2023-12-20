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

    public static String GetInvoiceNumber(int customerId, LocalDateTime timestamp, int orderId)
    {
    	return new StringBuilder()
    			.append(customerId).append('-')
    			.append(timestamp.toString()).append('-')
    			.append(orderId).toString();
    }

    public static int getShipmentActorID(int customerId, int NumShipmentActors){
        return customerId % NumShipmentActors;
    }

}

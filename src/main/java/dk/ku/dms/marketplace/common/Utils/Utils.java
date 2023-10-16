package dk.ku.dms.marketplace.common.Utils;

import dk.ku.dms.marketplace.common.Entity.TransactionMark;
import dk.ku.dms.marketplace.constants.Enums;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.io.KafkaEgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.Optional;

public class Utils {

    static final TypeName KFK_EGRESS = TypeName.typeNameOf("e-commerce.fns", "kafkaSink");

    public static String getFnName(String fnType) {
        String[] fnTypeArr = fnType.split("/");
        return fnTypeArr[fnTypeArr.length - 1];
    }

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

    public static void notifyTransactionComplete(Context context,
                                                 String transactionType,
                                                 String functionID,
                                                 long taskId, int tid, String receiver, Enums.MarkStatus status, String source) {
        String response = "";
        try {
            TransactionMark transactionMark = new TransactionMark(taskId, tid, receiver, status, source);
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.writeValueAsString(transactionMark);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

//        context.send(
//                KafkaEgressMessage.forEgress(KFK_EGRESS)
//                        .withTopic(transactionType)
//                        .withUtf8Key(functionID)
//                        .withUtf8Value(response)
//                        .build());

//        final Messages.Receipt receipt = new Messages.Receipt(context.self().id(), items);
        final EgressMessage egressMessage =
                EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                        .withCustomType(
                                Messages.EGRESS_RECORD_JSON_TYPE,
                                new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, transactionMark.toString()))
                        .build();
        context.send(egressMessage);

    }

    ////        System.out.println("checkout result send to kafka");

}

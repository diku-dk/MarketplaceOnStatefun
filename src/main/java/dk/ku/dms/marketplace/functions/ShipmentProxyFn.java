//package dk.ku.dms.marketplace.functions;
//
//import dk.ku.dms.marketplace.common.utils.Utils;
//import dk.ku.dms.marketplace.constants.Constants;
//import dk.ku.dms.marketplace.constants.Enums;
//import dk.ku.dms.marketplace.types.MsgToShipment.UpdateShipment;
//import dk.ku.dms.marketplace.types.MsgToShipmentProxy.UpdateShipments;
//import dk.ku.dms.marketplace.types.State.ShipmentProxyState;
//import org.apache.flink.statefun.sdk.java.*;
//import org.apache.flink.statefun.sdk.java.message.Message;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.logging.Logger;
//
//public class ShipmentProxyFn implements StatefulFunction {
//
//    Logger logger = Logger.getLogger("ShipmentProxyFn");
//
//    static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNCTIONS_NAMESPACE, "shipmentProxy");
//
//    static final ValueSpec<ShipmentProxyState> PROXYSTATE =  ValueSpec.named("shipmentProxyState").withCustomType(ShipmentProxyState.TYPE);
//
//    //  Contains all the information needed to create a function instance
//    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
//            .withValueSpec(PROXYSTATE)
//            .withSupplier(ShipmentProxyFn::new)
//            .build();
//
//    static final TypeName KFK_EGRESS = TypeName.typeNameOf("e-commerce.fns", "kafkaSink");
//
//    @Override
//    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
//        try {
//            if (message.is(UpdateShipments.TYPE)) {
//                onUpdateShipments(context, message);
//            }
//            // ack from shipment
//            else if (message.is(UpdateShipment.TYPE)) {
//                onProcessPartitionAck(context, message);
//            }
//        } catch (Exception e) {
//            logger.info("Exception in ShipmentProxyFn: " + e.getMessage());
//        }
//        return context.done();
//    }
//
//    private void printLog(String log) {
//        System.out.println(log);
//    }
//
//    private String getPartionText(String id) {
//        return String.format(" [ ShipmentProxy partitionId %s ] ", id);
//    }
//
//    private void onUpdateShipments(Context context, Message message) {
//        UpdateShipments updateShipments = message.as(UpdateShipments.TYPE);
//        int tid = updateShipments.getTid();
//        ShipmentProxyState shipmentProxyState = context.storage().get(PROXYSTATE).orElse(new ShipmentProxyState());
//
//        int partitionNum = Constants.nShipmentPartitions;
//        shipmentProxyState.addTask(tid, partitionNum);
////        logger.info("[receive] {tid=" + tid + "} updated delivery");
//        String log_ = getPartionText(context.self().id())
//                + "updated delivery [receive], " + "tid : " + tid + "\n";
//        printLog(log_);
//
//        // 循环partitionNum次，每次发送一个UpdateShipment
//        for (int i = 0; i < partitionNum; i++) {
//            // TODO: 7/6/2023
//            Utils.sendMessage(context,
//                    ShipmentFn.TYPE,
//                    String.valueOf(i),
//                    UpdateShipment.TYPE,
//                    new UpdateShipment(tid));
//        }
//
//        context.storage().set(PROXYSTATE, shipmentProxyState);
////        logger.info("ShipmentProxyFn: Updated shipmentProxyState, tid = " + tid);
//    }
//
//    private void onProcessPartitionAck(Context context, Message message) {
//        ShipmentProxyState shipmentProxyState = context.storage().get(PROXYSTATE).orElse(new ShipmentProxyState());
//
//        UpdateShipment updateShipment = message.as(UpdateShipment.TYPE);
//        int tid = updateShipment.getTid();
//
//        shipmentProxyState.subTaskDone(tid);
//
////        logger.info("ShipmentProxyFn: Received ack from shipment, tid = " + tid + "remain task = " + shipmentProxyState.getTaskList().get(tid));
//        if (shipmentProxyState.isTaskDone(tid)) {
//            shipmentProxyState.removeTask(tid);
////            logger.info("ShipmentProxyFn: All partitions acked, tid = " + tid);
//
//
//
//            Utils.notifyTransactionComplete(context,
//                    Enums.TransactionType.UPDATE_DELIVERY.toString(),
//                    context.self().id(),
//                    tid,
//                    tid,
//                    "0",
//                    Enums.MarkStatus.SUCCESS,
//                    "shipment");
//
////            String response = "";
////            try {
////                TransactionMark transactionMark = new TransactionMark(
////                        tid,
////                        tid,
////                        "0",
////                        Enums.MarkStatus.SUCCESS,
////                        "shipment");
////                ObjectMapper mapper = new ObjectMapper();
////                response = mapper.writeValueAsString(transactionMark);
////            } catch (JsonProcessingException e) {
////                e.printStackTrace();
////            }
////
////            context.send(
////                    KafkaEgressMessage.forEgress(KFK_EGRESS)
////                            .withTopic("updateDeliveryTask")
////                            .withUtf8Key(context.self().id())
////                            .withUtf8Value(response)
////                            .build());
//            String log_ = getPartionText(context.self().id())
//                    + "updated delivery success, " + "tid : " + tid + "\n";
//            printLog(log_);
////            logger.info("[success] {tid=" + tid + "} updated delivery");
//        }
//        context.storage().set(PROXYSTATE, shipmentProxyState);
//    }
//}

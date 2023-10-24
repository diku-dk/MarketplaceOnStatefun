package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.egress.Identifiers;
import dk.ku.dms.marketplace.egress.Messages;
import dk.ku.dms.marketplace.egress.TransactionMark;
import dk.ku.dms.marketplace.messages.shipment.ShipmentMessages;
import dk.ku.dms.marketplace.messages.shipment.UpdateShipment;
import dk.ku.dms.marketplace.messages.shipment.UpdateShipmentAck;
import dk.ku.dms.marketplace.states.ShipmentProxyState;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.EgressMessage;
import org.apache.flink.statefun.sdk.java.message.EgressMessageBuilder;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ShipmentProxyFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentProxyFn.class);

    public static final TypeName TYPE = TypeName.typeNameFromString("marketplace/shipmentProxy");

    public static final ValueSpec<ShipmentProxyState> PROXY_STATE =  ValueSpec.named("shipmentProxyState").withCustomType(ShipmentProxyState.TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(PROXY_STATE)
            .withSupplier(ShipmentProxyFn::new)
            .build();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            if (message.is(ShipmentMessages.UPDATE_SHIPMENT_TYPE)) {
                onUpdateShipments(context, message);
            }
            // ack from shipment
            else if (message.is(ShipmentMessages.UPDATE_SHIPMENT_ACK_TYPE)) {
                onProcessShipmentAck(context, message);
            }
        } catch (Exception e) {
            LOG.error("Exception in ShipmentProxyFn: " + e.getMessage());
        }
        return context.done();
    }

    private void onUpdateShipments(Context context, Message message) {
        UpdateShipment updateShipment = message.as(ShipmentMessages.UPDATE_SHIPMENT_TYPE);
        String tid = updateShipment.getTid();
        ShipmentProxyState shipmentProxyState = context.storage().get(PROXY_STATE).orElse(new ShipmentProxyState());

        int partitionNum = Constants.nShipmentPartitions;
        shipmentProxyState.tidList.put(tid, partitionNum);

        for (int i = 0; i < partitionNum; i++) {
            Message updateShipmentMsg =
                    MessageBuilder.forAddress(ShipmentFn.TYPE, String.valueOf(i))
                            .withCustomType(ShipmentMessages.UPDATE_SHIPMENT_TYPE, updateShipment)
                            .build();
            context.send(updateShipmentMsg);
        }

        context.storage().set(PROXY_STATE, shipmentProxyState);
    }

    private void onProcessShipmentAck(Context context, Message message) {
        Optional<ShipmentProxyState> stateOptional = context.storage().get(PROXY_STATE);
        if(stateOptional.isPresent()){
            ShipmentProxyState state = stateOptional.get();
            UpdateShipmentAck updateShipment = message.as(ShipmentMessages.UPDATE_SHIPMENT_ACK_TYPE);
            if(state.tidList.get(updateShipment.getTid()) != null){
                int remaining = state.tidList.computeIfPresent(updateShipment.getTid(), (k,v)-> v-1);
                if(remaining == 0){
                    TransactionMark mark = new TransactionMark(updateShipment.getTid(),
                            Enums.TransactionType.UPDATE_DELIVERY, Integer.parseInt( context.self().id() ),
                            Enums.MarkStatus.SUCCESS, "shipment");

                    final EgressMessage egressMessage =
                            EgressMessageBuilder.forEgress(Identifiers.RECEIPT_EGRESS)
                                    .withCustomType(
                                            Messages.EGRESS_RECORD_JSON_TYPE,
                                            new Messages.EgressRecord(Identifiers.RECEIPT_TOPICS, mark.toString()))
                                    .build();

                    context.send(egressMessage);
                    state.tidList.remove(updateShipment.getTid());
                }
                context.storage().set(PROXY_STATE, state);
            }
        }
    }
}

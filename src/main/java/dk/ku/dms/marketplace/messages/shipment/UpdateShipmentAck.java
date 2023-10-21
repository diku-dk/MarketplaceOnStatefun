package dk.ku.dms.marketplace.messages.shipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateShipmentAck {

    @JsonProperty("tid")
    private final String tid;

    @JsonCreator
    public UpdateShipmentAck(@JsonProperty("tid") String tid) {
        this.tid = tid;
    }

    public String getTid() {
        return tid;
    }
}

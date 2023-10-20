package dk.ku.dms.marketplace.messages.shipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateShipment {

    @JsonProperty("tid")
    private final int tid;

    @JsonCreator
    public UpdateShipment(@JsonProperty("tid") int tid) {
        this.tid = tid;
    }

}

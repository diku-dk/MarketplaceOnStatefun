package dk.ku.dms.marketplace.messages.shipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateShipment {

    @JsonProperty("tid")
    private final String tid;

    @JsonCreator
    public UpdateShipment(@JsonProperty("tid") String tid) {
        this.tid = tid;
    }

    @JsonIgnore
    public String getTid() {
        return tid;
    }

}

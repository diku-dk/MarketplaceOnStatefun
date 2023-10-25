package dk.ku.dms.marketplace.messages.shipment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GetShipments {

    @JsonProperty("customerId")
    private final int customerId;

    @JsonCreator
    public GetShipments(@JsonProperty("customerId") int customerId) {
        this.customerId = customerId;
    }

    @JsonIgnore
    public int getCustomerId() {
        return customerId;
    }
}
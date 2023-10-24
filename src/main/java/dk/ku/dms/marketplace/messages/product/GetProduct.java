package dk.ku.dms.marketplace.messages.product;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class GetProduct {

    @JsonCreator
    public GetProduct() {
    }

    @Override
    public String toString() {
        return "GetProduct{}";
    }

}
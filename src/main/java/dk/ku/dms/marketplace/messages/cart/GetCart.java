package dk.ku.dms.marketplace.messages.cart;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class GetCart {

    @JsonCreator
    public GetCart() {
    }

    @Override
    public String toString() {
        return "GetCart{}";
    }

}
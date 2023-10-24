package dk.ku.dms.marketplace.messages.customer;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class GetCustomer {

    @JsonCreator
    public GetCustomer() {
    }

    @Override
    public String toString() {
        return "GetCustomer{}";
    }

}
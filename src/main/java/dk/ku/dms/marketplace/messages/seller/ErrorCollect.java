package dk.ku.dms.marketplace.messages.seller;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ErrorCollect {
    @JsonCreator
    public ErrorCollect() {
    }

    @Override
    public String toString() {
        return "ErrorCollect{}";
    }
}

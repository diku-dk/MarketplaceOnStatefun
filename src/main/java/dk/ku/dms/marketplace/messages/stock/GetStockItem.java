package dk.ku.dms.marketplace.messages.stock;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class GetStockItem {

    @JsonCreator
    public GetStockItem() {
    }

    @Override
    public String toString() {
        return "GetStockItem{}";
    }

}
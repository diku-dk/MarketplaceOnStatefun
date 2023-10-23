package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class SellerDashboard {

    @JsonProperty("sellerView")
    private final OrderSellerView sellerView;

    @JsonProperty("orderEntries")
    private final List<OrderEntry> orderEntries;

    @JsonCreator
    public SellerDashboard(
            @JsonProperty("sellerView") OrderSellerView sellerView,
            @JsonProperty("orderEntries") List<OrderEntry> orderEntries) {
        this.sellerView = sellerView;
        this.orderEntries = orderEntries;
    }

}

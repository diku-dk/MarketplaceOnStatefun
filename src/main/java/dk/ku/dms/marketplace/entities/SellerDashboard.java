package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;


public class SellerDashboard {
    @JsonProperty("sellerView") private OrderSellerView sellerView = new OrderSellerView();
    @JsonProperty("orderEntries") private Set<OrderEntry> orderEntries  = new HashSet<>();

    public SellerDashboard() {
    }

    @JsonCreator
    public SellerDashboard(
            @JsonProperty("sellerView") OrderSellerView sellerView,
            @JsonProperty("orderEntries") Set<OrderEntry> orderEntries) {
        this.sellerView = sellerView;
        this.orderEntries = orderEntries;
    }
}

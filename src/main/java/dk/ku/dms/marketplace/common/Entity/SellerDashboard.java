package dk.ku.dms.marketplace.common.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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

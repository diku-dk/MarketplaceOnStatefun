package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderSellerView {
    @JsonProperty("sellerId") private int sellerId;

    // information below from a seller's perspective

    @JsonProperty("count_items") private int count_items = 0;
    @JsonProperty("total_amount") private float total_amount = 0;
    @JsonProperty("total_freight") private float total_freight = 0;
    @JsonProperty("total_incentive") private float total_incentive = 0;
    @JsonProperty("total_invoice") private float total_invoice = 0;
    @JsonProperty("total_items") private float total_items = 0;

    @JsonCreator
    public OrderSellerView() {
    }

    @JsonCreator
    public OrderSellerView(
            @JsonProperty("sellerId") int sellerId,
            @JsonProperty("count_items") int count_items,
            @JsonProperty("total_amount") float total_amount,
            @JsonProperty("total_freight") float total_freight,
            @JsonProperty("total_incentive") float total_incentive,
            @JsonProperty("total_invoice") float total_invoice,
            @JsonProperty("total_items") float total_items) {
          this.sellerId = sellerId;
          this.count_items = count_items;
          this.total_amount = total_amount;
          this.total_freight = total_freight;
          this.total_incentive = total_incentive;
          this.total_invoice = total_invoice;
          this.total_items = total_items;
     }
}

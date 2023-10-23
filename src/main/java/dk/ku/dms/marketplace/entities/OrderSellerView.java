package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderSellerView {
    
    @JsonProperty("sellerId") 
    private final int sellerId;

    @JsonProperty("count_orders")
    private final int count_orders;

    @JsonProperty("count_items")
    private final int count_items;

    @JsonProperty("total_amount")
    private final float total_amount;

    @JsonProperty("total_freight")
    private final float total_freight;

    @JsonProperty("total_incentive")
    private final float total_incentive;

    @JsonProperty("total_invoice")
    private final float total_invoice;

    @JsonProperty("total_items")
    private final float total_items;

    @JsonCreator
    public OrderSellerView(
            @JsonProperty("sellerId") int sellerId,
            @JsonProperty("count_orders") int count_orders,
            @JsonProperty("count_items") int count_items,
            @JsonProperty("total_amount") float total_amount,
            @JsonProperty("total_freight") float total_freight,
            @JsonProperty("total_incentive") float total_incentive,
            @JsonProperty("total_invoice") float total_invoice,
            @JsonProperty("total_items") float total_items) {
          this.sellerId = sellerId;
          this.count_orders = count_orders;
          this.count_items = count_items;
          this.total_amount = total_amount;
          this.total_freight = total_freight;
          this.total_incentive = total_incentive;
          this.total_invoice = total_invoice;
          this.total_items = total_items;
     }

    @Override
    public String toString() {
        return "OrderSellerView{" +
                " \"sellerId\" : " + sellerId +
                ", \"count_orders\" : " + count_orders +
                ", \"count_items\" : " + count_items +
                ", \"total_amount\" : " + total_amount +
                ", \"total_freight\" : " + total_freight +
                ", \"total_incentive\" : " + total_incentive +
                ", \"total_invoice\" : " + total_invoice +
                ", \"total_items\" : " + total_items +
                '}';
    }
}

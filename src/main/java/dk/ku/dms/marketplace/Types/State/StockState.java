package dk.ku.dms.marketplace.Types.State;

import dk.ku.dms.marketplace.common.Entity.StockItem;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Setter
@Getter
public class StockState {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<StockState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "StockState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, StockState.class));

    @JsonProperty("stockItems")
    public StockItem stockItem;

    public StockState() {}

    @JsonCreator
    public StockState(@JsonProperty("stockItems") StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @JsonIgnore
    public StockItem getItem() {
        return stockItem;
    }

    @JsonIgnore
    public void addStock(StockItem StockItem) {
        this.stockItem = StockItem;
    }

//    @JsonIgnore
//    public void addItem(StockItem stockItem) {
//        stockItems.put(stockItem.getProduct_id(), stockItem);
//    }
}

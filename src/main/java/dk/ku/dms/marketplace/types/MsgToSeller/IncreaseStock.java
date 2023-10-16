package dk.ku.dms.marketplace.types.MsgToSeller;

import dk.ku.dms.marketplace.common.Entity.StockItem;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Setter
@Getter
public class IncreaseStock {
//    also send this message to stock
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<IncreaseStock> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "IncreaseStock"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, IncreaseStock.class));

    @JsonProperty("stockItem")
    private StockItem stockItem;

    public IncreaseStock() {
    }

    @JsonCreator
    public IncreaseStock(
                     @JsonProperty("stockItem") StockItem stockItem) {
        this.stockItem = stockItem;
    }
}

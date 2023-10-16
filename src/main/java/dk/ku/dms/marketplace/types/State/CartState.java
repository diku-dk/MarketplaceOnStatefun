package dk.ku.dms.marketplace.types.State;

import dk.ku.dms.marketplace.common.Entity.BasketItem;
import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CartState {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<CartState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CartState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CartState.class));

    public enum Status
    {
        OPEN,
        CHECKOUT_SENT,
        PRODUCT_DIVERGENCE
    };

    @JsonProperty("status")
    public Status status;
    @JsonProperty("items")
    public Map<Integer, BasketItem> items; //     private final Map<Long, BasketItem> items;
    @JsonProperty("currentTransactionId")
    public int currentTransactionId;

    @JsonProperty("createdAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonProperty("updateAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateAt;

    public CartState() {
        this.status = Status.OPEN;
        this.items = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

//    写一个方法，接受Long和BasketItem，给items添加一个元素
    @JsonIgnore
    public void addItem(int itemId, BasketItem item) {
        if (this.items.containsKey(itemId)) {
            this.items.replace(itemId, item);
        }
        this.items.put(itemId, item);
    }

    @JsonIgnore
    public void clear() {
        this.items.clear();
        this.status = Status.OPEN;
    }

    @JsonIgnore
    public String getCartConent() {
    StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, BasketItem> entry : this.items.entrySet()) {
            sb.append("  " + entry.getValue().toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}

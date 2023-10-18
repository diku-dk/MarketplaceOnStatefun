package dk.ku.dms.marketplace.states;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class CartState {

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
    }

    @JsonProperty("status")
    private Status status;

    @JsonProperty("items")
    private final List<CartItem> items;

    @JsonProperty("createdAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime createdAt;

    @JsonProperty("updateAt")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    public CartState() {
        this.status = Status.OPEN;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @JsonIgnore
    public void seal() {
        this.items.clear();
        this.status = Status.OPEN;
        this.updatedAt = LocalDateTime.now();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
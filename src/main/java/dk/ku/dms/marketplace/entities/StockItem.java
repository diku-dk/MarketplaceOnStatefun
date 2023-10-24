package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class StockItem {

    public static final Type<StockItem> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "StockItem"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, StockItem.class));

    @JsonProperty("product_id")
    private int product_id;
    @JsonProperty("seller_id")
    private int seller_id;
    @JsonProperty("qty_available")
    private int qty_available;
    @JsonProperty("qty_reserved")
    private int qty_reserved;
    @JsonProperty("order_count")
    private int order_count;
    @JsonProperty("ytd")
    private int ytd;
    @JsonProperty("data")
    private String data;
    @JsonProperty("version")
    private String version;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonCreator
    public StockItem(@JsonProperty("product_id") int product_id,
                     @JsonProperty("seller_id") int seller_id,
                     @JsonProperty("qty_available") int qty_available,
                     @JsonProperty("qty_reserved") int qty_reserved,
                     @JsonProperty("order_count") int order_count,
                     @JsonProperty("ytd") int ytd,
                     @JsonProperty("data") String data,
                     @JsonProperty("version") String version)
    {
            this.product_id = product_id;
            this.seller_id = seller_id;
            this.qty_available = qty_available;
            this.qty_reserved = qty_reserved;
            this.order_count = order_count;
            this.ytd = ytd;
            this.version = version;
            this.data = data;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.updatedAt = now;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonIgnore
    public String getVersion() {
        return version;
    }

    @JsonIgnore
    public int getQtyAvailable() {
        return qty_available;
    }

    @JsonIgnore
    public int getQtyReserved() {
        return qty_reserved;
    }

    public void reserve(int quantity){
        this.qty_reserved += quantity;
    }

    public void confirmReservation(int quantity){
        this.qty_reserved -= quantity;
        this.qty_available += quantity;
    }

    public void cancelReservation(int quantity){
        this.qty_reserved -= quantity;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "product_id=" + product_id +
                ", seller_id=" + seller_id +
                ", qty_available=" + qty_available +
                ", qty_reserved=" + qty_reserved +
                ", order_count=" + order_count +
                ", ytd=" + ytd +
                ", data='" + data + '\'' +
                ", version='" + version + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

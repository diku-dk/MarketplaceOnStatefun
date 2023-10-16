package dk.ku.dms.marketplace.common.Entity;

import dk.ku.dms.marketplace.constants.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockItem {
    @JsonProperty("product_id") private int product_id;
    @JsonProperty("seller_id") private int seller_id;
    @JsonProperty("qty_available") private int qty_available;
    @JsonProperty("qty_reserved") private int qty_reserved;
    @JsonProperty("order_count") private int order_count;
    @JsonProperty("ytd") private int ytd;
    @JsonProperty("data") private String data;
//    @JsonProperty("is_active") private Boolean is_active;
    @JsonProperty("version") private int version;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
//    还有这些字段吗
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
                     @JsonProperty("version") int version
                     ) {
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

//    public StockItem() {
//        this.product_id = 0;
//        this.seller_id = 0;
//        this.qty_available = 0;
//        this.qty_reserved = 0;
//        this.order_count = 0;
//        this.ytd = 0;
//        this.is_active = true;
//    }

//    @Override
//    public String toString() {
//        return "StockItem{" +
//                "product_id=" + product_id +
//                ", seller_id=" + seller_id +
//                ", qty_available=" + qty_available +
//                ", qty_reserved=" + qty_reserved +
//                ", order_count=" + order_count +
//                ", ytd=" + ytd +
//                ", is_active=" + is_active +
//                '}';
//    }
}

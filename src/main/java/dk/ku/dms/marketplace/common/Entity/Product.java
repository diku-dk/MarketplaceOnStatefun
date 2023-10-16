package dk.ku.dms.marketplace.common.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Product {
    @JsonProperty("product_id") private int product_id;
    @JsonProperty("seller_id") private int seller_id;
    @JsonProperty("name") private String name;
    @JsonProperty("sku") private String sku;
    @JsonProperty("category") private String category;
    @JsonProperty("description") private String description;
    @JsonProperty("price") private float price;
    @JsonProperty("freight_value") private float freight_value;
    @JsonProperty("status") private String status = "approved";
//    @JsonProperty("active") private boolean isActive;
    @JsonProperty("version") private int version;

//    need?????????????
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonCreator
    public Product(
                   @JsonProperty("seller_id") int seller_id,
                   @JsonProperty("product_id") int product_id,
                   @JsonProperty("name") String name,
                   @JsonProperty("sku") String sku,
                   @JsonProperty("category") String category,
                   @JsonProperty("description") String description,
                   @JsonProperty("price") float price,
                   @JsonProperty("freight_value") float freight_value,
                   @JsonProperty("status") String status,
                   @JsonProperty("created_at") LocalDateTime createdAt,
                   @JsonProperty("updated_at") LocalDateTime updatedAt,
                   @JsonProperty("version") int version) {
        this.product_id = product_id;
        this.seller_id = seller_id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.description = description;
        this.price = price;
        this.freight_value = freight_value;
        this.status = status;
        this.version = version;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

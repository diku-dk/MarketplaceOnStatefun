package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class Product {

    @JsonProperty("product_id")
    private final int product_id;
    @JsonProperty("seller_id")
    private final int seller_id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("sku")
    private final String sku;
    @JsonProperty("category")
    private final String category;
    @JsonProperty("description")
    private final String description;
    @JsonProperty("price")
    private float price;
    @JsonProperty("freight_value")
    private final float freight_value;
    @JsonProperty("status")
    private final String status = "approved";
    @JsonProperty("version")
    private String version;

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
                   @JsonProperty("version") String version
                  ) {
        this.product_id = product_id;
        this.seller_id = seller_id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.description = description;
        this.price = price;
        this.freight_value = freight_value;
        this.version = version;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @JsonIgnore
    public int getProductId() {
        return product_id;
    }

    @JsonIgnore
    public int getSellerId() {
        return seller_id;
    }

    @JsonIgnore
    public String getVersion() {
        return version;
    }

    @JsonIgnore
    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", seller_id=" + seller_id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", freight_value=" + freight_value +
                ", status='" + status + '\'' +
                ", version='" + version + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

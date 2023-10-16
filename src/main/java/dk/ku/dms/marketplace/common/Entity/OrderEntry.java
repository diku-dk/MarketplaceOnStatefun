package dk.ku.dms.marketplace.common.Entity;

import dk.ku.dms.marketplace.constants.Enums;
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

// for dashboard

@Getter
@Setter
@ToString
public class OrderEntry {
    @JsonProperty("seller_id") private int seller_id;
    @JsonProperty("order_id") private int order_id;
    @JsonProperty("package_id") private int package_id;

    @JsonProperty("product_id") private int product_id;
    @JsonProperty("product_name") private String product_name = "";
    @JsonProperty("product_category") private String product_category = "";

    @JsonProperty("unit_price") private float unit_price;
    @JsonProperty("quantity") private int quantity;

    @JsonProperty("totalItems") private float totalItems;
    @JsonProperty("totalAmount") private float totalAmount;
    @JsonProperty("totalInvoice") private float totalInvoice;
    @JsonProperty("totalIncentive") private float totalIncentive = 0;
    @JsonProperty("freight_value") private float freight_value;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("shipment_date")
    private LocalDateTime shipment_date;
//
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("delivery_date")
    private LocalDateTime delivery_date;

    // denormalized, thus redundant. to avoid join on details
    // 这个字段的设计虽然在数据上是冗余的，（同样的信息在数据库中可能被存储了多次），这是为了避免在查询订单状态时需要进行联接查询（join），从而提高查询性能。这种设计策略被称为“反规范化”（denormalized）。
    // 这个两个字段是冗余的，我们来想把他们放到单独的表，但是R建议不要这样，只是两个field，增加存储没事。
    // 如果不这样，每次要查询状态再join，会慢且与Dapr的设计不符
    @JsonProperty("order_status") private Enums.OrderStatus order_status;
    @JsonProperty("delivery_status") private Enums.PackageStatus delivery_status;

    @JsonCreator
    public OrderEntry() {
    }

    @JsonCreator
    public OrderEntry(
            @JsonProperty("order_id") int order_id,
            @JsonProperty("seller_id") int seller_id,
//            @JsonProperty("package_id") long package_id,
            @JsonProperty("product_id") int product_id,
            @JsonProperty("product_name") String product_name,

            @JsonProperty("quantity") int quantity,
            @JsonProperty("totalAmount") float totalAmount,
            @JsonProperty("totalItems") float totalItems,

            @JsonProperty("freight_value") float freight_value,

            @JsonProperty("unit_price") float unit_price,
            @JsonProperty("order_status") Enums.OrderStatus order_status

    ) {
        this.seller_id = seller_id;
        this.order_id = order_id;
//        this.package_id = package_id;
        this.product_id = product_id;
        this.product_name = product_name;
//        this.product_category = product_category;
        this.unit_price = unit_price;
        this.quantity = quantity;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
//        this.totalInvoice = totalInvoice;
        this.totalIncentive = totalIncentive;
        this.freight_value = freight_value;
//        this.shipment_date = shipment_date;
//        this.delivery_date = delivery_date;
        this.order_status = order_status;
//        this.delivery_status = delivery_status;
    }
}

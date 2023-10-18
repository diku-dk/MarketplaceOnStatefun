package dk.ku.dms.marketplace.messages.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.entities.Product;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class ProductMessages {

    public static final Type<Product> ADD_PRODUCT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "addProduct"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Product.class));

    public static final Type<Product> UPDATE_PRODUCT_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "updateProduct"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, Product.class));

    public static final Type<UpdatePrice> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdatePrice"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, UpdatePrice.class));

    public final static class UpdatePrice {


        public static final Type<UpdatePrice> TYPE =
                SimpleType.simpleImmutableTypeFrom(
                        TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "UpdatePrice"),
                        mapper::writeValueAsBytes,
                        bytes -> mapper.readValue(bytes, UpdatePrice.class));

        @JsonProperty("sellerId")
        private final int sellerId;
        @JsonProperty("productId")
        private final int productId;
        @JsonProperty("price")
        private final float price;
        @JsonProperty("instanceId")
        private final String instanceId;

        @JsonCreator
        public UpdatePrice(@JsonProperty("sellerId") int sellerId,
                           @JsonProperty("productId") int productId,
                           @JsonProperty("price") float price,
                           @JsonProperty("instanceId") String instanceId) {
            this.sellerId = sellerId;
            this.productId = productId;
            this.price = price;
            this.instanceId = instanceId;
        }

        public int getSellerId() {
            return sellerId;
        }

        public int getProductId() {
            return productId;
        }

        public float getPrice() {
            return price;
        }

        public String getInstanceId() {
            return instanceId;
        }

        @Override
        public String toString() {
            return "UpdatePrice{" +
                    "sellerId=" + sellerId +
                    ", productId=" + productId +
                    ", price=" + price +
                    ", instanceId='" + instanceId + '\'' +
                    '}';
        }
    }

}

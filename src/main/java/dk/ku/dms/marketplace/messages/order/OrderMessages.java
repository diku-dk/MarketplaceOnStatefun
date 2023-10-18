package dk.ku.dms.marketplace.messages.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.entities.CartItem;
import dk.ku.dms.marketplace.entities.CustomerCheckout;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;
import java.util.List;

import static dk.ku.dms.marketplace.utils.Constants.mapper;

public final class OrderMessages {

    public static final Type<AttemptReservationResponse> ATTEMPT_RESERVATION_RESPONSE_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "AttemptReservationResponse"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, AttemptReservationResponse.class));

    public static final Type<CheckoutRequest> CHECKOUT_REQUEST_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "CheckoutRequest"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, CheckoutRequest.class));

    public static final class AttemptReservationResponse {

        @JsonProperty("sellerId")
        public int sellerId;

        @JsonProperty("productId")
        public int productId;

        @JsonProperty("status")
        public Enums.ItemStatus status;

        @JsonCreator
        public AttemptReservationResponse(@JsonProperty("sellerId") int sellerId,
                                          @JsonProperty("productId") int productId,
                                          @JsonProperty("status") Enums.ItemStatus status)
        {
            this.sellerId = sellerId;
            this.productId = productId;
            this.status = status;
        }

        public int getSellerId() {
            return sellerId;
        }

        public int getProductId() {
            return productId;
        }

        public Enums.ItemStatus getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "AttemptReservationResponse{" +
                    "sellerId=" + sellerId +
                    ", productId=" + productId +
                    ", status=" + status +
                    '}';
        }
    }

    public static final class CheckoutRequest {

        private final LocalDateTime timestamp;

        private final CustomerCheckout customerCheckout;

        private final List<CartItem> items;

        private final String instanceId;

        public CheckoutRequest(@JsonProperty("timestamp") LocalDateTime timestamp,
                               @JsonProperty("customerCheckout") CustomerCheckout customerCheckout,
                               @JsonProperty("items") List<CartItem> items,
                               @JsonProperty("instanceId") String instanceId) {
            this.timestamp = timestamp;
            this.customerCheckout = customerCheckout;
            this.items = items;
            this.instanceId = instanceId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public CustomerCheckout getCustomerCheckout() {
            return customerCheckout;
        }

        public List<CartItem> getItems() {
            return items;
        }

        public String getInstanceId() {
            return instanceId;
        }

        @Override
        public String toString() {
            return "CheckoutRequest{" +
                    "timestamp=" + timestamp +
                    ", customerCheckout=" + customerCheckout +
                    ", items=" + items +
                    ", instanceId='" + instanceId + '\'' +
                    '}';
        }
    }

}

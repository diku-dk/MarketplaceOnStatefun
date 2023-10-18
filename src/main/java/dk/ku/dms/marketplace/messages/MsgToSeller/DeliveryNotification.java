package dk.ku.dms.marketplace.messages.MsgToSeller;

import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.time.LocalDateTime;

import static dk.ku.dms.marketplace.utils.Constants.mapper;


public class DeliveryNotification {
    

    public static final Type<DeliveryNotification> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "DeliveryNotification"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, DeliveryNotification.class));

    @JsonProperty("orderId")
    private int orderId;

    @JsonProperty("customerId")
    private int customerId;

    @JsonProperty("sellerId")
    private int sellerId;

    @JsonProperty("packageId")
    private int packageId;

    @JsonProperty("productID")
    private int productID;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("Status")
    private Enums.PackageStatus packageStatus;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonProperty("eventDate")
    private LocalDateTime eventDate;

    @JsonCreator
    public DeliveryNotification(@JsonProperty("customerID") int customerID,
                                @JsonProperty("orderId") int orderId,
                                @JsonProperty("packageId") int packageId,
                                @JsonProperty("sellerId") int sellerId,
                                @JsonProperty("productID") int productID,
                                @JsonProperty("productName") String productName,
                                @JsonProperty("Status") Enums.PackageStatus packageStatus,
                                @JsonProperty("eventDate") LocalDateTime eventDate) {
        this.orderId = orderId;
        this.customerId = customerID;
        this.sellerId = sellerId;
        this.packageId = packageId;
        this.productID = productID;
        this.productName = productName;
        this.packageStatus = packageStatus;
        this.eventDate = eventDate;
    }
}


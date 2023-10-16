package dk.ku.dms.marketplace.common.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Customer {

    @JsonProperty("id") private int customerId;
    @JsonProperty("first_name") private String firstName;
    @JsonProperty("last_name") private String lastName;
    @JsonProperty("address") private String address;
    @JsonProperty("complement") private String complement;
    @JsonProperty("birth_date") private String birth_date;
    @JsonProperty("zip_code") private String zipCode;
    @JsonProperty("city") private String city;
    @JsonProperty("state") private String state;
    @JsonProperty("card_number") private String cardNumber;
    @JsonProperty("card_security_number") private String cardSecurityNumber;
    @JsonProperty("card_expiration") private String cardExpiration;
    @JsonProperty("card_holder_name") private String cardHolderName;
    @JsonProperty("card_type") private String cardType;
    @JsonProperty("data") private String data;

    @JsonProperty("success_payment_count") private int successPaymentCount;
    @JsonProperty("failed_payment_count") private int failedPaymentCount;
    @JsonProperty("delivery_count") private int deliveryCount;

//    ?
    @JsonProperty("pendingDeliveriesCount") private int pendingDeliveriesCount = 0;
//    @JsonProperty("totalSpentItems") private BigDecimal totalSpentItems;
//    @JsonProperty("totalSpentFreights") private BigDecimal totalSpentFreights;
    @JsonProperty("name") private String name; // need to delete
//    ??
    @JsonProperty("abandoned_cart_count") private int abandonedCartCount = 0;

    @JsonCreator
    public Customer(
            @JsonProperty("id") int customerId,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("address") String address,
            @JsonProperty("complement") String complement,
            @JsonProperty("birth_date") String birth_date,
            @JsonProperty("zip_code") String zipCode,
            @JsonProperty("city") String city,
            @JsonProperty("state") String state,
            @JsonProperty("card_number") String cardNumber,
            @JsonProperty("card_security_number") String cardSecurityNumber,
            @JsonProperty("card_expiration") String cardExpiration,
            @JsonProperty("card_holder_name") String cardHolderName,
            @JsonProperty("card_type") String cardType,
            @JsonProperty("data") String data,
            @JsonProperty("success_payment_count") int successPaymentCount,
            @JsonProperty("failed_payment_count") int failedPaymentCount,
            @JsonProperty("pendingDeliveriesCount") int pendingDeliveriesCount,
            @JsonProperty("abandoned_cart_count") int abandonedCartCount,
            @JsonProperty("delivery_count") int deliveryCount
            ) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = firstName + " " + lastName;
        this.address = address;
        this.complement = complement;
        this.birth_date = birth_date;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.cardNumber = cardNumber;
        this.cardSecurityNumber = cardSecurityNumber;
        this.cardExpiration = cardExpiration;
        this.cardHolderName = cardHolderName;
        this.cardType = cardType;
        this.data = data;
        this.successPaymentCount = successPaymentCount;
        this.failedPaymentCount = failedPaymentCount;
        this.pendingDeliveriesCount = 0;
        this.deliveryCount = deliveryCount;
//        this.totalSpentItems = BigDecimal.ZERO;
//        this.totalSpentFreights = BigDecimal.ZERO;
    }
}

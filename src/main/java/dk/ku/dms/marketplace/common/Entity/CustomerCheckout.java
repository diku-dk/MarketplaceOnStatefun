package dk.ku.dms.marketplace.common.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CustomerCheckout {

    @JsonProperty("CustomerId") private int customerId;
    @JsonProperty("FirstName") private String firstName;
    @JsonProperty("LastName") private String lastName;
    @JsonProperty("Street") private String street;
    @JsonProperty("Complement") private String complement;
    @JsonProperty("City") private String city;
    @JsonProperty("State") private String state;
    @JsonProperty("ZipCode") private String zipCode;
    @JsonProperty("PaymentType") private String paymentType;
    @JsonProperty("CardNumber") private String cardNumber;
    @JsonProperty("CardHolderName") private String cardHolderName;
    @JsonProperty("CardExpiration") private String cardExpiration;
    @JsonProperty("CardSecurityNumber") private String cardSecurityNumber;
    @JsonProperty("CardBrand") private String cardBrand;
    @JsonProperty("Installments") private int installments;
    @JsonProperty("instanceId") private int instanceId;

    public CustomerCheckout() {
    }

    @JsonCreator
    public CustomerCheckout(
            @JsonProperty("CustomerId") int customerId,
            @JsonProperty("FirstName") String firstName,
            @JsonProperty("LastName") String lastName,
            @JsonProperty("Street") String street,
            @JsonProperty("Complement") String complement,
            @JsonProperty("City") String city,
            @JsonProperty("State") String state,
            @JsonProperty("ZipCode") String zipCode,
            @JsonProperty("PaymentType") String paymentType,
            @JsonProperty("CardNumber") String cardNumber,
            @JsonProperty("CardHolderName") String cardHolderName,
            @JsonProperty("CardExpiration") String cardExpiration,
            @JsonProperty("CardSecurityNumber") String cardSecurityNumber,
            @JsonProperty("CardBrand") String cardBrand,
            @JsonProperty("Installments") int installments,
            @JsonProperty("instanceId") int instanceId) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.paymentType = paymentType;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.cardExpiration = cardExpiration;
        this.cardSecurityNumber = cardSecurityNumber;
        this.cardBrand = cardBrand;
        this.installments = installments;
        this.instanceId = instanceId;
    }
}

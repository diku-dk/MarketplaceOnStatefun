package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CustomerCheckout {

    @JsonProperty("CustomerId")
    private final int customerId;

    @JsonProperty("FirstName")
    private final String firstName;

    @JsonProperty("LastName")
    private final String lastName;

    @JsonProperty("Street")
    private final String street;

    @JsonProperty("Complement")
    private final String complement;

    @JsonProperty("City")
    private final String city;

    @JsonProperty("State")
    private final String state;

    @JsonProperty("ZipCode")
    private final String zipCode;

    @JsonProperty("PaymentType")
    private final String paymentType;

    @JsonProperty("CardNumber")
    private final String cardNumber;

    @JsonProperty("CardHolderName")
    private final String cardHolderName;

    @JsonProperty("CardExpiration")
    private final String cardExpiration;

    @JsonProperty("CardSecurityNumber")
    private final String cardSecurityNumber;

    @JsonProperty("CardBrand")
    private final String cardBrand;

    @JsonProperty("Installments")
    private final int installments;

    @JsonProperty("instanceId")
    private final int instanceId;

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

    public int getInstanceId() {
        return instanceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStreet() {
        return street;
    }

    public String getComplement() {
        return complement;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardExpiration() {
        return cardExpiration;
    }

    public String getCardSecurityNumber() {
        return cardSecurityNumber;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public int getInstallments() {
        return installments;
    }

    @Override
    public String toString() {
        return "CustomerCheckout{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", complement='" + complement + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardExpiration='" + cardExpiration + '\'' +
                ", cardSecurityNumber='" + cardSecurityNumber + '\'' +
                ", cardBrand='" + cardBrand + '\'' +
                ", installments=" + installments +
                ", instanceId=" + instanceId +
                '}';
    }
}

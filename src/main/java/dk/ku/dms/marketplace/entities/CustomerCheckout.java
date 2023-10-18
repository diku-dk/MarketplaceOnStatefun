package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private final String instanceId;

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
            @JsonProperty("instanceId") String instanceId) {
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

    @JsonIgnore
    public String getInstanceId() {
        return instanceId;
    }

    @JsonIgnore
    public int getCustomerId() {
        return customerId;
    }

    @JsonIgnore
    public String getFirstName() {
        return firstName;
    }

    @JsonIgnore
    public String getLastName() {
        return lastName;
    }

    @JsonIgnore
    public String getStreet() {
        return street;
    }

    @JsonIgnore
    public String getComplement() {
        return complement;
    }

    @JsonIgnore
    public String getCity() {
        return city;
    }

    @JsonIgnore
    public String getState() {
        return state;
    }

    @JsonIgnore
    public String getZipCode() {
        return zipCode;
    }

    @JsonIgnore
    public String getPaymentType() {
        return paymentType;
    }

    @JsonIgnore
    public String getCardNumber() {
        return cardNumber;
    }

    @JsonIgnore
    public String getCardHolderName() {
        return cardHolderName;
    }

    @JsonIgnore
    public String getCardExpiration() {
        return cardExpiration;
    }

    @JsonIgnore
    public String getCardSecurityNumber() {
        return cardSecurityNumber;
    }

    @JsonIgnore
    public String getCardBrand() {
        return cardBrand;
    }

    @JsonIgnore
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

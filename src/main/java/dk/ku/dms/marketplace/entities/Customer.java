package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class Customer {

    public static final Type<Customer> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "Customer"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, Customer.class));

    @JsonProperty("id")
    private int id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("complement")
    private String complement;

    @JsonProperty("birth_date")
    private String birthDate;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("card_security_number")
    private String cardSecurityNumber;

    @JsonProperty("card_expiration")
    private String cardExpiration;

    @JsonProperty("card_holder_name")
    private String cardHolderName;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("data")
    private String data;

    @JsonProperty("success_payment_count")
    private int successPaymentCount;

    @JsonProperty("failed_payment_count")
    private int failedPaymentCount;

    @JsonProperty("delivery_count")
    private int deliveryCount;

    @JsonCreator
    public Customer(
            @JsonProperty("id") int id,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("address") String address,
            @JsonProperty("complement") String complement,
            @JsonProperty("birth_date") String birthDate,
            @JsonProperty("zip_code") String zipCode,
            @JsonProperty("city") String city,
            @JsonProperty("state") String state,
            @JsonProperty("card_number") String cardNumber,
            @JsonProperty("card_security_number") String cardSecurityNumber,
            @JsonProperty("card_expiration") String cardExpiration,
            @JsonProperty("card_holder_name") String cardHolderName,
            @JsonProperty("card_type") String cardType,
            @JsonProperty("data") String data) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.complement = complement;
        this.birthDate = birthDate;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.cardNumber = cardNumber;
        this.cardSecurityNumber = cardSecurityNumber;
        this.cardExpiration = cardExpiration;
        this.cardHolderName = cardHolderName;
        this.cardType = cardType;
        this.data = data;
        this.successPaymentCount = 0;
        this.failedPaymentCount = 0;
        this.deliveryCount = 0;
    }

    @JsonCreator
    public Customer() { }

    public void incrementSuccessPaymentCount() {
        this.successPaymentCount++;
    }

    public void incrementFailedPaymentCount() {
        this.failedPaymentCount++;
    }

    public void incrementDeliveryCount() {
        this.deliveryCount++;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", complement='" + complement + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardSecurityNumber='" + cardSecurityNumber + '\'' +
                ", cardExpiration='" + cardExpiration + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardType='" + cardType + '\'' +
                ", data='" + data + '\'' +
                ", successPaymentCount=" + successPaymentCount +
                ", failedPaymentCount=" + failedPaymentCount +
                ", deliveryCount=" + deliveryCount +
                '}';
    }
}

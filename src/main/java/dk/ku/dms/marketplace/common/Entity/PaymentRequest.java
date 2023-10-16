package dk.ku.dms.marketplace.common.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PaymentRequest {
    @JsonProperty("customer") private String customer;
    @JsonProperty("amount") private float amount;
//    @JsonProperty("paymentMethod") private String paymentMethod;
    @JsonProperty("idempotencyKey") private String idempotencyKey;
    @JsonProperty("cardNumber") private String cardNumber;
    @JsonProperty("Cvc") private String Cvc;
    @JsonProperty("expirationDate") private String expirationDate;

    @JsonCreator
    public PaymentRequest(@JsonProperty("amount") float amount,
//                          @JsonProperty("paymentMethod") String paymentMethod,
                          @JsonProperty("idempotencyKey") String idempotencyKey,
                          @JsonProperty("customer") String customer,
                          @JsonProperty("cardNumber") String cardNumber,
                          @JsonProperty("Cvc") String Cvc,
                          @JsonProperty("expirationDate") String expirationDate) {
        this.customer = customer;
        this.amount = amount;
//        this.paymentMethod = paymentMethod;
        this.idempotencyKey = idempotencyKey;
        this.cardNumber = cardNumber;
        this.Cvc = Cvc;
        this.expirationDate = expirationDate;
    }
}

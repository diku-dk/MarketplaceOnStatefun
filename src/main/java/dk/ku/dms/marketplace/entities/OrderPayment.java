package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Enums;

public class OrderPayment {
    @JsonProperty("orderId")
    private final int orderId;

    // e.g.  1 - credit card  2 - coupon
    @JsonProperty("sequential")
    private final int sequential;

    @JsonProperty("type")
    private final Enums.PaymentType type;

    // number of times the credit card is charged (usually once a month)
    @JsonProperty("installments")
    private final int installments;

    // respective to this line (ie. coupon)
    @JsonProperty("value")
    private final float value;

    // vouchers dont need to have this field filled
    @JsonProperty("status")
    private final Enums.PaymentStatus status;

    @JsonCreator
    public OrderPayment(
            @JsonProperty("orderId") int orderId,
            @JsonProperty("sequential") int sequential,
            @JsonProperty("type") Enums.PaymentType type,
            @JsonProperty("installments") int installments,
            @JsonProperty("value") float value,
            @JsonProperty("status") Enums.PaymentStatus status) {
        this.orderId = orderId;
        this.sequential = sequential;
        this.type = type;
        this.installments = installments;
        this.value = value;
        this.status = status;
    }



}

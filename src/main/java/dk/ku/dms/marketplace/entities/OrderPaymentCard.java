package dk.ku.dms.marketplace.entities;

public class OrderPaymentCard {
    
    public int order_id;
    public int payment_sequential;
    
    public String card_number;

    public String card_holder_name;

    public String card_expiration;

    public String card_brand;

    public OrderPaymentCard(int order_id, int payment_sequential, String card_number, String card_holder_name, String card_expiration, String card_brand) {
        this.order_id = order_id;
        this.payment_sequential = payment_sequential;
        this.card_number = card_number;
        this.card_holder_name = card_holder_name;
        this.card_expiration = card_expiration;
        this.card_brand = card_brand;
    }
}

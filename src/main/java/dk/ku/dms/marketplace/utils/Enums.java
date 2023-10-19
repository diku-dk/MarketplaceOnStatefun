package dk.ku.dms.marketplace.utils;

public class Enums {

    public enum MarkStatus
    {
        SUCCESS,
        ERROR,
        ABORT,
        NOT_ACCEPTED
    };

    public enum SendType
    {
        ProductFn,
        StockFn,
        None
    }

    public enum ItemStatus
    {
        UNAVAILABLE,
        OUT_OF_STOCK,
        PRICE_DIVERGENCE,
        IN_STOCK,
        // Strictly speaking it is not item status, but used as a placeholder.
        UNKNOWN
    }



    public enum OrderStatus
    {
        INVOICED,
        SHIPPED,
        DELIVERED,
        PAYMENT_FAILED,
        PAYMENT_PROCESSED,
        READY_FOR_SHIPMENT,
        IN_TRANSIT
    }

    public enum PackageStatus
    {
        CREATED,
        SHIPPED,
        DELIVERED
    }

    public enum CustomerNotificationType
    {
        notify_failed_payment,
        notify_success_payment,
//        notify_shipment,
//        notfiy_delivered,
        notify_fail_checkout
    }

    public enum ShipmentStatus
    {
        APPROVED,
        CONCLUDED,
        DELIVERY_IN_PROGRESS
    }

    public enum PaymentStatus
    {
        requires_payment_method,
        succeeded,
        canceled
    }

    public enum PaymentType
    {
        CREDIT_CARD,
        DEBIT_CARD,
        BOLETO,
        VOUCHER
    }

    public enum TransactionType
    {
        CUSTOMER_SESSION,
        QUERY_DASHBOARD,
        PRICE_UPDATE,

        UPDATE_PRODUCT,
        UPDATE_DELIVERY,
    }
}

package dk.ku.dms.marketplace.constants;

public class Enums {
//    public enum TaskType
//    {
//        AddProductType,
//        UpdatePriceType,
//        GetAllProductsType,
//        DeleteProductType
//    };

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
    };

    public enum ItemStatus
    {
        UNAVAILABLE, // deleted from DB
        OUT_OF_STOCK, //
        PRICE_DIVERGENCE,
        IN_STOCK,
        // Strictly speaking it is not item status, but used as a placeholder.
        UNKNOWN
    };

    public enum OrderStatus
    {
        INVOICED,
        SHIPPED,
        DELIVERED,
        PAYMENT_FAILED,
        PAYMENT_PROCESSED,
        READY_FOR_SHIPMENT,
        IN_TRANSIT
    };

    public enum PackageStatus
    {
        CREATED,
        SHIPPED,
        DELIVERED
    }

    public enum NotificationType
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

    public enum PaymentType
    {
        CREDIT_CARD,
        DEBIT_CARD,
        BOLETO,
        VOUCHER
    }

    public enum TransactionType
    {
        checkoutTask,
        queryDashboardTask,
        updatePriceTask,
        updateDeliveryTask,
        updateProductTask
    }
}

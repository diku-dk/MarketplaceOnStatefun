package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.common.Entity.BasketItem;
import dk.ku.dms.marketplace.common.Entity.StockItem;
import dk.ku.dms.marketplace.common.Utils.Utils;
import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.constants.Enums;
import dk.ku.dms.marketplace.Types.MsgToSeller.UpdateProduct;
import dk.ku.dms.marketplace.Types.MsgToSeller.IncreaseStock;
import dk.ku.dms.marketplace.Types.MsgToStock.ReserveStockEvent;
import dk.ku.dms.marketplace.Types.MsgToStock.ConfirmStockEvent;
import dk.ku.dms.marketplace.Types.State.StockState;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class StockFn implements StatefulFunction {

    Logger logger = Logger.getLogger("StockFn");

    static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNS_NAMESPACE, "stock");

    static final ValueSpec<StockState> STOCKSTATE = ValueSpec.named("stock").withCustomType(StockState.TYPE);

    //  Contains all the information needed to create a function instance
    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(STOCKSTATE)
            .withSupplier(StockFn::new)
            .build();

    private String getPartionText(String id) {
        return String.format("[ StockFn partitionId %s ] ", id);
    }

    static final TypeName KFK_EGRESS = TypeName.typeNameOf("e-commerce.fns", "kafkaSink");

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // seller ---> stock (increase stock)
            if (message.is(IncreaseStock.TYPE)) {
                addStockItem(context, message);
            }
            // product ---> stock (delete product)
            else if (message.is(UpdateProduct.TYPE)) {
                onUpdateProduct(context, message);
            }
            // order ---> stock (attempt reservsation)
            else if (message.is(ReserveStockEvent.TYPE)) {
                onHandleCheckoutResv(context, message);
            }
            // payment ---> stock (payment result finally decided change stock or not
            else if (message.is(ConfirmStockEvent.TYPE)) {
                onHandlePaymentResv(context, message);
            }
        } catch (Exception e) {
//            System.out.println("StockFn apply error !!!!!!!!!!!!!!!");
//            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return context.done();
    }

    private StockState getStockState(Context context) {
        return context.storage().get(STOCKSTATE).orElse(new StockState());
    }

    private void showLog(String log) {
        logger.info(log);
//        System.out.println(log);
    }

    private void printLog(String log) {
        System.out.println(log);
    }

    private void addStockItem(Context context, Message message) {
        IncreaseStock increaseStock = message.as(IncreaseStock.TYPE);
        StockItem stockItem = increaseStock.getStockItem();
        int productId = stockItem.getProduct_id();

//        int num = stockItem.getQty_available();
        StockState stockState = getStockState(context);
        stockState.addStock(stockItem);
        context.storage().set(STOCKSTATE, stockState);

        String log = String.format(getPartionText(context.self().id())
                        + "addStockItem success, "
                        + "productId: %s\n"
                , productId);
        printLog(log);
    }

    private void onUpdateProduct(Context context, Message message) {
        StockState stockState = getStockState(context);
        UpdateProduct updateProduct = message.as(UpdateProduct.TYPE);
        int productId = updateProduct.getProduct_id();
        StockItem stockItem = stockState.getItem();
//        String result = "fail";

        Enums.MarkStatus markStatus = Enums.MarkStatus.ERROR;
        if (stockItem == null) {
            String log = String.format(getPartionText(context.self().id())
                            + "update product failed as product not exist\n"
                            + "productId: %s\n"
                    , productId);
            logger.warning(log);
        } else {
            stockItem.setUpdatedAt(LocalDateTime.now());
//            stockItem.setIs_active(false);
            stockItem.setVersion(stockItem.getVersion());
//            result = "success";
            markStatus = Enums.MarkStatus.SUCCESS;
            context.storage().set(STOCKSTATE, stockState);

            String log = String.format(getPartionText(context.self().id())
                            + "update product success (stock part)\n"
                            + "productId: %s\n"
                    , productId);
//            showLog(log);
        }

        int tid = updateProduct.getVersion();
        int sellerId = updateProduct.getSeller_id();

        Utils.notifyTransactionComplete(context,
                Enums.TransactionType.updateProductTask.toString(),
                context.self().id(),
                productId,
                tid,
                String.valueOf(sellerId),
                markStatus,
                "stock");

        String log_ = getPartionText(context.self().id())
                + "update product success, " + "tid : " + updateProduct.getVersion() + "\n";
        printLog(log_);
//        logger.info("[success] {tid=" + deleteProduct.getInstanceId() + "} delete product, stockFn " + context.self().id());
    }

    private void onHandleCheckoutResv(Context context, Message message) {
        ReserveStockEvent reserveStockEvent = message.as(ReserveStockEvent.TYPE);

        BasketItem basketItem = reserveStockEvent.getItem();
        int productId = basketItem.getProductId();
        int quantity = basketItem.getQuantity();
        int customerId = reserveStockEvent.getCustomerId();
        int version = basketItem.getVersion();

        Enums.ItemStatus itemStatus = onAtptResvReq(context, productId, quantity, customerId, version);
        reserveStockEvent.setItemStatus(itemStatus);

        Utils.sendMessageToCaller(
                context,
                ReserveStockEvent.TYPE,
                reserveStockEvent);
    }

    private Enums.ItemStatus onAtptResvReq(Context context, int productId, int quantity, int customerId, int version) {
        StockState stockState = getStockState(context);
        StockItem stockItem = stockState.getItem();

        String partitionText = getPartionText(context.self().id());
        String productIdText = "productId: " + productId;

        if (stockItem.getVersion() != version) {
            String log = partitionText + " #sub-task#, attempt reservation request failed as product version not match\n"
                    + productIdText
                    + ", " + "customerId: " + customerId + "\n";
//            showLog(log);
            return Enums.ItemStatus.UNAVAILABLE;
        }
        if (stockItem.getQty_available() - stockItem.getQty_reserved() < quantity) {
            String log = partitionText + " #sub-task#, attempt reservation request failed as stock not enough\n"
                    + productIdText
                    + ", " + "customerId: " + customerId
                    + ", " + "qty_available: " + stockItem.getQty_available()
                    + ", " + "need: " + quantity + "\n";

//            showLog(log);
            return Enums.ItemStatus.OUT_OF_STOCK;
        } else {
            stockItem.setQty_reserved(stockItem.getQty_reserved() + quantity);
            stockItem.setUpdatedAt(LocalDateTime.now());
            context.storage().set(STOCKSTATE, stockState);
            String log = partitionText + " #sub-task#, attempt reservation request success\n"
                    + productIdText
                    + ", " + "customerId: " + customerId
                    + ", " + "qty_available: " + stockItem.getQty_available()
                    + ", need: " + quantity + "\n";
//            showLog(log);
            return Enums.ItemStatus.IN_STOCK;
        }
    }

    private void onCancelResvReq(Context context, int productId, int quantity) {
        StockState stockState = getStockState(context);
        StockItem stockItem = stockState.getItem();
        stockItem.setQty_reserved(stockItem.getQty_reserved() - quantity);
        stockItem.setUpdatedAt(LocalDateTime.now());
        context.storage().set(STOCKSTATE, stockState);
    }

    private void paymentFail(Context context, int productId, int quantity) {
        onCancelResvReq(context, productId, quantity);
    }

    private void paymentConfirm(Context context, int productId, int quantity) {
        // increase order count
        StockState stockState = getStockState(context);
        StockItem stockItem = stockState.getItem();
        // 之前忘写了
        stockItem.setQty_reserved(stockItem.getQty_reserved() - quantity);
        stockItem.setQty_available(stockItem.getQty_available() - quantity);
        stockItem.setOrder_count(stockItem.getOrder_count() + 1);
        stockItem.setUpdatedAt(LocalDateTime.now());
        context.storage().set(STOCKSTATE, stockState);
    }

    private void onHandlePaymentResv(Context context, Message message){
        ConfirmStockEvent confirmStockEvent = message.as(ConfirmStockEvent.TYPE);
        int productId = confirmStockEvent.getProductId();
        int quantity = confirmStockEvent.getQuantity();
        Enums.OrderStatus orderStatus = confirmStockEvent.getOrderStatus();
        String uniqueId = confirmStockEvent.getUniqueOrderId();

        String log = String.format(getPartionText(context.self().id())
                + "StockFn apply PaymentResv, productId: %s, uniqueOrderId: %s", productId, uniqueId);
//        showLog(log);

        if (orderStatus == Enums.OrderStatus.PAYMENT_PROCESSED) {
//            NOTE: NOT call onConfirmResvReq here
            paymentConfirm(context, productId, quantity);
        } else {
            paymentFail(context, productId, quantity);
        }
    }
}







package Marketplace.Funs;

import Common.Entity.Customer;
import Common.Entity.Order;
import Marketplace.Constant.Constants;
import Marketplace.Constant.Enums;
import Marketplace.Types.MsgToCartFn.Cleanup;
import Marketplace.Types.MsgToCustomer.*;
import Marketplace.Types.MsgToSeller.DeliveryNotification;
import Marketplace.Types.State.CustomerState;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class CustomerFn implements StatefulFunction {

    Logger logger = Logger.getLogger("CustomerFn");

    static final TypeName TYPE = TypeName.typeNameOf(Constants.FUNS_NAMESPACE, "customer");

    static final ValueSpec<CustomerState> CUSTOMERSTATE = ValueSpec.named("customer").withCustomType(CustomerState.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(CUSTOMERSTATE)
            .withSupplier(CustomerFn::new)
            .build();

    private String getPartionText(String id) {
        return String.format("[ CustomerFn partitionId %s ] ", id);
    }

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // client ---> customer (init customer type)
            if (message.is(InitCustomer.TYPE)) {
                onInitCustomer(context, message);
            }
            // client ---> seller (get seller type)
            else if (message.is(GetCustomer.TYPE)) {
                onGetCustomer(context, message);
            }
            // ShippmentFn ---> customer (notify shipped type)
            // OrderFn / PaymentFn ---> customer (notify failed payment type)
            // PaymentFn ---> customer (notify success payment type)
            else if (message.is(NotifyCustomer.TYPE)) {
                onhandleNotifyCustomer(context, message);
            }
            else if (message.is(DeliveryNotification.TYPE)) {
                onhandleDeliveryNotification(context, message);
            }
//            else if (message.is(Cleanup.TYPE)) {
//                onCleanup(context);
//            }
            else {
                System.out.println("do nothing");
            }
        } catch (Exception e) {
            System.out.println("Exception in CustomerFn !!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
        return context.done();
    }

    private void showLog(String log) {
        logger.info(log);
//        System.out.println(log);
    }

    private void printLog(String log) {
        System.out.println(log);
    }

    private CustomerState getCustomerState(Context context) {
        return context.storage().get(CUSTOMERSTATE).orElse(new CustomerState());
    }

    private void onInitCustomer(Context context, Message message) {
        InitCustomer initCustomer = message.as(InitCustomer.TYPE);
        Customer customer = initCustomer.getCustomer();
        CustomerState customerState = getCustomerState(context);
        customerState.addCustomer(customer);

        context.storage().set(CUSTOMERSTATE, customerState);

        String log = String.format(getPartionText(context.self().id())
                        + "init customer success, "
                        + "customer ID: %s\n"
                , customer.getCustomerId());
        printLog(log);
    }

    private void onGetCustomer(Context context, Message message) {
        GetCustomer getCustomer = message.as(GetCustomer.TYPE);
        long customerId = getCustomer.getCustomerId();
        CustomerState customerState = getCustomerState(context);
        Customer customer = customerState.getCustomerById(customerId);

        if (customer == null) {
            String log = String.format(getPartionText(context.self().id())
                    + "get customer failed as customer doesnt exist\n"
            );
            showLog(log);
            return;
        }

        String log = String.format(getPartionText(context.self().id())
                + "get customer success\n"
                + customer.toString()
                + "\n"
        );
        showLog(log);
    }

    private void onhandleNotifyCustomer(Context context, Message message) {
        NotifyCustomer notifyCustomer = message.as(NotifyCustomer.TYPE);
        long customerId = notifyCustomer.getCustomerId();
        Order order = notifyCustomer.getOrder();
        Enums.NotificationType notificationType = notifyCustomer.getNotifyType();

        CustomerState customerState = getCustomerState(context);
        Customer customer = customerState.getCustomerById(customerId);

        String notificationInfo = "";
        int statistic = 0;
        String statisticInfo = "";

        switch (notificationType) {
            case notify_success_payment:
                customer.setSuccessPaymentCount(customer.getSuccessPaymentCount() + 1);
                notificationInfo = "[ notify success payment ] ";
                statistic = customer.getSuccessPaymentCount();
                statisticInfo = "successful payment count : ";
                break;
            // use in 2 case: fail order and fail payment
            case notify_fail_checkout:
                customer.setFailedPaymentCount(customer.getAbandonedCartCount() + 1);
                notificationInfo = "[ notify failed checkout ] ";
                statistic = customer.getAbandonedCartCount();
                statisticInfo = "failed checkout count : ";
                break;
            case notify_failed_payment:
                customer.setFailedPaymentCount(customer.getFailedPaymentCount() + 1);
                notificationInfo = "[ notify failed payment ] ";
                statistic = customer.getFailedPaymentCount();
                statisticInfo = "failed payment count : ";
                break;
        }

        context.storage().set(CUSTOMERSTATE, customerState);
        String log = String.format(getPartionText(context.self().id())
                        + notificationInfo
                        + ", customer ID: " + customer.getCustomerId() + ", "
                        + statisticInfo + statistic + "\n"
                        );
        if (order != null) {
            log += "order ID: " + order.toString() + "\n";
        }
        showLog(log);
    }

    private void onhandleDeliveryNotification(Context context, Message message) {
        CustomerState customerState = getCustomerState(context);
        DeliveryNotification deliveryNotification = message.as(DeliveryNotification.TYPE);
        long customerId = deliveryNotification.getCustomerId();
        Customer customer = customerState.getCustomerById(customerId);
        customer.setDeliveryCount(customer.getDeliveryCount() + 1);
    }
}

package dk.ku.dms.marketplace.functions;

import dk.ku.dms.marketplace.entities.Customer;
import dk.ku.dms.marketplace.messages.MsgToSeller.DeliveryNotification;
import dk.ku.dms.marketplace.messages.customer.NotifyCustomer;
import dk.ku.dms.marketplace.utils.Enums;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public final class CustomerFn implements StatefulFunction {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerFn.class);

    static final TypeName TYPE = TypeName.typeNameFromString("marketplace/customer");
    static final ValueSpec<Customer> CUSTOMER_STATE = ValueSpec.named("customer").withCustomType(Customer.TYPE);

    public static final StatefulFunctionSpec SPEC = StatefulFunctionSpec.builder(TYPE)
            .withValueSpec(CUSTOMER_STATE)
            .withSupplier(CustomerFn::new)
            .build();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Throwable {
        try {
            // client ---> customer (init customer type)
            if (message.is(Customer.TYPE)) {
                Customer customer = message.as(Customer.TYPE);
                context.storage().set(CUSTOMER_STATE, customer);
            }
            // ShippmentFn ---> customer (notify shipped type)
            // OrderFn / PaymentFn ---> customer (notify failed payment type)
            // PaymentFn ---> customer (notify success payment type)
            else if (message.is(NotifyCustomer.TYPE)) {
                onHandleNotifyCustomer(context, message);
            }
            else if (message.is(DeliveryNotification.TYPE)) {
                onHandleDeliveryNotification(context, message);
            }
            else {
                LOG.error("Message unknown: "+message);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return context.done();
    }

    private Customer getCustomerState(Context context) {
        return context.storage().get(CUSTOMER_STATE).orElse(new Customer());
    }

    private void onHandleNotifyCustomer(Context context, Message message) {
        NotifyCustomer notifyCustomer = message.as(NotifyCustomer.TYPE);
        Enums.CustomerNotificationType notificationType = notifyCustomer.getNotifyType();
        Customer customer = getCustomerState(context);
        switch (notificationType) {
            case notify_success_payment:
                customer.incrementSuccessPaymentCount();
                break;
            // use in 2 case: fail order and fail payment
            case notify_fail_checkout:
            case notify_failed_payment:
                customer.incrementFailedPaymentCount();
                break;
        }

        context.storage().set(CUSTOMER_STATE, customer);
    }

    private void onHandleDeliveryNotification(Context context, Message message) {
        Customer customer = getCustomerState(context);
        customer.incrementDeliveryCount();
        context.storage().set(CUSTOMER_STATE, customer);
    }
}

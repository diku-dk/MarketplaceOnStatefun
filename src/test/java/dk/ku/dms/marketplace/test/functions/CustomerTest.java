package dk.ku.dms.marketplace.test.functions;

import dk.ku.dms.marketplace.entities.Customer;
import dk.ku.dms.marketplace.functions.CustomerFn;
import dk.ku.dms.marketplace.messages.customer.CustomerMessages;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.testing.TestContext;
import org.junit.Test;

public class CustomerTest {

    @Test
    public void test() throws Throwable {

        // Arrange
        Address self = new Address(CustomerFn.TYPE, "1");

        TestContext context = TestContext.forTarget(self);

        Customer customer = new Customer(1, "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test");

        // Action
        CustomerFn function = new CustomerFn();
        Message message = MessageBuilder
                .forAddress(self)
                .withCustomType(CustomerMessages.SET_CUSTOMER_TYPE, customer)
                .build();
        function.apply(context, message);

        // Assert State
        assert(context.storage().get(CustomerFn.CUSTOMER_STATE).isPresent() &&
                context.storage().get(CustomerFn.CUSTOMER_STATE).get().getId() == 1);

    }

}

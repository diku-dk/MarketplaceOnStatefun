package dk.ku.dms.marketplace;

import dk.ku.dms.marketplace.functions.*;
import dk.ku.dms.marketplace.utils.PostgresHelper;
import io.undertow.Undertow;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import static io.undertow.UndertowOptions.ENABLE_HTTP2;

public final class MarketplaceServer {
    public static void main(String[] args) {

        final StatefulFunctions functions = new StatefulFunctions();
        functions.withStatefulFunction(CartFn.SPEC);
        functions.withStatefulFunction(OrderFn.SPEC);
        functions.withStatefulFunction(SellerFn.SPEC);
        functions.withStatefulFunction(StockFn.SPEC);
        functions.withStatefulFunction(ProductFn.SPEC);
        functions.withStatefulFunction(CustomerFn.SPEC);
        functions.withStatefulFunction(PaymentFn.SPEC);
        functions.withStatefulFunction(ShipmentFn.SPEC);
        functions.withStatefulFunction(ShipmentProxyFn.SPEC);

        PostgresHelper.init();

        final RequestReplyHandler handler = functions.requestReplyHandler();
        final Undertow httpServer =
                Undertow.builder()
                        .addHttpListener(1108, "0.0.0.0")
                        .setHandler(new UndertowHttpHandler(handler))
                        .setServerOption(ENABLE_HTTP2, true)
                        .build();
        httpServer.start();
    }
}

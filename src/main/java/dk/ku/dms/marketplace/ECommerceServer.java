package dk.ku.dms.marketplace;


import io.undertow.Undertow;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;



public final class ECommerceServer {
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

        final RequestReplyHandler handler = functions.requestReplyHandler();
        final Undertow httpServer =
                Undertow.builder()
                        .addHttpListener(1108, "0.0.0.0")
                        .setHandler(new UndertowHttpHandler(handler))
                        .build();
        httpServer.start();
    }
}

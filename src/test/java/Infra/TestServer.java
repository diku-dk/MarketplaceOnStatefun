//package Infra;
//
//import Functions.CustomerTest;
//import dk.ku.dms.marketplace.UndertowHttpHandler;
//import io.undertow.Undertow;
//import org.apache.flink.statefun.sdk.java.StatefulFunctions;
//import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
//
//public class TestServer {
//
//    public static final int PORT = 8081;
//
//    public static void main(String[] args) {
//
//        final StatefulFunctions functions = new StatefulFunctions();
//        functions.withStatefulFunction(CustomerTest.GREET_FN_SPEC);
//
//        final RequestReplyHandler requestReplyHandler = functions.requestReplyHandler();
//
//        final Undertow httpServer =
//                Undertow.builder()
//                        .addHttpListener(PORT, "localhost")
//                        .setHandler(new UndertowHttpHandler(requestReplyHandler))
//                        .build();
//        httpServer.start();
//    }
//
//}

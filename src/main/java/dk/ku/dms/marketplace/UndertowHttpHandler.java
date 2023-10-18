package dk.ku.dms.marketplace;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.apache.flink.statefun.sdk.java.slice.Slice;
import org.apache.flink.statefun.sdk.java.slice.Slices;

public final class UndertowHttpHandler implements HttpHandler {

    private final RequestReplyHandler handler;

    public UndertowHttpHandler(RequestReplyHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullBytes(this::onRequestBody);
    }

    private void onRequestBody(HttpServerExchange exchange, byte[] requestBytes) {
        try {
            CompletableFuture<Slice> future = this.handler.handle(Slices.wrap(requestBytes));
            exchange.dispatch();
            future.whenComplete(
                    (responseBytes, ex) -> {
                        if (ex != null) {
                            onException(exchange, ex);
                        } else {
                            onSuccess(exchange, responseBytes);
                        }
                    });
        } catch (Throwable t) {
            onException(exchange, t);
        }
    }

    private void onException(HttpServerExchange exchange, Throwable t) {
        t.printStackTrace(System.out);
        exchange.getResponseHeaders().put(Headers.STATUS, 500);
        exchange.endExchange();
    }

    private void onSuccess(HttpServerExchange exchange, Slice result) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
        exchange.getResponseSender().send(result.asReadOnlyByteBuffer());
    }
}

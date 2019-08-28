package com.networknt.graphql.router.handlers;

import com.networknt.graphql.common.GraphqlConstants;
import com.networknt.handler.LightHttpHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.protocol.Handshake;
import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
import io.undertow.websockets.extensions.ExtensionHandshake;
import io.undertow.websockets.extensions.PerMessageDeflateHandshake;

import java.util.HashSet;
import java.util.Set;

public class GraphqlSubscriptionWrapper implements LightHttpHandler {
    HttpHandler handler;

    public GraphqlSubscriptionWrapper() {
        ExtensionHandshake extensionHandshake = new PerMessageDeflateHandshake();

        WebSocketConnectionCallback webSocketConnectionCallback = new GraphqlSubscriptionHandler();
        handler = new WebSocketProtocolHandshakeHandler(buildHandshakeset(),
                webSocketConnectionCallback).addExtension(extensionHandshake);
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        handler.handleRequest(httpServerExchange);
    }

    /**
     * For meeting specification of the general websocket protocol, we are required to supply the supported subprotocols
     * when requested.
     * @return
     */
    private Set<Handshake> buildHandshakeset() {
        Set<Handshake> handshakeSet = new HashSet<>();
        Set<String> subprotocols = new HashSet<>();
        subprotocols.add(GraphqlConstants.GraphqlRouterConstants.GRAPHQL_WS_SUBPROTOCOL);
        handshakeSet.add(new Hybi13Handshake(subprotocols, true));
        handshakeSet.add(new Hybi07Handshake(subprotocols, true));
        handshakeSet.add(new Hybi08Handshake(subprotocols, true));
        return handshakeSet;
    }

}

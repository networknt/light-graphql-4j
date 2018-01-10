package com.networknt.graphql.router;

import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.server.HandlerProvider;
import io.undertow.server.HttpHandler;
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

import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants;
import static io.undertow.Handlers.path;

/**
 * Router of the graphql request to map different handlers.
 *
 * @author Steve Hu
 */
public class GraphqlRouter implements HandlerProvider {


    @Override
    public HttpHandler getHandler() {
        ExtensionHandshake extensionHandshake = new PerMessageDeflateHandshake();

        WebSocketConnectionCallback webSocketConnectionCallback = new GraphqlSubscriptionHandler();
        HttpHandler websocketHttpHandler = new WebSocketProtocolHandshakeHandler(buildHandshakeset(),
                webSocketConnectionCallback).addExtension(extensionHandshake);

        return path()
                .addPrefixPath(GraphqlUtil.config.getPath(), new GraphqlPathHandler())
                .addPrefixPath(GraphqlUtil.config.getSubscriptionsPath(), websocketHttpHandler);
    }

    /**
     * For meeting specification of the general websocket protocol, we are required to supply the supported subprotocols
     * when requested.
     * @return
     */
    private Set<Handshake> buildHandshakeset() {
        Set<Handshake> handshakeSet = new HashSet<>();
        Set<String> subprotocols = new HashSet<>();
        subprotocols.add(GraphqlRouterConstants.GRAPHQL_WS_SUBPROTOCOL);
        handshakeSet.add(new Hybi13Handshake(subprotocols, true));
        handshakeSet.add(new Hybi07Handshake(subprotocols, true));
        handshakeSet.add(new Hybi08Handshake(subprotocols, true));
        return handshakeSet;
    }
}

package com.networknt.graphql.router.handlers;

import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.xnio.ChannelListener;
import tools.jackson.core.JacksonException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GraphqlSubscriptionJackson3Test {
    @Test
    void malformedMessageStillReachesUndertowsCheckedErrorBoundary() throws Exception {
        WebSocketChannel channel = mock(WebSocketChannel.class);
        Object listener = receiveListener(channel);
        BufferedTextMessage message = mock(BufferedTextMessage.class);
        when(message.getData()).thenReturn("{");
        Method callback = callback(listener);

        InvocationTargetException failure = assertThrows(InvocationTargetException.class,
                () -> callback.invoke(listener, channel, message));
        IOException ioFailure = assertInstanceOf(IOException.class, failure.getCause());
        assertInstanceOf(JacksonException.class, ioFailure.getCause());
    }

    @Test
    void initMessageRetainsAcknowledgementWireShape() throws Exception {
        WebSocketChannel channel = mock(WebSocketChannel.class);
        Object listener = receiveListener(channel);
        BufferedTextMessage message = mock(BufferedTextMessage.class);
        when(message.getData()).thenReturn("{\"type\":\"connection_init\"}");
        Method callback = callback(listener);

        try (MockedStatic<WebSockets> sockets = mockStatic(WebSockets.class)) {
            callback.invoke(listener, channel, message);
            sockets.verify(() -> WebSockets.sendText("{\"type\":\"connection_ack\"}", channel, null));
        }
    }

    private Object receiveListener(WebSocketChannel channel) {
        ChannelListener.SimpleSetter<WebSocketChannel> setter = new ChannelListener.SimpleSetter<>();
        doReturn(setter).when(channel).getReceiveSetter();
        new GraphqlSubscriptionHandler().onConnect(null, channel);
        assertNotNull(setter.get());
        return setter.get();
    }

    private Method callback(Object listener) throws Exception {
        Method method = listener.getClass().getDeclaredMethod("onFullTextMessage",
                WebSocketChannel.class, BufferedTextMessage.class);
        method.setAccessible(true);
        return method;
    }
}

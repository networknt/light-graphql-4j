package com.networknt.graphql.router;

import com.networknt.graphql.common.GraphqlUtil;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This is the handler to handle graphql get request.
 *
 * @author Steve Hu
 */
public class GraphqlGetHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(GraphqlGetHandler.class);

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        @SuppressWarnings("unchecked")
        Map<String, Object> requestParameters = (Map<String, Object>)exchange.getAttachment(GraphqlUtil.GRAPHQL_PARAMS);
        if(logger.isDebugEnabled()) logger.debug("requestParameters: " + requestParameters);
        String graphiql = RenderGraphiQL.render(requestParameters, null);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(graphiql);
    }
}

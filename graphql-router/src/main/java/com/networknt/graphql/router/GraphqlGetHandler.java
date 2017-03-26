package com.networknt.graphql.router;

import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.utility.Util;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 24/03/17.
 */
public class GraphqlGetHandler implements HttpHandler {

    static final Logger logger = LoggerFactory.getLogger(GraphqlGetHandler.class);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Map<String, Object> requestParameters = (Map<String, Object>)exchange.getAttachment(GraphqlUtil.GRAPHQL_PARAMS);
        if(logger.isDebugEnabled()) logger.debug("requestParameters: " + requestParameters);
        String graphiql = RenderGraphiQL.render(requestParameters, null);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(graphiql);
    }
}

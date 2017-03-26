package com.networknt.graphql.router;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;


/**
 * Created by steve on 24/03/17.
 */
public class GraphqlPostHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // get the request parameters
        //Object requestParameters = exchange.getAttachment(ValidatorHandler.)
        exchange.getResponseSender().send("OK");
    }
}

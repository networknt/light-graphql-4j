package com.networknt.graphql.router;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;

public class GraphqlPathHandler implements HttpHandler {

    /**
     * Map requests that come to the graphql endpoint within graphql.yml to either GET or POST.
     */
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if (Methods.GET.equals(httpServerExchange.getRequestMethod())) {
            new GraphqlGetHandler().handleRequest(httpServerExchange);
        } else if (Methods.POST.equals(httpServerExchange.getRequestMethod())) {
            new GraphqlPostHandler().handleRequest(httpServerExchange);
        } else {
            throw new Exception(String.format("Unsupported request method %s", httpServerExchange.getRequestMethod().toString()));
        }
    }
}

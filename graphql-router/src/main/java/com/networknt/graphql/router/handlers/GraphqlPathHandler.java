package com.networknt.graphql.router.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;

/**
 * Map requests that come into the /graphql path to the appropriate handler by the used request method.
 *
 * @author Nicholas Azar
 */
public class GraphqlPathHandler implements HttpHandler {

    // create handler instances here so that one one instance is created.
    GraphqlGetHandler graphqlGetHandler = new GraphqlGetHandler();
    GraphqlPostHandler graphqlPostHandler = new GraphqlPostHandler();
    GraphqlOptionsHandler graphqlOptionsHandler = new GraphqlOptionsHandler();

    /**
     * Map requests that come to the graphql endpoint within graphql.yml to either GET or POST.
     */
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if (Methods.GET.equals(httpServerExchange.getRequestMethod())) {
            // Get will return the rendered graphiql content.
            graphqlGetHandler.handleRequest(httpServerExchange);
        } else if (Methods.POST.equals(httpServerExchange.getRequestMethod())) {
            // Post will return results from graphql queries.
            graphqlPostHandler.handleRequest(httpServerExchange);
        } else if (Methods.OPTIONS.equals(httpServerExchange.getRequestMethod())) {
            // Option will return introspection queries.
            graphqlOptionsHandler.handleRequest(httpServerExchange);
        } else {
            throw new Exception(String.format("Unsupported request method %s", httpServerExchange.getRequestMethod().toString()));
        }
    }
}

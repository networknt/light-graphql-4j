package com.networknt.graphql.router;

import graphql.ExecutionResult;
import io.undertow.server.HttpServerExchange;

public interface GraphqlCustomHandler {
    void handleResponse(HttpServerExchange exchange, ExecutionResult result);
}

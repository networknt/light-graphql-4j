package com.networknt.graphql.router;

import com.networknt.config.Config;
import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.status.Status;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * GraphQL post request handler
 *
 * @author Steve Hu
 */
public class GraphqlPostHandler implements HttpHandler {
    private static final String STATUS_GRAPHQL_MISSING_QUERY = "ERR11502";

    private static final Logger logger = LoggerFactory.getLogger(GraphqlPostHandler.class);
    private static GraphQLSchema schema = null;

    static {
        // load GraphQL Schema with service loader. It should be defined in service.yml
        SchemaProvider schemaProvider = SingletonServiceFactory.getBean(SchemaProvider.class);
        if(schemaProvider != null) {
            schema = schemaProvider.getSchema();
        }
        if (schema == null) {
            logger.error("Unable to load GraphQL schema - no SchemaProvider implementation in service.yml");
            throw new RuntimeException("Unable to load GraphQL schema - no SchemaProvider implementation in service.yml");
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // get the request parameters as a Map<String, Object>
        @SuppressWarnings("unchecked")
        Map<String, Object> requestParameters = (Map<String, Object>)exchange.getAttachment(GraphqlUtil.GRAPHQL_PARAMS);
        if(logger.isDebugEnabled()) logger.debug("requestParameters: " + requestParameters);
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        String query = (String)requestParameters.get("query");
        if(query == null) {
            Status status = new Status(STATUS_GRAPHQL_MISSING_QUERY);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>)requestParameters.get("variables");
        if(variables == null) {
            variables = new HashMap<>();
        }
        String operationName = (String)requestParameters.get("operationName");
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(query).operationName(operationName).context(exchange).root(exchange).variables(variables).build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        Map<String, Object> result = new HashMap<>();
        if (executionResult.getErrors().size() > 0) {
            result.put("errors", executionResult.getErrors());
            logger.error("Errors: {}", executionResult.getErrors());
        } else {
            result.put("data", executionResult.getData());
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(result));
    }
}

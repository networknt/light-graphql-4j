package com.networknt.graphql.router.handlers;

import com.networknt.config.Config;
import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.graphql.router.ExecutionStrategyProvider;
import com.networknt.graphql.common.InstrumentationLoader;
import com.networknt.graphql.common.InstrumentationProvider;
import com.networknt.graphql.router.SchemaProvider;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.status.Status;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.ExecutionStrategy;
import graphql.schema.GraphQLSchema;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_OP_NAME_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_QUERY_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_VARIABLES_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_RESPONSE_DATA_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_RESPONSE_ERROR_KEY;

/**
 * GraphQL post request handler
 *
 * @author Steve Hu
 */
public class GraphqlPostHandler implements HttpHandler {
    private static final String STATUS_GRAPHQL_MISSING_QUERY = "ERR11502";

    private static final Logger logger = LoggerFactory.getLogger(GraphqlPostHandler.class);
    static GraphQLSchema schema = null;
    static ExecutionStrategy queryExecutionStrategy = null;
    static ExecutionStrategy mutationExecutionStrategy = null;
    static ExecutionStrategy subscriptionExecutionStrategy = null;

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

        // Replace default execution strategies if so configured.
        ExecutionStrategyProvider executionStrategyProvider = SingletonServiceFactory.getBean(ExecutionStrategyProvider.class);
        if(executionStrategyProvider != null) {
            queryExecutionStrategy = executionStrategyProvider.getQueryExecutionStrategy();
        }
        if(executionStrategyProvider != null) {
            mutationExecutionStrategy = executionStrategyProvider.getMutationExecutionStrategy();
        }
        if(executionStrategyProvider != null) {
            subscriptionExecutionStrategy = executionStrategyProvider.getSubscriptionExecutionStrategy();
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // get the request parameters as a Map<String, Object>
        @SuppressWarnings("unchecked")
        Map<String, Object> requestParameters = (Map<String, Object>)exchange.getAttachment(GraphqlUtil.GRAPHQL_PARAMS);
        if(logger.isDebugEnabled()) logger.debug("requestParameters: " + requestParameters);

        GraphQL graphQL = this.getGraphql();
        String query = (String)requestParameters.get(GRAPHQL_REQUEST_QUERY_KEY);
        if(query == null) {
            Status status = new Status(STATUS_GRAPHQL_MISSING_QUERY);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(status.toString());
            logger.error(status.toString());
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> variables = (Map<String, Object>)requestParameters.get(GRAPHQL_REQUEST_VARIABLES_KEY);
        if(variables == null) {
            variables = new HashMap<>();
        }
        String operationName = (String)requestParameters.get(GRAPHQL_REQUEST_OP_NAME_KEY);
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(query).operationName(operationName).context(exchange).root(exchange).variables(variables).build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        Map<String, Object> result = new HashMap<>();
        if (executionResult.getErrors().size() > 0) {
            result.put(GRAPHQL_RESPONSE_ERROR_KEY, executionResult.getErrors());
            logger.error("Errors: {}", executionResult.getErrors());
        } else {
            result.put(GRAPHQL_RESPONSE_DATA_KEY, executionResult.getData());
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.setStatusCode(StatusCodes.OK);
        exchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(result));
    }

    private GraphQL getGraphql() {
        GraphQL.Builder graphql = GraphQL.newGraphQL(schema);
        if (InstrumentationLoader.graphqlInstrumentation != null) {
            graphql = graphql.instrumentation(InstrumentationLoader.graphqlInstrumentation);
        }
        if(queryExecutionStrategy != null) {
            graphql.queryExecutionStrategy(queryExecutionStrategy);
        }
        if(mutationExecutionStrategy != null) {
            graphql.mutationExecutionStrategy(mutationExecutionStrategy);
        }
        if(subscriptionExecutionStrategy != null) {
            graphql.subscriptionExecutionStrategy(subscriptionExecutionStrategy);
        }
        return graphql.build();
    }
}

package com.networknt.graphql.router.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.config.Config;
import com.networknt.graphql.common.GraphqlConstants;
import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.graphql.router.SchemaProvider;
import com.networknt.service.SingletonServiceFactory;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.introspection.IntrospectionQuery;
import graphql.schema.GraphQLSchema;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle introspection queries issued using the http OPTIONS request method.
 *
 * @author Nicholas Azar
 */
public class GraphqlOptionsHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(GraphqlOptionsHandler.class);
    static GraphQLSchema schema = null;

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

    /**
     * For introspection queries, we execute the built in query supplied in graphql-java without any parameters.
     *
     * @param httpServerExchange exchange
     * @throws JsonProcessingException json processing exception
     */
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

        Map<String, Object> result = new HashMap<>();
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(IntrospectionQuery.INTROSPECTION_QUERY).build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        result.put(GraphqlConstants.GraphqlRouterConstants.GRAPHQL_RESPONSE_DATA_KEY, executionResult.getData());
        httpServerExchange.setStatusCode(StatusCodes.OK);
        httpServerExchange.getResponseSender().send(Config.getInstance().getMapper().writeValueAsString(result));
    }
}

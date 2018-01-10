package com.networknt.graphql.router.models;

import java.util.Collections;
import java.util.Map;

import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_QUERY_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_VARIABLES_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_RESPONSE_PAYLOAD_KEY;

/**
 * Graphql clients can send GET or POST HTTP requests.  The spec does not make an explicit
 * distinction.  So you may need to handle both.  The following was tested using
 * a graphiql client tool found here : https://github.com/skevy/graphiql-app
 *
 * You should consider bundling graphiql in your application
 *
 * https://github.com/graphql/graphiql
 *
 * This outlines more information on how to handle parameters over http
 *
 * http://graphql.org/learn/serving-over-http/
 */
public class QueryParameters {

    private String query;
    private Map<String, Object> variables = Collections.emptyMap();

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public static QueryParameters from(Map inputData) {
        QueryParameters parameters = new QueryParameters();
        Map<String, Object> payload = (Map)inputData.get(GRAPHQL_RESPONSE_PAYLOAD_KEY);
        parameters.query = (String)payload.get(GRAPHQL_REQUEST_QUERY_KEY);
        parameters.variables = (Map)payload.get(GRAPHQL_REQUEST_VARIABLES_KEY);
        return parameters;
    }
}

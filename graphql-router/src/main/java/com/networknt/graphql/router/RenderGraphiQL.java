package com.networknt.graphql.router;

import com.networknt.config.Config;
import com.networknt.utility.Util;

import java.util.HashMap;
import java.util.Map;

import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_OP_NAME_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_QUERY_KEY;
import static com.networknt.graphql.common.GraphqlConstants.GraphqlRouterConstants.GRAPHQL_REQUEST_VARIABLES_KEY;

/**
 * GraphiQL implementation
 *
 * @author Steve Hu
 */
public class RenderGraphiQL {

    public static String render(Map<String, Object> parameters, String result) {
        Map<String, String> variables = new HashMap<>();
        Config config = Config.getInstance();
        String graphiqlTemplate = config.getStringFromFile("graphiql.html");
        String graphiqlSubscriptionsFetcher = config.getStringFromFile("graphiql-subscriptions-fetcher.js");
        variables.put("GRAPHIQL_SUBSCRIPTION_FETCHER", graphiqlSubscriptionsFetcher);

        variables.put("queryString", (String)parameters.get(GRAPHQL_REQUEST_QUERY_KEY));
        variables.put("resultString", result);
        variables.put("variablesString", (String)parameters.get(GRAPHQL_REQUEST_VARIABLES_KEY));
        variables.put("operationName", (String)parameters.get(GRAPHQL_REQUEST_OP_NAME_KEY));
        return Util.substituteVariables(graphiqlTemplate, variables);
    }
}

package com.networknt.graphql.router;

import com.networknt.config.Config;
import com.networknt.utility.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * GraphiQL implementation
 *
 * @author Steve Hu
 */
public class RenderGraphiQL {
    // Current latest version of GraphiQL
    private static final String GRAPHIQL_VERSION = "0.11.11";

    public static String render(Map<String, Object> parameters, String result) {
        Map<String, String> variables = new HashMap<>();
        Config config = Config.getInstance();
        String graphiqlTemplate = config.getStringFromFile("graphiql.html");
        String subTransportWs = config.getStringFromFile("subscriptions-transport-ws-0.5.2.js");
        variables.put("GRAPHIQL_VERSION", GRAPHIQL_VERSION);
        variables.put("SUBSCRIPTION_TRANSPORT_WS", subTransportWs);

        variables.put("queryString", (String)parameters.get("query"));
        variables.put("resultString", result);
        variables.put("variablesString", (String)parameters.get("variables"));
        variables.put("operationName", (String)parameters.get("operationName"));
        return Util.substituteVariables(graphiqlTemplate, variables);
    }
}

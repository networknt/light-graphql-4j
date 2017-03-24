package com.networknt.graphql.router;

import com.networknt.utility.Util;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 24/03/17.
 */
public class GraphiqlHandler implements HttpHandler {
    // Current latest version of GraphiQL
    public static final String GRAPHIQL_VERSION = "0.9.1";

    public static final String template = "<!--\n" +
            "The request to this GraphQL server provided the header \"Accept: text/html\"\n" +
            "and as a result has been presented GraphiQL - an in-browser IDE for\n" +
            "exploring GraphQL.\n" +
            "\n" +
            "If you wish to receive JSON, provide the header \"Accept: application/json\" or\n" +
            "add \"&raw\" to the end of the URL within a browser.\n" +
            "-->\n" +
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"utf-8\" />\n" +
            "  <title>GraphiQL</title>\n" +
            "  <meta name=\"robots\" content=\"noindex\" />\n" +
            "  <style>\n" +
            "    html, body {\n" +
            "      height: 100%;\n" +
            "      margin: 0;\n" +
            "      overflow: hidden;\n" +
            "      width: 100%;\n" +
            "    }\n" +
            "  </style>\n" +
            "  <link href=\"//cdn.jsdelivr.net/graphiql/${GRAPHIQL_VERSION}/graphiql.css\" rel=\"stylesheet\" />\n" +
            "  <script src=\"//cdn.jsdelivr.net/fetch/0.9.0/fetch.min.js\"></script>\n" +
            "  <script src=\"//cdn.jsdelivr.net/react/15.4.2/react.min.js\"></script>\n" +
            "  <script src=\"//cdn.jsdelivr.net/react/15.4.2/react-dom.min.js\"></script>\n" +
            "  <script src=\"//cdn.jsdelivr.net/graphiql/${GRAPHIQL_VERSION}/graphiql.min.js\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <script>\n" +
            "    // Collect the URL parameters\n" +
            "    var parameters = {};\n" +
            "    window.location.search.substr(1).split('&').forEach(function (entry) {\n" +
            "      var eq = entry.indexOf('=');\n" +
            "      if (eq >= 0) {\n" +
            "        parameters[decodeURIComponent(entry.slice(0, eq))] =\n" +
            "          decodeURIComponent(entry.slice(eq + 1));\n" +
            "      }\n" +
            "    });\n" +
            "\n" +
            "    // Produce a Location query string from a parameter object.\n" +
            "    function locationQuery(params) {\n" +
            "      return '?' + Object.keys(params).map(function (key) {\n" +
            "        return encodeURIComponent(key) + '=' +\n" +
            "          encodeURIComponent(params[key]);\n" +
            "      }).join('&');\n" +
            "    }\n" +
            "\n" +
            "    // Derive a fetch URL from the current URL, sans the GraphQL parameters.\n" +
            "    var graphqlParamNames = {\n" +
            "      query: true,\n" +
            "      variables: true,\n" +
            "      operationName: true\n" +
            "    };\n" +
            "\n" +
            "    var otherParams = {};\n" +
            "    for (var k in parameters) {\n" +
            "      if (parameters.hasOwnProperty(k) && graphqlParamNames[k] !== true) {\n" +
            "        otherParams[k] = parameters[k];\n" +
            "      }\n" +
            "    }\n" +
            "    var fetchURL = locationQuery(otherParams);\n" +
            "\n" +
            "    // Defines a GraphQL fetcher using the fetch API.\n" +
            "    function graphQLFetcher(graphQLParams) {\n" +
            "      return fetch(fetchURL, {\n" +
            "        method: 'post',\n" +
            "        headers: {\n" +
            "          'Accept': 'application/json',\n" +
            "          'Content-Type': 'application/json'\n" +
            "        },\n" +
            "        body: JSON.stringify(graphQLParams),\n" +
            "        credentials: 'include',\n" +
            "      }).then(function (response) {\n" +
            "        return response.text();\n" +
            "      }).then(function (responseBody) {\n" +
            "        try {\n" +
            "          return JSON.parse(responseBody);\n" +
            "        } catch (error) {\n" +
            "          return responseBody;\n" +
            "        }\n" +
            "      });\n" +
            "    }\n" +
            "\n" +
            "    // When the query and variables string is edited, update the URL bar so\n" +
            "    // that it can be easily shared.\n" +
            "    function onEditQuery(newQuery) {\n" +
            "      parameters.query = newQuery;\n" +
            "      updateURL();\n" +
            "    }\n" +
            "\n" +
            "    function onEditVariables(newVariables) {\n" +
            "      parameters.variables = newVariables;\n" +
            "      updateURL();\n" +
            "    }\n" +
            "\n" +
            "    function onEditOperationName(newOperationName) {\n" +
            "      parameters.operationName = newOperationName;\n" +
            "      updateURL();\n" +
            "    }\n" +
            "\n" +
            "    function updateURL() {\n" +
            "      history.replaceState(null, null, locationQuery(parameters));\n" +
            "    }\n" +
            "\n" +
            "    // Render <GraphiQL /> into the body.\n" +
            "    ReactDOM.render(\n" +
            "      React.createElement(GraphiQL, {\n" +
            "        fetcher: graphQLFetcher,\n" +
            "        onEditQuery: onEditQuery,\n" +
            "        onEditVariables: onEditVariables,\n" +
            "        onEditOperationName: onEditOperationName,\n" +
            "        query: ${queryString},\n" +
            "        response: ${resultString},\n" +
            "        variables: ${variablesString},\n" +
            "        operationName: ${operationName},\n" +
            "      }),\n" +
            "      document.body\n" +
            "    );\n" +
            "  </script>\n" +
            "</body>\n" +
            "</html>";
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Map<String, String> variables = new HashMap<>();
        variables.put("GRAPHIQL_VERSION", GRAPHIQL_VERSION);
        // TODO put other variables from exchange into the map

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html; charset=UTF-8");
        exchange.getResponseSender().send(Util.substituteVariables(template, variables));
    }
}

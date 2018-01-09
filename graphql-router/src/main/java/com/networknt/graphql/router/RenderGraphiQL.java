package com.networknt.graphql.router;

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

    private static final String template = "<!--\n" +
            " *  Copyright (c) Facebook, Inc.\n" +
            " *  All rights reserved.\n" +
            " *\n" +
            " *  This source code is licensed under the license found in the\n" +
            " *  LICENSE file in the root directory of this source tree.\n" +
            "-->\n" +
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <style>\n" +
            "      body {\n" +
            "        height: 100%;\n" +
            "        margin: 0;\n" +
            "        width: 100%;\n" +
            "        overflow: hidden;\n" +
            "      }\n" +
            "      #graphiql {\n" +
            "        height: 100vh;\n" +
            "      }\n" +
            "    </style>\n" +
            "\n" +
            "    <!--\n" +
            "      This GraphiQL example depends on Promise and fetch, which are available in\n" +
            "      modern browsers, but can be \"polyfilled\" for older browsers.\n" +
            "      GraphiQL itself depends on React DOM.\n" +
            "      If you do not want to rely on a CDN, you can host these files locally or\n" +
            "      include them directly in your favored resource bunder.\n" +
            "    -->\n" +
            "    <script src=\"//cdn.jsdelivr.net/es6-promise/4.0.5/es6-promise.auto.min.js\"></script>\n" +
            "    <script src=\"//cdn.jsdelivr.net/fetch/0.9.0/fetch.min.js\"></script>\n" +
            "    <script src=\"//cdn.jsdelivr.net/react/15.4.2/react.min.js\"></script>\n" +
            "    <script src=\"//cdn.jsdelivr.net/react/15.4.2/react-dom.min.js\"></script>\n" +
            "\n" +
            "    <!--\n" +
            "      These two files can be found in the npm module, however you may wish to\n" +
            "      copy them directly into your environment, or perhaps include them in your\n" +
            "      favored resource bundler.\n" +
            "     -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/graphiql/${GRAPHIQL_VERSION}/graphiql.min.css\" />\n" +
            "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/graphiql/${GRAPHIQL_VERSION}/graphiql.min.js\"></script>\n" +
            "\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"graphiql\">Loading...</div>\n" +
            "    <script>\n" +
            "\n" +
            "      /**\n" +
            "       * This GraphiQL example illustrates how to use some of GraphiQL's props\n" +
            "       * in order to enable reading and updating the URL parameters, making\n" +
            "       * link sharing of queries a little bit easier.\n" +
            "       *\n" +
            "       * This is only one example of this kind of feature, GraphiQL exposes\n" +
            "       * various React params to enable interesting integrations.\n" +
            "       */\n" +
            "\n" +
            "      // Parse the search string to get url parameters.\n" +
            "      var search = window.location.search;\n" +
            "      var parameters = {};\n" +
            "      search.substr(1).split('&').forEach(function (entry) {\n" +
            "        var eq = entry.indexOf('=');\n" +
            "        if (eq >= 0) {\n" +
            "          parameters[decodeURIComponent(entry.slice(0, eq))] =\n" +
            "            decodeURIComponent(entry.slice(eq + 1));\n" +
            "        }\n" +
            "      });\n" +
            "\n" +
            "      // if variables was provided, try to format it.\n" +
            "      if (parameters.variables) {\n" +
            "        try {\n" +
            "          parameters.variables =\n" +
            "            JSON.stringify(JSON.parse(parameters.variables), null, 2);\n" +
            "        } catch (e) {\n" +
            "          // Do nothing, we want to display the invalid JSON as a string, rather\n" +
            "          // than present an error.\n" +
            "        }\n" +
            "      }\n" +
            "\n" +
            "      // When the query and variables string is edited, update the URL bar so\n" +
            "      // that it can be easily shared\n" +
            "      function onEditQuery(newQuery) {\n" +
            "        parameters.query = newQuery;\n" +
            "        updateURL();\n" +
            "      }\n" +
            "\n" +
            "      function onEditVariables(newVariables) {\n" +
            "        parameters.variables = newVariables;\n" +
            "        updateURL();\n" +
            "      }\n" +
            "\n" +
            "      function onEditOperationName(newOperationName) {\n" +
            "        parameters.operationName = newOperationName;\n" +
            "        updateURL();\n" +
            "      }\n" +
            "\n" +
            "      function updateURL() {\n" +
            "        var newSearch = '?' + Object.keys(parameters).filter(function (key) {\n" +
            "          return Boolean(parameters[key]);\n" +
            "        }).map(function (key) {\n" +
            "          return encodeURIComponent(key) + '=' +\n" +
            "            encodeURIComponent(parameters[key]);\n" +
            "        }).join('&');\n" +
            "        history.replaceState(null, null, newSearch);\n" +
            "      }\n" +
            "\n" +
            "      // Defines a GraphQL fetcher using the fetch API. You're not required to\n" +
            "      // use fetch, and could instead implement graphQLFetcher however you like,\n" +
            "      // as long as it returns a Promise or Observable.\n" +
            "      function graphQLFetcher(graphQLParams) {\n" +
            "        // This example expects a GraphQL server at the path /graphql.\n" +
            "        // Change this to point wherever you host your GraphQL server.\n" +
            "        return fetch('/graphql', {\n" +
            "          method: 'post',\n" +
            "          headers: {\n" +
            "            'Accept': 'application/json',\n" +
            "            'Content-Type': 'application/json',\n" +
            "          },\n" +
            "          body: JSON.stringify(graphQLParams),\n" +
            "          credentials: 'include',\n" +
            "        }).then(function (response) {\n" +
            "          return response.text();\n" +
            "        }).then(function (responseBody) {\n" +
            "          try {\n" +
            "            return JSON.parse(responseBody);\n" +
            "          } catch (error) {\n" +
            "            return responseBody;\n" +
            "          }\n" +
            "        });\n" +
            "      }\n" +
            "\n" +
            "      // Render <GraphiQL /> into the body.\n" +
            "      // See the README in the top level of this module to learn more about\n" +
            "      // how you can customize GraphiQL by providing different values or\n" +
            "      // additional child elements.\n" +
            "      ReactDOM.render(\n" +
            "        React.createElement(GraphiQL, {\n" +
            "          fetcher: graphQLFetcher,\n" +
            "          query: parameters.query,\n" +
            "          variables: parameters.variables,\n" +
            "          operationName: parameters.operationName,\n" +
            "          onEditQuery: onEditQuery,\n" +
            "          onEditVariables: onEditVariables,\n" +
            "          onEditOperationName: onEditOperationName\n" +
            "        }),\n" +
            "        document.getElementById('graphiql')\n" +
            "      );\n" +
            "    </script>\n" +
            "  </body>\n" +
            "</html>\n";

    public static String render(Map<String, Object> parameters, String result) {
        Map<String, String> variables = new HashMap<>();
        variables.put("GRAPHIQL_VERSION", GRAPHIQL_VERSION);

        variables.put("queryString", (String)parameters.get("query"));
        variables.put("resultString", result);
        variables.put("variablesString", (String)parameters.get("variables"));
        variables.put("operationName", (String)parameters.get("operationName"));
        return Util.substituteVariables(template, variables);
    }
}

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
            "    <script>${SUBSCRIPTION_TRANSPORT_WS}</script>\n" +
            "\n" +
            "    <!--\n" +
            "      These two files can be found in the npm module, however you may wish to\n" +
            "      copy them directly into your environment, or perhaps include them in your\n" +
            "      favored resource bundler.\n" +
            "     -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/graphiql/${GRAPHIQL_VERSION}/graphiql.min.css\" />\n" +
            "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/graphiql/${GRAPHIQL_VERSION}/graphiql.js\"></script>\n" +
            "\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"graphiql\">Loading...</div>\n" +
            "    <script>\n" +
            "      /**\n" +
            "       * This GraphiQL example illustrates how to use some of GraphiQL's props\n" +
            "       * in order to enable reading and updating the URL parameters, making\n" +
            "       * link sharing of queries a little bit easier.\n" +
            "       *\n" +
            "       * This is only one example of this kind of feature, GraphiQL exposes\n" +
            "       * various React params to enable interesting integrations.\n" +
            "       */\n" +
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
            "      // When the query and variables string is edited, update the URL bar so\n" +
            "      // that it can be easily shared\n" +
            "      function onEditQuery(newQuery) {\n" +
            "        parameters.query = newQuery;\n" +
            "        updateURL();\n" +
            "      }\n" +
            "      function onEditVariables(newVariables) {\n" +
            "        parameters.variables = newVariables;\n" +
            "        updateURL();\n" +
            "      }\n" +
            "      function onEditOperationName(newOperationName) {\n" +
            "        parameters.operationName = newOperationName;\n" +
            "        updateURL();\n" +
            "      }\n" +
            "      function updateURL() {\n" +
            "        var newSearch = '?' + Object.keys(parameters).filter(function (key) {\n" +
            "          return Boolean(parameters[key]);\n" +
            "        }).map(function (key) {\n" +
            "          return encodeURIComponent(key) + '=' +\n" +
            "            encodeURIComponent(parameters[key]);\n" +
            "        }).join('&');\n" +
            "        history.replaceState(null, null, newSearch);\n" +
            "      }\n" +
            "      var subscriptionsClient;\n" +
            "      var activeSubscriptionId = -1;\n" +
            "      function initSubscriptions() {\n" +
            "        var subscriptionsEndpoint = 'ws://' + window.location.hostname + (window.location.port ? ':' + window.location.port: '') + '/subscriptions';\n" +
            "        if (window.SubscriptionsTransportWs && window.SubscriptionsTransportWs.SubscriptionClient) {\n" +
            "          subscriptionsClient = new window.SubscriptionsTransportWs.SubscriptionClient(subscriptionsEndpoint, {\n" +
            "            reconnect: true\n" +
            "          });\n" +
            "          subscriptionsClient.onConnect(function() {\n" +
            "            console.log('Connected to GraphQL Subscriptions server...');\n" +
            "          });\n" +
            "        }\n" +
            "      }\n" +
            "      function graphQlSubscriptionFetcher(graphQLParams) {\n" +
            "        return {\n" +
            "          subscribe: function(observer) {\n" +
            "            activeSubscriptionId = subscriptionsClient.subscribe({\n" +
            "              query: graphQLParams.query,\n" +
            "              variables: graphQLParams.variables\n" +
            "            }, function(error, result) {\n" +
            "              if (error) {\n" +
            "                observer.error(error);\n" +
            "              }\n" +
            "              else {\n" +
            "                observer.next(result);\n" +
            "              }\n" +
            "            });\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "      // Defines a GraphQL fetcher using the fetch API.\n" +
            "      function graphQLFetcher(graphQLParams) {\n" +
            "        if (subscriptionsClient && activeSubscriptionId !== -1) {\n" +
            "          subscriptionsClient.unsubscribe(activeSubscriptionId);\n" +
            "        }\n" +
            "        if (subscriptionsClient && graphQLParams.query.startsWith('subscription')) {\n" +
            "          return graphQlSubscriptionFetcher(graphQLParams);\n" +
            "        }\n" +
            "        else {\n" +
            "          return fetch('/graphql', {\n" +
            "            method: 'post',\n" +
            "            headers: {\n" +
            "              'Accept': 'application/json',\n" +
            "              'Content-Type': 'application/json',\n" +
            "            },\n" +
            "            body: JSON.stringify(graphQLParams),\n" +
            "            credentials: 'include',\n" +
            "          }).then(function (response) {\n" +
            "            return response.text();\n" +
            "          }).then(function (responseBody) {\n" +
            "            try {\n" +
            "              return JSON.parse(responseBody);\n" +
            "            } catch (error) {\n" +
            "              return responseBody;\n" +
            "            }\n" +
            "          });\n" +
            "        }\n" +
            "      }\n" +
            "      initSubscriptions();\n" +
            "      // Render <GraphiQL /> into the body.\n" +
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
            "    </script>" +
            "  </body>\n" +
            "</html>\n";

    // From: https://github.com/Urigo/graphiql/blob/master/test/vendor/subscriptions-transport-ws-0.5.2.js
    private static final String SUBSCRIPTION_TRANSPORT_WS_0_5_2 = "var SubscriptionsTransportWs =\n" +
            "  /******/ (function(modules) { // webpackBootstrap\n" +
            "  /******/ \t// The module cache\n" +
            "  /******/ \tvar installedModules = {};\n" +
            "\n" +
            "  /******/ \t// The require function\n" +
            "  /******/ \tfunction __webpack_require__(moduleId) {\n" +
            "\n" +
            "    /******/ \t\t// Check if module is in cache\n" +
            "    /******/ \t\tif(installedModules[moduleId])\n" +
            "    /******/ \t\t\treturn installedModules[moduleId].exports;\n" +
            "\n" +
            "    /******/ \t\t// Create a new module (and put it into the cache)\n" +
            "    /******/ \t\tvar module = installedModules[moduleId] = {\n" +
            "      /******/ \t\t\ti: moduleId,\n" +
            "      /******/ \t\t\tl: false,\n" +
            "      /******/ \t\t\texports: {}\n" +
            "      /******/ \t\t};\n" +
            "\n" +
            "    /******/ \t\t// Execute the module function\n" +
            "    /******/ \t\tmodules[moduleId].call(module.exports, module, module.exports, __webpack_require__);\n" +
            "\n" +
            "    /******/ \t\t// Flag the module as loaded\n" +
            "    /******/ \t\tmodule.l = true;\n" +
            "\n" +
            "    /******/ \t\t// Return the exports of the module\n" +
            "    /******/ \t\treturn module.exports;\n" +
            "    /******/ \t}\n" +
            "\n" +
            "\n" +
            "  /******/ \t// expose the modules object (__webpack_modules__)\n" +
            "  /******/ \t__webpack_require__.m = modules;\n" +
            "\n" +
            "  /******/ \t// expose the module cache\n" +
            "  /******/ \t__webpack_require__.c = installedModules;\n" +
            "\n" +
            "  /******/ \t// identity function for calling harmony imports with the correct context\n" +
            "  /******/ \t__webpack_require__.i = function(value) { return value; };\n" +
            "\n" +
            "  /******/ \t// define getter function for harmony exports\n" +
            "  /******/ \t__webpack_require__.d = function(exports, name, getter) {\n" +
            "    /******/ \t\tif(!__webpack_require__.o(exports, name)) {\n" +
            "      /******/ \t\t\tObject.defineProperty(exports, name, {\n" +
            "        /******/ \t\t\t\tconfigurable: false,\n" +
            "        /******/ \t\t\t\tenumerable: true,\n" +
            "        /******/ \t\t\t\tget: getter\n" +
            "        /******/ \t\t\t});\n" +
            "      /******/ \t\t}\n" +
            "    /******/ \t};\n" +
            "\n" +
            "  /******/ \t// getDefaultExport function for compatibility with non-harmony modules\n" +
            "  /******/ \t__webpack_require__.n = function(module) {\n" +
            "    /******/ \t\tvar getter = module && module.__esModule ?\n" +
            "      /******/ \t\t\tfunction getDefault() { return module['default']; } :\n" +
            "      /******/ \t\t\tfunction getModuleExports() { return module; };\n" +
            "    /******/ \t\t__webpack_require__.d(getter, 'a', getter);\n" +
            "    /******/ \t\treturn getter;\n" +
            "    /******/ \t};\n" +
            "\n" +
            "  /******/ \t// Object.prototype.hasOwnProperty.call\n" +
            "  /******/ \t__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };\n" +
            "\n" +
            "  /******/ \t// __webpack_public_path__\n" +
            "  /******/ \t__webpack_require__.p = \"\";\n" +
            "\n" +
            "  /******/ \t// Load entry module and return exports\n" +
            "  /******/ \treturn __webpack_require__(__webpack_require__.s = 9);\n" +
            "  /******/ })\n" +
            "/************************************************************************/\n" +
            "/******/ ([\n" +
            "  /* 0 */\n" +
            "  /***/ (function(module, exports, __webpack_require__) {\n" +
            "\n" +
            "    \"use strict\";\n" +
            "\n" +
            "    var printer_1 = __webpack_require__(8);\n" +
            "    function addGraphQLSubscriptions(networkInterface, wsClient) {\n" +
            "      return Object.assign(networkInterface, {\n" +
            "        subscribe: function (request, handler) {\n" +
            "          return wsClient.subscribe({\n" +
            "            query: printer_1.print(request.query),\n" +
            "            variables: request.variables,\n" +
            "          }, handler);\n" +
            "        },\n" +
            "        unsubscribe: function (id) {\n" +
            "          wsClient.unsubscribe(id);\n" +
            "        },\n" +
            "      });\n" +
            "    }\n" +
            "    exports.addGraphQLSubscriptions = addGraphQLSubscriptions;\n" +
            "//# sourceMappingURL=helpers.js.map\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 1 */\n" +
            "  /***/ (function(module, exports, __webpack_require__) {\n" +
            "\n" +
            "    \"use strict\";\n" +
            "\n" +
            "    var SUBSCRIPTION_FAIL = 'subscription_fail';\n" +
            "    exports.SUBSCRIPTION_FAIL = SUBSCRIPTION_FAIL;\n" +
            "    var SUBSCRIPTION_END = 'subscription_end';\n" +
            "    exports.SUBSCRIPTION_END = SUBSCRIPTION_END;\n" +
            "    var SUBSCRIPTION_DATA = 'subscription_data';\n" +
            "    exports.SUBSCRIPTION_DATA = SUBSCRIPTION_DATA;\n" +
            "    var SUBSCRIPTION_START = 'subscription_start';\n" +
            "    exports.SUBSCRIPTION_START = SUBSCRIPTION_START;\n" +
            "    var SUBSCRIPTION_SUCCESS = 'subscription_success';\n" +
            "    exports.SUBSCRIPTION_SUCCESS = SUBSCRIPTION_SUCCESS;\n" +
            "    var KEEPALIVE = 'keepalive';\n" +
            "    exports.KEEPALIVE = KEEPALIVE;\n" +
            "    var INIT = 'init';\n" +
            "    exports.INIT = INIT;\n" +
            "    var INIT_SUCCESS = 'init_success';\n" +
            "    exports.INIT_SUCCESS = INIT_SUCCESS;\n" +
            "    var INIT_FAIL = 'init_fail';\n" +
            "    exports.INIT_FAIL = INIT_FAIL;\n" +
            "//# sourceMappingURL=messageTypes.js.map\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 2 */\n" +
            "  /***/ (function(module, exports, __webpack_require__) {\n" +
            "\n" +
            "    \"use strict\";\n" +
            "\n" +
            "    var GRAPHQL_SUBSCRIPTIONS = 'graphql-subscriptions';\n" +
            "    exports.GRAPHQL_SUBSCRIPTIONS = GRAPHQL_SUBSCRIPTIONS;\n" +
            "//# sourceMappingURL=protocols.js.map\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 3 */\n" +
            "  /***/ (function(module, exports) {\n" +
            "\n" +
            "\n" +
            "    /**\n" +
            "     * Expose `Backoff`.\n" +
            "     */\n" +
            "\n" +
            "    module.exports = Backoff;\n" +
            "\n" +
            "    /**\n" +
            "     * Initialize backoff timer with `opts`.\n" +
            "     *\n" +
            "     * - `min` initial timeout in milliseconds [100]\n" +
            "     * - `max` max timeout [10000]\n" +
            "     * - `jitter` [0]\n" +
            "     * - `factor` [2]\n" +
            "     *\n" +
            "     * @param {Object} opts\n" +
            "     * @api public\n" +
            "     */\n" +
            "\n" +
            "    function Backoff(opts) {\n" +
            "      opts = opts || {};\n" +
            "      this.ms = opts.min || 100;\n" +
            "      this.max = opts.max || 10000;\n" +
            "      this.factor = opts.factor || 2;\n" +
            "      this.jitter = opts.jitter > 0 && opts.jitter <= 1 ? opts.jitter : 0;\n" +
            "      this.attempts = 0;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Return the backoff duration.\n" +
            "     *\n" +
            "     * @return {Number}\n" +
            "     * @api public\n" +
            "     */\n" +
            "\n" +
            "    Backoff.prototype.duration = function(){\n" +
            "      var ms = this.ms * Math.pow(this.factor, this.attempts++);\n" +
            "      if (this.jitter) {\n" +
            "        var rand =  Math.random();\n" +
            "        var deviation = Math.floor(rand * this.jitter * ms);\n" +
            "        ms = (Math.floor(rand * 10) & 1) == 0  ? ms - deviation : ms + deviation;\n" +
            "      }\n" +
            "      return Math.min(ms, this.max) | 0;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Reset the number of attempts.\n" +
            "     *\n" +
            "     * @api public\n" +
            "     */\n" +
            "\n" +
            "    Backoff.prototype.reset = function(){\n" +
            "      this.attempts = 0;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Set the minimum duration\n" +
            "     *\n" +
            "     * @api public\n" +
            "     */\n" +
            "\n" +
            "    Backoff.prototype.setMin = function(min){\n" +
            "      this.ms = min;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Set the maximum duration\n" +
            "     *\n" +
            "     * @api public\n" +
            "     */\n" +
            "\n" +
            "    Backoff.prototype.setMax = function(max){\n" +
            "      this.max = max;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Set the jitter\n" +
            "     *\n" +
            "     * @api public\n" +
            "     */\n" +
            "\n" +
            "    Backoff.prototype.setJitter = function(jitter){\n" +
            "      this.jitter = jitter;\n" +
            "    };\n" +
            "\n" +
            "\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 4 */\n" +
            "  /***/ (function(module, exports, __webpack_require__) {\n" +
            "\n" +
            "    \"use strict\";\n" +
            "\n" +
            "\n" +
            "    var has = Object.prototype.hasOwnProperty\n" +
            "      , prefix = '~';\n" +
            "\n" +
            "    /**\n" +
            "     * Constructor to create a storage for our `EE` objects.\n" +
            "     * An `Events` instance is a plain object whose properties are event names.\n" +
            "     *\n" +
            "     * @constructor\n" +
            "     * @api private\n" +
            "     */\n" +
            "    function Events() {}\n" +
            "\n" +
            "//\n" +
            "// We try to not inherit from `Object.prototype`. In some engines creating an\n" +
            "// instance in this way is faster than calling `Object.create(null)` directly.\n" +
            "// If `Object.create(null)` is not supported we prefix the event names with a\n" +
            "// character to make sure that the built-in object properties are not\n" +
            "// overridden or used as an attack vector.\n" +
            "//\n" +
            "    if (Object.create) {\n" +
            "      Events.prototype = Object.create(null);\n" +
            "\n" +
            "      //\n" +
            "      // This hack is needed because the `__proto__` property is still inherited in\n" +
            "      // some old browsers like Android 4, iPhone 5.1, Opera 11 and Safari 5.\n" +
            "      //\n" +
            "      if (!new Events().__proto__) prefix = false;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Representation of a single event listener.\n" +
            "     *\n" +
            "     * @param {Function} fn The listener function.\n" +
            "     * @param {Mixed} context The context to invoke the listener with.\n" +
            "     * @param {Boolean} [once=false] Specify if the listener is a one-time listener.\n" +
            "     * @constructor\n" +
            "     * @api private\n" +
            "     */\n" +
            "    function EE(fn, context, once) {\n" +
            "      this.fn = fn;\n" +
            "      this.context = context;\n" +
            "      this.once = once || false;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Minimal `EventEmitter` interface that is molded against the Node.js\n" +
            "     * `EventEmitter` interface.\n" +
            "     *\n" +
            "     * @constructor\n" +
            "     * @api public\n" +
            "     */\n" +
            "    function EventEmitter() {\n" +
            "      this._events = new Events();\n" +
            "      this._eventsCount = 0;\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Return an array listing the events for which the emitter has registered\n" +
            "     * listeners.\n" +
            "     *\n" +
            "     * @returns {Array}\n" +
            "     * @api public\n" +
            "     */\n" +
            "    EventEmitter.prototype.eventNames = function eventNames() {\n" +
            "      var names = []\n" +
            "        , events\n" +
            "        , name;\n" +
            "\n" +
            "      if (this._eventsCount === 0) return names;\n" +
            "\n" +
            "      for (name in (events = this._events)) {\n" +
            "        if (has.call(events, name)) names.push(prefix ? name.slice(1) : name);\n" +
            "      }\n" +
            "\n" +
            "      if (Object.getOwnPropertySymbols) {\n" +
            "        return names.concat(Object.getOwnPropertySymbols(events));\n" +
            "      }\n" +
            "\n" +
            "      return names;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Return the listeners registered for a given event.\n" +
            "     *\n" +
            "     * @param {String|Symbol} event The event name.\n" +
            "     * @param {Boolean} exists Only check if there are listeners.\n" +
            "     * @returns {Array|Boolean}\n" +
            "     * @api public\n" +
            "     */\n" +
            "    EventEmitter.prototype.listeners = function listeners(event, exists) {\n" +
            "      var evt = prefix ? prefix + event : event\n" +
            "        , available = this._events[evt];\n" +
            "\n" +
            "      if (exists) return !!available;\n" +
            "      if (!available) return [];\n" +
            "      if (available.fn) return [available.fn];\n" +
            "\n" +
            "      for (var i = 0, l = available.length, ee = new Array(l); i < l; i++) {\n" +
            "        ee[i] = available[i].fn;\n" +
            "      }\n" +
            "\n" +
            "      return ee;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Calls each of the listeners registered for a given event.\n" +
            "     *\n" +
            "     * @param {String|Symbol} event The event name.\n" +
            "     * @returns {Boolean} `true` if the event had listeners, else `false`.\n" +
            "     * @api public\n" +
            "     */\n" +
            "    EventEmitter.prototype.emit = function emit(event, a1, a2, a3, a4, a5) {\n" +
            "      var evt = prefix ? prefix + event : event;\n" +
            "\n" +
            "      if (!this._events[evt]) return false;\n" +
            "\n" +
            "      var listeners = this._events[evt]\n" +
            "        , len = arguments.length\n" +
            "        , args\n" +
            "        , i;\n" +
            "\n" +
            "      if (listeners.fn) {\n" +
            "        if (listeners.once) this.removeListener(event, listeners.fn, undefined, true);\n" +
            "\n" +
            "        switch (len) {\n" +
            "          case 1: return listeners.fn.call(listeners.context), true;\n" +
            "          case 2: return listeners.fn.call(listeners.context, a1), true;\n" +
            "          case 3: return listeners.fn.call(listeners.context, a1, a2), true;\n" +
            "          case 4: return listeners.fn.call(listeners.context, a1, a2, a3), true;\n" +
            "          case 5: return listeners.fn.call(listeners.context, a1, a2, a3, a4), true;\n" +
            "          case 6: return listeners.fn.call(listeners.context, a1, a2, a3, a4, a5), true;\n" +
            "        }\n" +
            "\n" +
            "        for (i = 1, args = new Array(len -1); i < len; i++) {\n" +
            "          args[i - 1] = arguments[i];\n" +
            "        }\n" +
            "\n" +
            "        listeners.fn.apply(listeners.context, args);\n" +
            "      } else {\n" +
            "        var length = listeners.length\n" +
            "          , j;\n" +
            "\n" +
            "        for (i = 0; i < length; i++) {\n" +
            "          if (listeners[i].once) this.removeListener(event, listeners[i].fn, undefined, true);\n" +
            "\n" +
            "          switch (len) {\n" +
            "            case 1: listeners[i].fn.call(listeners[i].context); break;\n" +
            "            case 2: listeners[i].fn.call(listeners[i].context, a1); break;\n" +
            "            case 3: listeners[i].fn.call(listeners[i].context, a1, a2); break;\n" +
            "            case 4: listeners[i].fn.call(listeners[i].context, a1, a2, a3); break;\n" +
            "            default:\n" +
            "              if (!args) for (j = 1, args = new Array(len -1); j < len; j++) {\n" +
            "                args[j - 1] = arguments[j];\n" +
            "              }\n" +
            "\n" +
            "              listeners[i].fn.apply(listeners[i].context, args);\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "\n" +
            "      return true;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Add a listener for a given event.\n" +
            "     *\n" +
            "     * @param {String|Symbol} event The event name.\n" +
            "     * @param {Function} fn The listener function.\n" +
            "     * @param {Mixed} [context=this] The context to invoke the listener with.\n" +
            "     * @returns {EventEmitter} `this`.\n" +
            "     * @api public\n" +
            "     */\n" +
            "    EventEmitter.prototype.on = function on(event, fn, context) {\n" +
            "      var listener = new EE(fn, context || this)\n" +
            "        , evt = prefix ? prefix + event : event;\n" +
            "\n" +
            "      if (!this._events[evt]) this._events[evt] = listener, this._eventsCount++;\n" +
            "      else if (!this._events[evt].fn) this._events[evt].push(listener);\n" +
            "      else this._events[evt] = [this._events[evt], listener];\n" +
            "\n" +
            "      return this;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Add a one-time listener for a given event.\n" +
            "     *\n" +
            "     * @param {String|Symbol} event The event name.\n" +
            "     * @param {Function} fn The listener function.\n" +
            "     * @param {Mixed} [context=this] The context to invoke the listener with.\n" +
            "     * @returns {EventEmitter} `this`.\n" +
            "     * @api public\n" +
            "     */\n" +
            "    EventEmitter.prototype.once = function once(event, fn, context) {\n" +
            "      var listener = new EE(fn, context || this, true)\n" +
            "        , evt = prefix ? prefix + event : event;\n" +
            "\n" +
            "      if (!this._events[evt]) this._events[evt] = listener, this._eventsCount++;\n" +
            "      else if (!this._events[evt].fn) this._events[evt].push(listener);\n" +
            "      else this._events[evt] = [this._events[evt], listener];\n" +
            "\n" +
            "      return this;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Remove the listeners of a given event.\n" +
            "     *\n" +
            "     * @param {String|Symbol} event The event name.\n" +
            "     * @param {Function} fn Only remove the listeners that match this function.\n" +
            "     * @param {Mixed} context Only remove the listeners that have this context.\n" +
            "     * @param {Boolean} once Only remove one-time listeners.\n" +
            "     * @returns {EventEmitter} `this`.\n" +
            "     * @api public\n" +
            "     */\n" +
            "    EventEmitter.prototype.removeListener = function removeListener(event, fn, context, once) {\n" +
            "      var evt = prefix ? prefix + event : event;\n" +
            "\n" +
            "      if (!this._events[evt]) return this;\n" +
            "      if (!fn) {\n" +
            "        if (--this._eventsCount === 0) this._events = new Events();\n" +
            "        else delete this._events[evt];\n" +
            "        return this;\n" +
            "      }\n" +
            "\n" +
            "      var listeners = this._events[evt];\n" +
            "\n" +
            "      if (listeners.fn) {\n" +
            "        if (\n" +
            "          listeners.fn === fn\n" +
            "          && (!once || listeners.once)\n" +
            "          && (!context || listeners.context === context)\n" +
            "        ) {\n" +
            "          if (--this._eventsCount === 0) this._events = new Events();\n" +
            "          else delete this._events[evt];\n" +
            "        }\n" +
            "      } else {\n" +
            "        for (var i = 0, events = [], length = listeners.length; i < length; i++) {\n" +
            "          if (\n" +
            "            listeners[i].fn !== fn\n" +
            "            || (once && !listeners[i].once)\n" +
            "            || (context && listeners[i].context !== context)\n" +
            "          ) {\n" +
            "            events.push(listeners[i]);\n" +
            "          }\n" +
            "        }\n" +
            "\n" +
            "        //\n" +
            "        // Reset the array, or remove it completely if we have no more listeners.\n" +
            "        //\n" +
            "        if (events.length) this._events[evt] = events.length === 1 ? events[0] : events;\n" +
            "        else if (--this._eventsCount === 0) this._events = new Events();\n" +
            "        else delete this._events[evt];\n" +
            "      }\n" +
            "\n" +
            "      return this;\n" +
            "    };\n" +
            "\n" +
            "    /**\n" +
            "     * Remove all listeners, or those of the specified event.\n" +
            "     *\n" +
            "     * @param {String|Symbol} [event] The event name.\n" +
            "     * @returns {EventEmitter} `this`.\n" +
            "     * @api public\n" +
            "     */\n" +
            "    EventEmitter.prototype.removeAllListeners = function removeAllListeners(event) {\n" +
            "      var evt;\n" +
            "\n" +
            "      if (event) {\n" +
            "        evt = prefix ? prefix + event : event;\n" +
            "        if (this._events[evt]) {\n" +
            "          if (--this._eventsCount === 0) this._events = new Events();\n" +
            "          else delete this._events[evt];\n" +
            "        }\n" +
            "      } else {\n" +
            "        this._events = new Events();\n" +
            "        this._eventsCount = 0;\n" +
            "      }\n" +
            "\n" +
            "      return this;\n" +
            "    };\n" +
            "\n" +
            "//\n" +
            "// Alias methods names because people roll like that.\n" +
            "//\n" +
            "    EventEmitter.prototype.off = EventEmitter.prototype.removeListener;\n" +
            "    EventEmitter.prototype.addListener = EventEmitter.prototype.on;\n" +
            "\n" +
            "//\n" +
            "// This function doesn't apply anymore.\n" +
            "//\n" +
            "    EventEmitter.prototype.setMaxListeners = function setMaxListeners() {\n" +
            "      return this;\n" +
            "    };\n" +
            "\n" +
            "//\n" +
            "// Expose the prefix.\n" +
            "//\n" +
            "    EventEmitter.prefixed = prefix;\n" +
            "\n" +
            "//\n" +
            "// Allow `EventEmitter` to be imported as module namespace.\n" +
            "//\n" +
            "    EventEmitter.EventEmitter = EventEmitter;\n" +
            "\n" +
            "//\n" +
            "// Expose the module.\n" +
            "//\n" +
            "    if (true) {\n" +
            "      module.exports = EventEmitter;\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 5 */\n" +
            "  /***/ (function(module, exports) {\n" +
            "\n" +
            "    /**\n" +
            "     * lodash 3.0.2 (Custom Build) <https://lodash.com/>\n" +
            "     * Build: `lodash modern modularize exports=\"npm\" -o ./`\n" +
            "     * Copyright 2012-2015 The Dojo Foundation <http://dojofoundation.org/>\n" +
            "     * Based on Underscore.js 1.8.3 <http://underscorejs.org/LICENSE>\n" +
            "     * Copyright 2009-2015 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors\n" +
            "     * Available under MIT license <https://lodash.com/license>\n" +
            "     */\n" +
            "\n" +
            "    /**\n" +
            "     * Checks if `value` is the [language type](https://es5.github.io/#x8) of `Object`.\n" +
            "     * (e.g. arrays, functions, objects, regexes, `new Number(0)`, and `new String('')`)\n" +
            "     *\n" +
            "     * @static\n" +
            "     * @memberOf _\n" +
            "     * @category Lang\n" +
            "     * @param {*} value The value to check.\n" +
            "     * @returns {boolean} Returns `true` if `value` is an object, else `false`.\n" +
            "     * @example\n" +
            "     *\n" +
            "     * _.isObject({});\n" +
            "     * // => true\n" +
            "     *\n" +
            "     * _.isObject([1, 2, 3]);\n" +
            "     * // => true\n" +
            "     *\n" +
            "     * _.isObject(1);\n" +
            "     * // => false\n" +
            "     */\n" +
            "    function isObject(value) {\n" +
            "      // Avoid a V8 JIT bug in Chrome 19-20.\n" +
            "      // See https://code.google.com/p/v8/issues/detail?id=2291 for more details.\n" +
            "      var type = typeof value;\n" +
            "      return !!value && (type == 'object' || type == 'function');\n" +
            "    }\n" +
            "\n" +
            "    module.exports = isObject;\n" +
            "\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 6 */\n" +
            "  /***/ (function(module, exports) {\n" +
            "\n" +
            "    /**\n" +
            "     * lodash 4.0.1 (Custom Build) <https://lodash.com/>\n" +
            "     * Build: `lodash modularize exports=\"npm\" -o ./`\n" +
            "     * Copyright 2012-2016 The Dojo Foundation <http://dojofoundation.org/>\n" +
            "     * Based on Underscore.js 1.8.3 <http://underscorejs.org/LICENSE>\n" +
            "     * Copyright 2009-2016 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors\n" +
            "     * Available under MIT license <https://lodash.com/license>\n" +
            "     */\n" +
            "\n" +
            "    /** `Object#toString` result references. */\n" +
            "    var stringTag = '[object String]';\n" +
            "\n" +
            "    /** Used for built-in method references. */\n" +
            "    var objectProto = Object.prototype;\n" +
            "\n" +
            "    /**\n" +
            "     * Used to resolve the [`toStringTag`](http://ecma-international.org/ecma-262/6.0/#sec-object.prototype.tostring)\n" +
            "     * of values.\n" +
            "     */\n" +
            "    var objectToString = objectProto.toString;\n" +
            "\n" +
            "    /**\n" +
            "     * Checks if `value` is classified as an `Array` object.\n" +
            "     *\n" +
            "     * @static\n" +
            "     * @memberOf _\n" +
            "     * @type Function\n" +
            "     * @category Lang\n" +
            "     * @param {*} value The value to check.\n" +
            "     * @returns {boolean} Returns `true` if `value` is correctly classified, else `false`.\n" +
            "     * @example\n" +
            "     *\n" +
            "     * _.isArray([1, 2, 3]);\n" +
            "     * // => true\n" +
            "     *\n" +
            "     * _.isArray(document.body.children);\n" +
            "     * // => false\n" +
            "     *\n" +
            "     * _.isArray('abc');\n" +
            "     * // => false\n" +
            "     *\n" +
            "     * _.isArray(_.noop);\n" +
            "     * // => false\n" +
            "     */\n" +
            "    var isArray = Array.isArray;\n" +
            "\n" +
            "    /**\n" +
            "     * Checks if `value` is object-like. A value is object-like if it's not `null`\n" +
            "     * and has a `typeof` result of \"object\".\n" +
            "     *\n" +
            "     * @static\n" +
            "     * @memberOf _\n" +
            "     * @category Lang\n" +
            "     * @param {*} value The value to check.\n" +
            "     * @returns {boolean} Returns `true` if `value` is object-like, else `false`.\n" +
            "     * @example\n" +
            "     *\n" +
            "     * _.isObjectLike({});\n" +
            "     * // => true\n" +
            "     *\n" +
            "     * _.isObjectLike([1, 2, 3]);\n" +
            "     * // => true\n" +
            "     *\n" +
            "     * _.isObjectLike(_.noop);\n" +
            "     * // => false\n" +
            "     *\n" +
            "     * _.isObjectLike(null);\n" +
            "     * // => false\n" +
            "     */\n" +
            "    function isObjectLike(value) {\n" +
            "      return !!value && typeof value == 'object';\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Checks if `value` is classified as a `String` primitive or object.\n" +
            "     *\n" +
            "     * @static\n" +
            "     * @memberOf _\n" +
            "     * @category Lang\n" +
            "     * @param {*} value The value to check.\n" +
            "     * @returns {boolean} Returns `true` if `value` is correctly classified, else `false`.\n" +
            "     * @example\n" +
            "     *\n" +
            "     * _.isString('abc');\n" +
            "     * // => true\n" +
            "     *\n" +
            "     * _.isString(1);\n" +
            "     * // => false\n" +
            "     */\n" +
            "    function isString(value) {\n" +
            "      return typeof value == 'string' ||\n" +
            "        (!isArray(value) && isObjectLike(value) && objectToString.call(value) == stringTag);\n" +
            "    }\n" +
            "\n" +
            "    module.exports = isString;\n" +
            "\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 7 */\n" +
            "  /***/ (function(module, exports) {\n" +
            "\n" +
            "    var g;\n" +
            "\n" +
            "// This works in non-strict mode\n" +
            "    g = (function() {\n" +
            "      return this;\n" +
            "    })();\n" +
            "\n" +
            "    try {\n" +
            "      // This works if eval is allowed (see CSP)\n" +
            "      g = g || Function(\"return this\")() || (1,eval)(\"this\");\n" +
            "    } catch(e) {\n" +
            "      // This works if the window reference is available\n" +
            "      if(typeof window === \"object\")\n" +
            "        g = window;\n" +
            "    }\n" +
            "\n" +
            "// g can still be undefined, but nothing to do about it...\n" +
            "// We return undefined, instead of nothing here, so it's\n" +
            "// easier to handle this case. if(!global) { ...}\n" +
            "\n" +
            "    module.exports = g;\n" +
            "\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 8 */\n" +
            "  /***/ (function(module, exports) {\n" +
            "\n" +
            "    module.exports =\n" +
            "      /******/ (function(modules) { // webpackBootstrap\n" +
            "      /******/ \t// The module cache\n" +
            "      /******/ \tvar installedModules = {};\n" +
            "\n" +
            "      /******/ \t// The require function\n" +
            "      /******/ \tfunction __webpack_require__(moduleId) {\n" +
            "\n" +
            "        /******/ \t\t// Check if module is in cache\n" +
            "        /******/ \t\tif(installedModules[moduleId])\n" +
            "        /******/ \t\t\treturn installedModules[moduleId].exports;\n" +
            "\n" +
            "        /******/ \t\t// Create a new module (and put it into the cache)\n" +
            "        /******/ \t\tvar module = installedModules[moduleId] = {\n" +
            "          /******/ \t\t\texports: {},\n" +
            "          /******/ \t\t\tid: moduleId,\n" +
            "          /******/ \t\t\tloaded: false\n" +
            "          /******/ \t\t};\n" +
            "\n" +
            "        /******/ \t\t// Execute the module function\n" +
            "        /******/ \t\tmodules[moduleId].call(module.exports, module, module.exports, __webpack_require__);\n" +
            "\n" +
            "        /******/ \t\t// Flag the module as loaded\n" +
            "        /******/ \t\tmodule.loaded = true;\n" +
            "\n" +
            "        /******/ \t\t// Return the exports of the module\n" +
            "        /******/ \t\treturn module.exports;\n" +
            "        /******/ \t}\n" +
            "\n" +
            "\n" +
            "      /******/ \t// expose the modules object (__webpack_modules__)\n" +
            "      /******/ \t__webpack_require__.m = modules;\n" +
            "\n" +
            "      /******/ \t// expose the module cache\n" +
            "      /******/ \t__webpack_require__.c = installedModules;\n" +
            "\n" +
            "      /******/ \t// __webpack_public_path__\n" +
            "      /******/ \t__webpack_require__.p = \"\";\n" +
            "\n" +
            "      /******/ \t// Load entry module and return exports\n" +
            "      /******/ \treturn __webpack_require__(0);\n" +
            "      /******/ })\n" +
            "    /************************************************************************/\n" +
            "    /******/ ([\n" +
            "      /* 0 */\n" +
            "      /***/ function(module, exports, __webpack_require__) {\n" +
            "\n" +
            "        module.exports = __webpack_require__(1);\n" +
            "\n" +
            "\n" +
            "        /***/ },\n" +
            "      /* 1 */\n" +
            "      /***/ function(module, exports, __webpack_require__) {\n" +
            "\n" +
            "        'use strict';\n" +
            "\n" +
            "        Object.defineProperty(exports, \"__esModule\", {\n" +
            "          value: true\n" +
            "        });\n" +
            "        exports.print = print;\n" +
            "\n" +
            "        var _visitor = __webpack_require__(2);\n" +
            "\n" +
            "        /**\n" +
            "         * Converts an AST into a string, using one set of reasonable\n" +
            "         * formatting rules.\n" +
            "         */\n" +
            "        function print(ast) {\n" +
            "          return (0, _visitor.visit)(ast, { leave: printDocASTReducer });\n" +
            "        } /**\n" +
            "         *  Copyright (c) 2015, Facebook, Inc.\n" +
            "         *  All rights reserved.\n" +
            "         *\n" +
            "         *  This source code is licensed under the BSD-style license found in the\n" +
            "         *  LICENSE file in the root directory of this source tree. An additional grant\n" +
            "         *  of patent rights can be found in the PATENTS file in the same directory.\n" +
            "         */\n" +
            "\n" +
            "        var printDocASTReducer = {\n" +
            "            Name: function Name(node) {\n" +
            "              return node.value;\n" +
            "            },\n" +
            "            Variable: function Variable(node) {\n" +
            "              return '$' + node.name;\n" +
            "            },\n" +
            "\n" +
            "            // Document\n" +
            "\n" +
            "            Document: function Document(node) {\n" +
            "              return join(node.definitions, '\\n\\n') + '\\n';\n" +
            "            },\n" +
            "\n" +
            "            OperationDefinition: function OperationDefinition(node) {\n" +
            "              var op = node.operation;\n" +
            "              var name = node.name;\n" +
            "              var varDefs = wrap('(', join(node.variableDefinitions, ', '), ')');\n" +
            "              var directives = join(node.directives, ' ');\n" +
            "              var selectionSet = node.selectionSet;\n" +
            "              // Anonymous queries with no directives or variable definitions can use\n" +
            "              // the query short form.\n" +
            "              return !name && !directives && !varDefs && op === 'query' ? selectionSet : join([op, join([name, varDefs]), directives, selectionSet], ' ');\n" +
            "            },\n" +
            "\n" +
            "\n" +
            "            VariableDefinition: function VariableDefinition(_ref) {\n" +
            "              var variable = _ref.variable;\n" +
            "              var type = _ref.type;\n" +
            "              var defaultValue = _ref.defaultValue;\n" +
            "              return variable + ': ' + type + wrap(' = ', defaultValue);\n" +
            "            },\n" +
            "\n" +
            "            SelectionSet: function SelectionSet(_ref2) {\n" +
            "              var selections = _ref2.selections;\n" +
            "              return block(selections);\n" +
            "            },\n" +
            "\n" +
            "            Field: function Field(_ref3) {\n" +
            "              var alias = _ref3.alias;\n" +
            "              var name = _ref3.name;\n" +
            "              var args = _ref3.arguments;\n" +
            "              var directives = _ref3.directives;\n" +
            "              var selectionSet = _ref3.selectionSet;\n" +
            "              return join([wrap('', alias, ': ') + name + wrap('(', join(args, ', '), ')'), join(directives, ' '), selectionSet], ' ');\n" +
            "            },\n" +
            "\n" +
            "            Argument: function Argument(_ref4) {\n" +
            "              var name = _ref4.name;\n" +
            "              var value = _ref4.value;\n" +
            "              return name + ': ' + value;\n" +
            "            },\n" +
            "\n" +
            "            // Fragments\n" +
            "\n" +
            "            FragmentSpread: function FragmentSpread(_ref5) {\n" +
            "              var name = _ref5.name;\n" +
            "              var directives = _ref5.directives;\n" +
            "              return '...' + name + wrap(' ', join(directives, ' '));\n" +
            "            },\n" +
            "\n" +
            "            InlineFragment: function InlineFragment(_ref6) {\n" +
            "              var typeCondition = _ref6.typeCondition;\n" +
            "              var directives = _ref6.directives;\n" +
            "              var selectionSet = _ref6.selectionSet;\n" +
            "              return join(['...', wrap('on ', typeCondition), join(directives, ' '), selectionSet], ' ');\n" +
            "            },\n" +
            "\n" +
            "            FragmentDefinition: function FragmentDefinition(_ref7) {\n" +
            "              var name = _ref7.name;\n" +
            "              var typeCondition = _ref7.typeCondition;\n" +
            "              var directives = _ref7.directives;\n" +
            "              var selectionSet = _ref7.selectionSet;\n" +
            "              return 'fragment ' + name + ' on ' + typeCondition + ' ' + wrap('', join(directives, ' '), ' ') + selectionSet;\n" +
            "            },\n" +
            "\n" +
            "            // Value\n" +
            "\n" +
            "            IntValue: function IntValue(_ref8) {\n" +
            "              var value = _ref8.value;\n" +
            "              return value;\n" +
            "            },\n" +
            "            FloatValue: function FloatValue(_ref9) {\n" +
            "              var value = _ref9.value;\n" +
            "              return value;\n" +
            "            },\n" +
            "            StringValue: function StringValue(_ref10) {\n" +
            "              var value = _ref10.value;\n" +
            "              return JSON.stringify(value);\n" +
            "            },\n" +
            "            BooleanValue: function BooleanValue(_ref11) {\n" +
            "              var value = _ref11.value;\n" +
            "              return JSON.stringify(value);\n" +
            "            },\n" +
            "            EnumValue: function EnumValue(_ref12) {\n" +
            "              var value = _ref12.value;\n" +
            "              return value;\n" +
            "            },\n" +
            "            ListValue: function ListValue(_ref13) {\n" +
            "              var values = _ref13.values;\n" +
            "              return '[' + join(values, ', ') + ']';\n" +
            "            },\n" +
            "            ObjectValue: function ObjectValue(_ref14) {\n" +
            "              var fields = _ref14.fields;\n" +
            "              return '{' + join(fields, ', ') + '}';\n" +
            "            },\n" +
            "            ObjectField: function ObjectField(_ref15) {\n" +
            "              var name = _ref15.name;\n" +
            "              var value = _ref15.value;\n" +
            "              return name + ': ' + value;\n" +
            "            },\n" +
            "\n" +
            "            // Directive\n" +
            "\n" +
            "            Directive: function Directive(_ref16) {\n" +
            "              var name = _ref16.name;\n" +
            "              var args = _ref16.arguments;\n" +
            "              return '@' + name + wrap('(', join(args, ', '), ')');\n" +
            "            },\n" +
            "\n" +
            "            // Type\n" +
            "\n" +
            "            NamedType: function NamedType(_ref17) {\n" +
            "              var name = _ref17.name;\n" +
            "              return name;\n" +
            "            },\n" +
            "            ListType: function ListType(_ref18) {\n" +
            "              var type = _ref18.type;\n" +
            "              return '[' + type + ']';\n" +
            "            },\n" +
            "            NonNullType: function NonNullType(_ref19) {\n" +
            "              var type = _ref19.type;\n" +
            "              return type + '!';\n" +
            "            },\n" +
            "\n" +
            "            // Type System Definitions\n" +
            "\n" +
            "            SchemaDefinition: function SchemaDefinition(_ref20) {\n" +
            "              var directives = _ref20.directives;\n" +
            "              var operationTypes = _ref20.operationTypes;\n" +
            "              return join(['schema', join(directives, ' '), block(operationTypes)], ' ');\n" +
            "            },\n" +
            "\n" +
            "            OperationTypeDefinition: function OperationTypeDefinition(_ref21) {\n" +
            "              var operation = _ref21.operation;\n" +
            "              var type = _ref21.type;\n" +
            "              return operation + ': ' + type;\n" +
            "            },\n" +
            "\n" +
            "            ScalarTypeDefinition: function ScalarTypeDefinition(_ref22) {\n" +
            "              var name = _ref22.name;\n" +
            "              var directives = _ref22.directives;\n" +
            "              return join(['scalar', name, join(directives, ' ')], ' ');\n" +
            "            },\n" +
            "\n" +
            "            ObjectTypeDefinition: function ObjectTypeDefinition(_ref23) {\n" +
            "              var name = _ref23.name;\n" +
            "              var interfaces = _ref23.interfaces;\n" +
            "              var directives = _ref23.directives;\n" +
            "              var fields = _ref23.fields;\n" +
            "              return join(['type', name, wrap('implements ', join(interfaces, ', ')), join(directives, ' '), block(fields)], ' ');\n" +
            "            },\n" +
            "\n" +
            "            FieldDefinition: function FieldDefinition(_ref24) {\n" +
            "              var name = _ref24.name;\n" +
            "              var args = _ref24.arguments;\n" +
            "              var type = _ref24.type;\n" +
            "              var directives = _ref24.directives;\n" +
            "              return name + wrap('(', join(args, ', '), ')') + ': ' + type + wrap(' ', join(directives, ' '));\n" +
            "            },\n" +
            "\n" +
            "            InputValueDefinition: function InputValueDefinition(_ref25) {\n" +
            "              var name = _ref25.name;\n" +
            "              var type = _ref25.type;\n" +
            "              var defaultValue = _ref25.defaultValue;\n" +
            "              var directives = _ref25.directives;\n" +
            "              return join([name + ': ' + type, wrap('= ', defaultValue), join(directives, ' ')], ' ');\n" +
            "            },\n" +
            "\n" +
            "            InterfaceTypeDefinition: function InterfaceTypeDefinition(_ref26) {\n" +
            "              var name = _ref26.name;\n" +
            "              var directives = _ref26.directives;\n" +
            "              var fields = _ref26.fields;\n" +
            "              return join(['interface', name, join(directives, ' '), block(fields)], ' ');\n" +
            "            },\n" +
            "\n" +
            "            UnionTypeDefinition: function UnionTypeDefinition(_ref27) {\n" +
            "              var name = _ref27.name;\n" +
            "              var directives = _ref27.directives;\n" +
            "              var types = _ref27.types;\n" +
            "              return join(['union', name, join(directives, ' '), '= ' + join(types, ' | ')], ' ');\n" +
            "            },\n" +
            "\n" +
            "            EnumTypeDefinition: function EnumTypeDefinition(_ref28) {\n" +
            "              var name = _ref28.name;\n" +
            "              var directives = _ref28.directives;\n" +
            "              var values = _ref28.values;\n" +
            "              return join(['enum', name, join(directives, ' '), block(values)], ' ');\n" +
            "            },\n" +
            "\n" +
            "            EnumValueDefinition: function EnumValueDefinition(_ref29) {\n" +
            "              var name = _ref29.name;\n" +
            "              var directives = _ref29.directives;\n" +
            "              return join([name, join(directives, ' ')], ' ');\n" +
            "            },\n" +
            "\n" +
            "            InputObjectTypeDefinition: function InputObjectTypeDefinition(_ref30) {\n" +
            "              var name = _ref30.name;\n" +
            "              var directives = _ref30.directives;\n" +
            "              var fields = _ref30.fields;\n" +
            "              return join(['input', name, join(directives, ' '), block(fields)], ' ');\n" +
            "            },\n" +
            "\n" +
            "            TypeExtensionDefinition: function TypeExtensionDefinition(_ref31) {\n" +
            "              var definition = _ref31.definition;\n" +
            "              return 'extend ' + definition;\n" +
            "            },\n" +
            "\n" +
            "            DirectiveDefinition: function DirectiveDefinition(_ref32) {\n" +
            "              var name = _ref32.name;\n" +
            "              var args = _ref32.arguments;\n" +
            "              var locations = _ref32.locations;\n" +
            "              return 'directive @' + name + wrap('(', join(args, ', '), ')') + ' on ' + join(locations, ' | ');\n" +
            "            }\n" +
            "          };\n" +
            "\n" +
            "        /**\n" +
            "         * Given maybeArray, print an empty string if it is null or empty, otherwise\n" +
            "         * print all items together separated by separator if provided\n" +
            "         */\n" +
            "        function join(maybeArray, separator) {\n" +
            "          return maybeArray ? maybeArray.filter(function (x) {\n" +
            "              return x;\n" +
            "            }).join(separator || '') : '';\n" +
            "        }\n" +
            "\n" +
            "        /**\n" +
            "         * Given array, print each item on its own line, wrapped in an\n" +
            "         * indented \"{ }\" block.\n" +
            "         */\n" +
            "        function block(array) {\n" +
            "          return array && array.length !== 0 ? indent('{\\n' + join(array, '\\n')) + '\\n}' : '{}';\n" +
            "        }\n" +
            "\n" +
            "        /**\n" +
            "         * If maybeString is not null or empty, then wrap with start and end, otherwise\n" +
            "         * print an empty string.\n" +
            "         */\n" +
            "        function wrap(start, maybeString, end) {\n" +
            "          return maybeString ? start + maybeString + (end || '') : '';\n" +
            "        }\n" +
            "\n" +
            "        function indent(maybeString) {\n" +
            "          return maybeString && maybeString.replace(/\\n/g, '\\n  ');\n" +
            "        }\n" +
            "\n" +
            "        /***/ },\n" +
            "      /* 2 */\n" +
            "      /***/ function(module, exports) {\n" +
            "\n" +
            "        'use strict';\n" +
            "\n" +
            "        Object.defineProperty(exports, \"__esModule\", {\n" +
            "          value: true\n" +
            "        });\n" +
            "        exports.visit = visit;\n" +
            "        exports.visitInParallel = visitInParallel;\n" +
            "        exports.visitWithTypeInfo = visitWithTypeInfo;\n" +
            "        /**\n" +
            "         *  Copyright (c) 2015, Facebook, Inc.\n" +
            "         *  All rights reserved.\n" +
            "         *\n" +
            "         *  This source code is licensed under the BSD-style license found in the\n" +
            "         *  LICENSE file in the root directory of this source tree. An additional grant\n" +
            "         *  of patent rights can be found in the PATENTS file in the same directory.\n" +
            "         */\n" +
            "\n" +
            "        var QueryDocumentKeys = exports.QueryDocumentKeys = {\n" +
            "          Name: [],\n" +
            "\n" +
            "          Document: ['definitions'],\n" +
            "          OperationDefinition: ['name', 'variableDefinitions', 'directives', 'selectionSet'],\n" +
            "          VariableDefinition: ['variable', 'type', 'defaultValue'],\n" +
            "          Variable: ['name'],\n" +
            "          SelectionSet: ['selections'],\n" +
            "          Field: ['alias', 'name', 'arguments', 'directives', 'selectionSet'],\n" +
            "          Argument: ['name', 'value'],\n" +
            "\n" +
            "          FragmentSpread: ['name', 'directives'],\n" +
            "          InlineFragment: ['typeCondition', 'directives', 'selectionSet'],\n" +
            "          FragmentDefinition: ['name', 'typeCondition', 'directives', 'selectionSet'],\n" +
            "\n" +
            "          IntValue: [],\n" +
            "          FloatValue: [],\n" +
            "          StringValue: [],\n" +
            "          BooleanValue: [],\n" +
            "          EnumValue: [],\n" +
            "          ListValue: ['values'],\n" +
            "          ObjectValue: ['fields'],\n" +
            "          ObjectField: ['name', 'value'],\n" +
            "\n" +
            "          Directive: ['name', 'arguments'],\n" +
            "\n" +
            "          NamedType: ['name'],\n" +
            "          ListType: ['type'],\n" +
            "          NonNullType: ['type'],\n" +
            "\n" +
            "          SchemaDefinition: ['directives', 'operationTypes'],\n" +
            "          OperationTypeDefinition: ['type'],\n" +
            "\n" +
            "          ScalarTypeDefinition: ['name', 'directives'],\n" +
            "          ObjectTypeDefinition: ['name', 'interfaces', 'directives', 'fields'],\n" +
            "          FieldDefinition: ['name', 'arguments', 'type', 'directives'],\n" +
            "          InputValueDefinition: ['name', 'type', 'defaultValue', 'directives'],\n" +
            "          InterfaceTypeDefinition: ['name', 'directives', 'fields'],\n" +
            "          UnionTypeDefinition: ['name', 'directives', 'types'],\n" +
            "          EnumTypeDefinition: ['name', 'directives', 'values'],\n" +
            "          EnumValueDefinition: ['name', 'directives'],\n" +
            "          InputObjectTypeDefinition: ['name', 'directives', 'fields'],\n" +
            "\n" +
            "          TypeExtensionDefinition: ['definition'],\n" +
            "\n" +
            "          DirectiveDefinition: ['name', 'arguments', 'locations']\n" +
            "        };\n" +
            "\n" +
            "        var BREAK = exports.BREAK = {};\n" +
            "\n" +
            "        /**\n" +
            "         * visit() will walk through an AST using a depth first traversal, calling\n" +
            "         * the visitor's enter function at each node in the traversal, and calling the\n" +
            "         * leave function after visiting that node and all of its child nodes.\n" +
            "         *\n" +
            "         * By returning different values from the enter and leave functions, the\n" +
            "         * behavior of the visitor can be altered, including skipping over a sub-tree of\n" +
            "         * the AST (by returning false), editing the AST by returning a value or null\n" +
            "         * to remove the value, or to stop the whole traversal by returning BREAK.\n" +
            "         *\n" +
            "         * When using visit() to edit an AST, the original AST will not be modified, and\n" +
            "         * a new version of the AST with the changes applied will be returned from the\n" +
            "         * visit function.\n" +
            "         *\n" +
            "         *     const editedAST = visit(ast, {\n" +
            "\t *       enter(node, key, parent, path, ancestors) {\n" +
            "\t *         // @return\n" +
            "\t *         //   undefined: no action\n" +
            "\t *         //   false: skip visiting this node\n" +
            "\t *         //   visitor.BREAK: stop visiting altogether\n" +
            "\t *         //   null: delete this node\n" +
            "\t *         //   any value: replace this node with the returned value\n" +
            "\t *       },\n" +
            "\t *       leave(node, key, parent, path, ancestors) {\n" +
            "\t *         // @return\n" +
            "\t *         //   undefined: no action\n" +
            "\t *         //   false: no action\n" +
            "\t *         //   visitor.BREAK: stop visiting altogether\n" +
            "\t *         //   null: delete this node\n" +
            "\t *         //   any value: replace this node with the returned value\n" +
            "\t *       }\n" +
            "\t *     });\n" +
            "         *\n" +
            "         * Alternatively to providing enter() and leave() functions, a visitor can\n" +
            "         * instead provide functions named the same as the kinds of AST nodes, or\n" +
            "         * enter/leave visitors at a named key, leading to four permutations of\n" +
            "         * visitor API:\n" +
            "         *\n" +
            "         * 1) Named visitors triggered when entering a node a specific kind.\n" +
            "         *\n" +
            "         *     visit(ast, {\n" +
            "\t *       Kind(node) {\n" +
            "\t *         // enter the \"Kind\" node\n" +
            "\t *       }\n" +
            "\t *     })\n" +
            "         *\n" +
            "         * 2) Named visitors that trigger upon entering and leaving a node of\n" +
            "         *    a specific kind.\n" +
            "         *\n" +
            "         *     visit(ast, {\n" +
            "\t *       Kind: {\n" +
            "\t *         enter(node) {\n" +
            "\t *           // enter the \"Kind\" node\n" +
            "\t *         }\n" +
            "\t *         leave(node) {\n" +
            "\t *           // leave the \"Kind\" node\n" +
            "\t *         }\n" +
            "\t *       }\n" +
            "\t *     })\n" +
            "         *\n" +
            "         * 3) Generic visitors that trigger upon entering and leaving any node.\n" +
            "         *\n" +
            "         *     visit(ast, {\n" +
            "\t *       enter(node) {\n" +
            "\t *         // enter any node\n" +
            "\t *       },\n" +
            "\t *       leave(node) {\n" +
            "\t *         // leave any node\n" +
            "\t *       }\n" +
            "\t *     })\n" +
            "         *\n" +
            "         * 4) Parallel visitors for entering and leaving nodes of a specific kind.\n" +
            "         *\n" +
            "         *     visit(ast, {\n" +
            "\t *       enter: {\n" +
            "\t *         Kind(node) {\n" +
            "\t *           // enter the \"Kind\" node\n" +
            "\t *         }\n" +
            "\t *       },\n" +
            "\t *       leave: {\n" +
            "\t *         Kind(node) {\n" +
            "\t *           // leave the \"Kind\" node\n" +
            "\t *         }\n" +
            "\t *       }\n" +
            "\t *     })\n" +
            "         */\n" +
            "        function visit(root, visitor, keyMap) {\n" +
            "          var visitorKeys = keyMap || QueryDocumentKeys;\n" +
            "\n" +
            "          var stack = void 0;\n" +
            "          var inArray = Array.isArray(root);\n" +
            "          var keys = [root];\n" +
            "          var index = -1;\n" +
            "          var edits = [];\n" +
            "          var parent = void 0;\n" +
            "          var path = [];\n" +
            "          var ancestors = [];\n" +
            "          var newRoot = root;\n" +
            "\n" +
            "          do {\n" +
            "            index++;\n" +
            "            var isLeaving = index === keys.length;\n" +
            "            var key = void 0;\n" +
            "            var node = void 0;\n" +
            "            var isEdited = isLeaving && edits.length !== 0;\n" +
            "            if (isLeaving) {\n" +
            "              key = ancestors.length === 0 ? undefined : path.pop();\n" +
            "              node = parent;\n" +
            "              parent = ancestors.pop();\n" +
            "              if (isEdited) {\n" +
            "                if (inArray) {\n" +
            "                  node = node.slice();\n" +
            "                } else {\n" +
            "                  var clone = {};\n" +
            "                  for (var k in node) {\n" +
            "                    if (node.hasOwnProperty(k)) {\n" +
            "                      clone[k] = node[k];\n" +
            "                    }\n" +
            "                  }\n" +
            "                  node = clone;\n" +
            "                }\n" +
            "                var editOffset = 0;\n" +
            "                for (var ii = 0; ii < edits.length; ii++) {\n" +
            "                  var editKey = edits[ii][0];\n" +
            "                  var editValue = edits[ii][1];\n" +
            "                  if (inArray) {\n" +
            "                    editKey -= editOffset;\n" +
            "                  }\n" +
            "                  if (inArray && editValue === null) {\n" +
            "                    node.splice(editKey, 1);\n" +
            "                    editOffset++;\n" +
            "                  } else {\n" +
            "                    node[editKey] = editValue;\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "              index = stack.index;\n" +
            "              keys = stack.keys;\n" +
            "              edits = stack.edits;\n" +
            "              inArray = stack.inArray;\n" +
            "              stack = stack.prev;\n" +
            "            } else {\n" +
            "              key = parent ? inArray ? index : keys[index] : undefined;\n" +
            "              node = parent ? parent[key] : newRoot;\n" +
            "              if (node === null || node === undefined) {\n" +
            "                continue;\n" +
            "              }\n" +
            "              if (parent) {\n" +
            "                path.push(key);\n" +
            "              }\n" +
            "            }\n" +
            "\n" +
            "            var result = void 0;\n" +
            "            if (!Array.isArray(node)) {\n" +
            "              if (!isNode(node)) {\n" +
            "                throw new Error('Invalid AST Node: ' + JSON.stringify(node));\n" +
            "              }\n" +
            "              var visitFn = getVisitFn(visitor, node.kind, isLeaving);\n" +
            "              if (visitFn) {\n" +
            "                result = visitFn.call(visitor, node, key, parent, path, ancestors);\n" +
            "\n" +
            "                if (result === BREAK) {\n" +
            "                  break;\n" +
            "                }\n" +
            "\n" +
            "                if (result === false) {\n" +
            "                  if (!isLeaving) {\n" +
            "                    path.pop();\n" +
            "                    continue;\n" +
            "                  }\n" +
            "                } else if (result !== undefined) {\n" +
            "                  edits.push([key, result]);\n" +
            "                  if (!isLeaving) {\n" +
            "                    if (isNode(result)) {\n" +
            "                      node = result;\n" +
            "                    } else {\n" +
            "                      path.pop();\n" +
            "                      continue;\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "\n" +
            "            if (result === undefined && isEdited) {\n" +
            "              edits.push([key, node]);\n" +
            "            }\n" +
            "\n" +
            "            if (!isLeaving) {\n" +
            "              stack = { inArray: inArray, index: index, keys: keys, edits: edits, prev: stack };\n" +
            "              inArray = Array.isArray(node);\n" +
            "              keys = inArray ? node : visitorKeys[node.kind] || [];\n" +
            "              index = -1;\n" +
            "              edits = [];\n" +
            "              if (parent) {\n" +
            "                ancestors.push(parent);\n" +
            "              }\n" +
            "              parent = node;\n" +
            "            }\n" +
            "          } while (stack !== undefined);\n" +
            "\n" +
            "          if (edits.length !== 0) {\n" +
            "            newRoot = edits[edits.length - 1][1];\n" +
            "          }\n" +
            "\n" +
            "          return newRoot;\n" +
            "        }\n" +
            "\n" +
            "        function isNode(maybeNode) {\n" +
            "          return maybeNode && typeof maybeNode.kind === 'string';\n" +
            "        }\n" +
            "\n" +
            "        /**\n" +
            "         * Creates a new visitor instance which delegates to many visitors to run in\n" +
            "         * parallel. Each visitor will be visited for each node before moving on.\n" +
            "         *\n" +
            "         * If a prior visitor edits a node, no following visitors will see that node.\n" +
            "         */\n" +
            "        function visitInParallel(visitors) {\n" +
            "          var skipping = new Array(visitors.length);\n" +
            "\n" +
            "          return {\n" +
            "            enter: function enter(node) {\n" +
            "              for (var i = 0; i < visitors.length; i++) {\n" +
            "                if (!skipping[i]) {\n" +
            "                  var fn = getVisitFn(visitors[i], node.kind, /* isLeaving */false);\n" +
            "                  if (fn) {\n" +
            "                    var result = fn.apply(visitors[i], arguments);\n" +
            "                    if (result === false) {\n" +
            "                      skipping[i] = node;\n" +
            "                    } else if (result === BREAK) {\n" +
            "                      skipping[i] = BREAK;\n" +
            "                    } else if (result !== undefined) {\n" +
            "                      return result;\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            },\n" +
            "            leave: function leave(node) {\n" +
            "              for (var i = 0; i < visitors.length; i++) {\n" +
            "                if (!skipping[i]) {\n" +
            "                  var fn = getVisitFn(visitors[i], node.kind, /* isLeaving */true);\n" +
            "                  if (fn) {\n" +
            "                    var result = fn.apply(visitors[i], arguments);\n" +
            "                    if (result === BREAK) {\n" +
            "                      skipping[i] = BREAK;\n" +
            "                    } else if (result !== undefined && result !== false) {\n" +
            "                      return result;\n" +
            "                    }\n" +
            "                  }\n" +
            "                } else if (skipping[i] === node) {\n" +
            "                  skipping[i] = null;\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          };\n" +
            "        }\n" +
            "\n" +
            "        /**\n" +
            "         * Creates a new visitor instance which maintains a provided TypeInfo instance\n" +
            "         * along with visiting visitor.\n" +
            "         */\n" +
            "        function visitWithTypeInfo(typeInfo, visitor) {\n" +
            "          return {\n" +
            "            enter: function enter(node) {\n" +
            "              typeInfo.enter(node);\n" +
            "              var fn = getVisitFn(visitor, node.kind, /* isLeaving */false);\n" +
            "              if (fn) {\n" +
            "                var result = fn.apply(visitor, arguments);\n" +
            "                if (result !== undefined) {\n" +
            "                  typeInfo.leave(node);\n" +
            "                  if (isNode(result)) {\n" +
            "                    typeInfo.enter(result);\n" +
            "                  }\n" +
            "                }\n" +
            "                return result;\n" +
            "              }\n" +
            "            },\n" +
            "            leave: function leave(node) {\n" +
            "              var fn = getVisitFn(visitor, node.kind, /* isLeaving */true);\n" +
            "              var result = void 0;\n" +
            "              if (fn) {\n" +
            "                result = fn.apply(visitor, arguments);\n" +
            "              }\n" +
            "              typeInfo.leave(node);\n" +
            "              return result;\n" +
            "            }\n" +
            "          };\n" +
            "        }\n" +
            "\n" +
            "        /**\n" +
            "         * Given a visitor instance, if it is leaving or not, and a node kind, return\n" +
            "         * the function the visitor runtime should call.\n" +
            "         */\n" +
            "        function getVisitFn(visitor, kind, isLeaving) {\n" +
            "          var kindVisitor = visitor[kind];\n" +
            "          if (kindVisitor) {\n" +
            "            if (!isLeaving && typeof kindVisitor === 'function') {\n" +
            "              // { Kind() {} }\n" +
            "              return kindVisitor;\n" +
            "            }\n" +
            "            var kindSpecificVisitor = isLeaving ? kindVisitor.leave : kindVisitor.enter;\n" +
            "            if (typeof kindSpecificVisitor === 'function') {\n" +
            "              // { Kind: { enter() {}, leave() {} } }\n" +
            "              return kindSpecificVisitor;\n" +
            "            }\n" +
            "          } else {\n" +
            "            var specificVisitor = isLeaving ? visitor.leave : visitor.enter;\n" +
            "            if (specificVisitor) {\n" +
            "              if (typeof specificVisitor === 'function') {\n" +
            "                // { enter() {}, leave() {} }\n" +
            "                return specificVisitor;\n" +
            "              }\n" +
            "              var specificKindVisitor = specificVisitor[kind];\n" +
            "              if (typeof specificKindVisitor === 'function') {\n" +
            "                // { enter: { Kind() {} }, leave: { Kind() {} } }\n" +
            "                return specificKindVisitor;\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "\n" +
            "        /***/ }\n" +
            "      /******/ ]);\n" +
            "\n" +
            "    /***/ }),\n" +
            "  /* 9 */\n" +
            "  /***/ (function(module, exports, __webpack_require__) {\n" +
            "\n" +
            "    \"use strict\";\n" +
            "    /* WEBPACK VAR INJECTION */(function(global) {\n" +
            "      function __export(m) {\n" +
            "        for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];\n" +
            "      }\n" +
            "      var Backoff = __webpack_require__(3);\n" +
            "      var eventemitter3_1 = __webpack_require__(4);\n" +
            "      var _global = typeof global !== 'undefined' ? global : (typeof window !== 'undefined' ? window : {});\n" +
            "      var NativeWebSocket = _global.WebSocket || _global.MozWebSocket;\n" +
            "      var messageTypes_1 = __webpack_require__(1);\n" +
            "      var protocols_1 = __webpack_require__(2);\n" +
            "      var isString = __webpack_require__(6);\n" +
            "      var isObject = __webpack_require__(5);\n" +
            "      __export(__webpack_require__(0));\n" +
            "      var DEFAULT_SUBSCRIPTION_TIMEOUT = 5000;\n" +
            "      var SubscriptionClient = (function () {\n" +
            "        function SubscriptionClient(url, options, webSocketImpl) {\n" +
            "          var _a = (options || {}), _b = _a.connectionCallback, connectionCallback = _b === void 0 ? undefined : _b, _c = _a.connectionParams, connectionParams = _c === void 0 ? {} : _c, _d = _a.timeout, timeout = _d === void 0 ? DEFAULT_SUBSCRIPTION_TIMEOUT : _d, _e = _a.reconnect, reconnect = _e === void 0 ? false : _e, _f = _a.reconnectionAttempts, reconnectionAttempts = _f === void 0 ? Infinity : _f;\n" +
            "          this.wsImpl = webSocketImpl || NativeWebSocket;\n" +
            "          if (!this.wsImpl) {\n" +
            "            throw new Error('Unable to find native implementation, or alternative implementation for WebSocket!');\n" +
            "          }\n" +
            "          this.connectionParams = connectionParams;\n" +
            "          this.connectionCallback = connectionCallback;\n" +
            "          this.url = url;\n" +
            "          this.subscriptions = {};\n" +
            "          this.maxId = 0;\n" +
            "          this.subscriptionTimeout = timeout;\n" +
            "          this.waitingSubscriptions = {};\n" +
            "          this.unsentMessagesQueue = [];\n" +
            "          this.reconnect = reconnect;\n" +
            "          this.reconnectSubscriptions = {};\n" +
            "          this.reconnecting = false;\n" +
            "          this.reconnectionAttempts = reconnectionAttempts;\n" +
            "          this.backoff = new Backoff({ jitter: 0.5 });\n" +
            "          this.eventEmitter = new eventemitter3_1.EventEmitter();\n" +
            "          this.connect();\n" +
            "        }\n" +
            "        Object.defineProperty(SubscriptionClient.prototype, \"status\", {\n" +
            "          get: function () {\n" +
            "            return this.client.readyState;\n" +
            "          },\n" +
            "          enumerable: true,\n" +
            "          configurable: true\n" +
            "        });\n" +
            "        SubscriptionClient.prototype.close = function () {\n" +
            "          this.client.close();\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.subscribe = function (options, handler) {\n" +
            "          var _this = this;\n" +
            "          var query = options.query, variables = options.variables, operationName = options.operationName, context = options.context;\n" +
            "          if (!query) {\n" +
            "            throw new Error('Must provide `query` to subscribe.');\n" +
            "          }\n" +
            "          if (!handler) {\n" +
            "            throw new Error('Must provide `handler` to subscribe.');\n" +
            "          }\n" +
            "          if (!isString(query) ||\n" +
            "            (operationName && !isString(operationName)) ||\n" +
            "            (variables && !isObject(variables))) {\n" +
            "            throw new Error('Incorrect option types to subscribe. `subscription` must be a string,' +\n" +
            "              '`operationName` must be a string, and `variables` must be an object.');\n" +
            "          }\n" +
            "          var subId = this.generateSubscriptionId();\n" +
            "          var message = Object.assign(options, { type: messageTypes_1.SUBSCRIPTION_START, id: subId });\n" +
            "          this.sendMessage(message);\n" +
            "          this.subscriptions[subId] = { options: options, handler: handler };\n" +
            "          this.waitingSubscriptions[subId] = true;\n" +
            "          setTimeout(function () {\n" +
            "            if (_this.waitingSubscriptions[subId]) {\n" +
            "              handler([new Error('Subscription timed out - no response from server')]);\n" +
            "              _this.unsubscribe(subId);\n" +
            "            }\n" +
            "          }, this.subscriptionTimeout);\n" +
            "          return subId;\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.on = function (eventName, callback, context) {\n" +
            "          var handler = this.eventEmitter.on(eventName, callback, context);\n" +
            "          return function () {\n" +
            "            handler.off(eventName, callback, context);\n" +
            "          };\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.onConnect = function (callback, context) {\n" +
            "          return this.on('connect', callback, context);\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.onDisconnect = function (callback, context) {\n" +
            "          return this.on('disconnect', callback, context);\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.onReconnect = function (callback, context) {\n" +
            "          return this.on('reconnect', callback, context);\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.unsubscribe = function (id) {\n" +
            "          delete this.subscriptions[id];\n" +
            "          delete this.waitingSubscriptions[id];\n" +
            "          var message = { id: id, type: messageTypes_1.SUBSCRIPTION_END };\n" +
            "          this.sendMessage(message);\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.unsubscribeAll = function () {\n" +
            "          var _this = this;\n" +
            "          Object.keys(this.subscriptions).forEach(function (subId) {\n" +
            "            _this.unsubscribe(parseInt(subId));\n" +
            "          });\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.sendMessage = function (message) {\n" +
            "          switch (this.client.readyState) {\n" +
            "            case this.client.OPEN:\n" +
            "              this.client.send(JSON.stringify(message));\n" +
            "              break;\n" +
            "            case this.client.CONNECTING:\n" +
            "              this.unsentMessagesQueue.push(message);\n" +
            "              break;\n" +
            "            case this.client.CLOSING:\n" +
            "            case this.client.CLOSED:\n" +
            "            default:\n" +
            "              if (!this.reconnecting) {\n" +
            "                throw new Error('Client is not connected to a websocket.');\n" +
            "              }\n" +
            "          }\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.generateSubscriptionId = function () {\n" +
            "          var id = this.maxId;\n" +
            "          this.maxId += 1;\n" +
            "          return id;\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.formatErrors = function (errors) {\n" +
            "          if (Array.isArray(errors)) {\n" +
            "            return errors;\n" +
            "          }\n" +
            "          if (errors && errors.message) {\n" +
            "            return [errors];\n" +
            "          }\n" +
            "          return [{ message: 'Unknown error' }];\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.tryReconnect = function () {\n" +
            "          var _this = this;\n" +
            "          if (!this.reconnect) {\n" +
            "            return;\n" +
            "          }\n" +
            "          if (this.backoff.attempts > this.reconnectionAttempts) {\n" +
            "            return;\n" +
            "          }\n" +
            "          if (!this.reconnecting) {\n" +
            "            this.reconnectSubscriptions = this.subscriptions;\n" +
            "            this.subscriptions = {};\n" +
            "            this.waitingSubscriptions = {};\n" +
            "            this.reconnecting = true;\n" +
            "          }\n" +
            "          var delay = this.backoff.duration();\n" +
            "          setTimeout(function () {\n" +
            "            _this.connect(true);\n" +
            "          }, delay);\n" +
            "        };\n" +
            "        SubscriptionClient.prototype.connect = function (isReconnect) {\n" +
            "          var _this = this;\n" +
            "          if (isReconnect === void 0) { isReconnect = false; }\n" +
            "          this.client = new this.wsImpl(this.url, protocols_1.GRAPHQL_SUBSCRIPTIONS);\n" +
            "          this.client.onopen = function () {\n" +
            "            _this.eventEmitter.emit(isReconnect ? 'reconnect' : 'connect');\n" +
            "            _this.reconnecting = false;\n" +
            "            _this.backoff.reset();\n" +
            "            Object.keys(_this.reconnectSubscriptions).forEach(function (key) {\n" +
            "              var _a = _this.reconnectSubscriptions[key], options = _a.options, handler = _a.handler;\n" +
            "              _this.subscribe(options, handler);\n" +
            "            });\n" +
            "            _this.unsentMessagesQueue.forEach(function (message) {\n" +
            "              _this.client.send(JSON.stringify(message));\n" +
            "            });\n" +
            "            _this.unsentMessagesQueue = [];\n" +
            "            _this.sendMessage({ type: messageTypes_1.INIT, payload: _this.connectionParams });\n" +
            "          };\n" +
            "          this.client.onclose = function () {\n" +
            "            _this.eventEmitter.emit('disconnect');\n" +
            "            _this.tryReconnect();\n" +
            "          };\n" +
            "          this.client.onerror = function () {\n" +
            "          };\n" +
            "          this.client.onmessage = function (_a) {\n" +
            "            var data = _a.data;\n" +
            "            var parsedMessage;\n" +
            "            try {\n" +
            "              parsedMessage = JSON.parse(data);\n" +
            "            }\n" +
            "            catch (e) {\n" +
            "              throw new Error(\"Message must be JSON-parseable. Got: \" + data);\n" +
            "            }\n" +
            "            var subId = parsedMessage.id;\n" +
            "            if ([messageTypes_1.KEEPALIVE, messageTypes_1.INIT_SUCCESS, messageTypes_1.INIT_FAIL].indexOf(parsedMessage.type) === -1 && !_this.subscriptions[subId]) {\n" +
            "              _this.unsubscribe(subId);\n" +
            "              return;\n" +
            "            }\n" +
            "            switch (parsedMessage.type) {\n" +
            "              case messageTypes_1.INIT_FAIL:\n" +
            "                if (_this.connectionCallback) {\n" +
            "                  _this.connectionCallback(parsedMessage.payload.error);\n" +
            "                }\n" +
            "                break;\n" +
            "              case messageTypes_1.INIT_SUCCESS:\n" +
            "                if (_this.connectionCallback) {\n" +
            "                  _this.connectionCallback();\n" +
            "                }\n" +
            "                break;\n" +
            "              case messageTypes_1.SUBSCRIPTION_SUCCESS:\n" +
            "                delete _this.waitingSubscriptions[subId];\n" +
            "                break;\n" +
            "              case messageTypes_1.SUBSCRIPTION_FAIL:\n" +
            "                _this.subscriptions[subId].handler(_this.formatErrors(parsedMessage.payload.errors), null);\n" +
            "                delete _this.subscriptions[subId];\n" +
            "                delete _this.waitingSubscriptions[subId];\n" +
            "                break;\n" +
            "              case messageTypes_1.SUBSCRIPTION_DATA:\n" +
            "                if (parsedMessage.payload.data && !parsedMessage.payload.errors) {\n" +
            "                  _this.subscriptions[subId].handler(null, parsedMessage.payload.data);\n" +
            "                }\n" +
            "                else {\n" +
            "                  _this.subscriptions[subId].handler(_this.formatErrors(parsedMessage.payload.errors), null);\n" +
            "                }\n" +
            "                break;\n" +
            "              case messageTypes_1.KEEPALIVE:\n" +
            "                break;\n" +
            "              default:\n" +
            "                throw new Error('Invalid message type!');\n" +
            "            }\n" +
            "          };\n" +
            "        };\n" +
            "        return SubscriptionClient;\n" +
            "      }());\n" +
            "      exports.SubscriptionClient = SubscriptionClient;\n" +
            "//# sourceMappingURL=client.js.map\n" +
            "      /* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(7)))\n" +
            "\n" +
            "    /***/ })\n" +
            "  /******/ ]);";

    public static String render(Map<String, Object> parameters, String result) {
        Map<String, String> variables = new HashMap<>();
        variables.put("GRAPHIQL_VERSION", GRAPHIQL_VERSION);
        variables.put("SUBSCRIPTION_TRANSPORT_WS", SUBSCRIPTION_TRANSPORT_WS_0_5_2);

        variables.put("queryString", (String)parameters.get("query"));
        variables.put("resultString", result);
        variables.put("variablesString", (String)parameters.get("variables"));
        variables.put("operationName", (String)parameters.get("operationName"));
        return Util.substituteVariables(template, variables);
    }
}

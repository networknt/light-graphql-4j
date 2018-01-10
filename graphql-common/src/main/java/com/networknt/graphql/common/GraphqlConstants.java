package com.networknt.graphql.common;

public class GraphqlConstants {

    public static class GraphqlRouterConstants {
        public static final String GRAPHQL_WS_SUBPROTOCOL = "graphql-subscriptions";

        public static final String GRAPHQL_RESPONSE_DATA_KEY = "data";
        public static final String GRAPHQL_RESPONSE_ERROR_KEY = "errors";
        public static final String GRAPHQL_RESPONSE_PAYLOAD_KEY = "payload";

        public static final String GRAPHQL_REQUEST_QUERY_KEY = "query";
        public static final String GRAPHQL_REQUEST_VARIABLES_KEY = "variables";
    }

    public static class GraphqlSubscriptionConstants {
        public static final String GRAPHQL_REQ_TYPE_KEY = "type";
        public static final String GRAPHQL_OP_ID_KEY = "id";

        public static final String GRAPHQL_INIT_REQ_TYPE = "init";
        public static final String GRAPHQL_INIT_SUCCESS_REQ_TYPE = "init_success";

        public static final String GRAPHQL_DATA_RES_TYPE = "subscription_data";
        public static final String GRAPHQL_START_REQ_TYPE = "subscription_start";
        public static final String GRAPHQL_SUCCESS_REQ_TYPE = "subscription_success";
    }

}

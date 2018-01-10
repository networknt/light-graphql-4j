package com.networknt.graphql.common;

public class GraphqlConstants {

    public static class GraphqlRouterConstants {
        public static final String GRAPHQL_WS_SUBPROTOCOL = "graphql-ws";

        public static final String GRAPHQL_RESPONSE_DATA_KEY = "data";
        public static final String GRAPHQL_RESPONSE_ERROR_KEY = "errors";
        public static final String GRAPHQL_RESPONSE_PAYLOAD_KEY = "payload";

        public static final String GRAPHQL_REQUEST_QUERY_KEY = "query";
        public static final String GRAPHQL_REQUEST_VARIABLES_KEY = "variables";
        public static final String GRAPHQL_REQUEST_OP_NAME_KEY = "operationName";
    }

    public static class GraphqlSubscriptionConstants {
        public static final String GRAPHQL_REQ_TYPE_KEY = "type";
        public static final String GRAPHQL_OP_ID_KEY = "id";

        // Taken from subscription-transport-ws@0.9.5
        public static final String GQL_CONNECTION_INIT = "connection_init";
        public static final String GQL_CONNECTION_ACK = "connection_ack";
        public static final String GQL_CONNECTION_ERROR = "connection_error";
        public static final String GQL_CONNECTION_KEEP_ALIVE = "ka";
        public static final String GQL_CONNECTION_TERMINATE = "connection_terminate";
        public static final String GQL_START = "start";
        public static final String GQL_DATA = "data";
        public static final String GQL_ERROR = "error";
        public static final String GQL_COMPLETE = "complete";
        public static final String GQL_STOP = "stop";
        public static final String SUBSCRIPTION_START = "subscription_start";
        public static final String SUBSCRIPTION_DATA = "subscription_data";
        public static final String SUBSCRIPTION_SUCCESS = "subscription_success";
        public static final String SUBSCRIPTION_FAIL = "subscription_fail";
        public static final String SUBSCRIPTION_END = "subscription_end";
        public static final String INIT = "init";
        public static final String INIT_SUCCESS = "init_success";
        public static final String INIT_FAIL = "init_fail";
        public static final String KEEP_ALIVE = "keepalive";
    }

}

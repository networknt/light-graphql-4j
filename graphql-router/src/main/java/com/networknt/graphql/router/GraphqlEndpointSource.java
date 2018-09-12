package com.networknt.graphql.router;

import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.handler.config.EndpointSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Lists standard GraphQl endpoints at /graphql and /subscriptions.
 */
public class GraphqlEndpointSource implements EndpointSource {

    private static final Logger log = LoggerFactory.getLogger(GraphqlEndpointSource.class);

    @Override
    public Iterable<Endpoint> listEndpoints() {
        String graphqlPath = GraphqlUtil.config.getPath();
        if(log.isInfoEnabled()) log.info("Generating " + graphqlPath + " from graphql.yml");
        return Arrays.asList(
            new Endpoint(graphqlPath, "GET"),
            new Endpoint(graphqlPath, "POST"),
            new Endpoint(graphqlPath, "OPTIONS")
            // The subscriptions websocket endpoint does not need to be listed
        );
    }
}

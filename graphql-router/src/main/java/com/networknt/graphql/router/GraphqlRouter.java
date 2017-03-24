package com.networknt.graphql.router;

import com.networknt.info.ServerInfoGetHandler;
import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.util.Methods;

/**
 * Created by stevehu on 2017-03-22.
 */
public class GraphQLRouter implements HandlerProvider {
    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(Methods.GET, "/graphql", new GraphQLGetHandler())
                .add(Methods.POST, "/graphql", new GraphQLPostHandler())
                .add(Methods.GET, "/server/info", new ServerInfoGetHandler())
                ;
    }

}

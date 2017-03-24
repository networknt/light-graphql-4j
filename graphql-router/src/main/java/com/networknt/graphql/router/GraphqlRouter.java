package com.networknt.graphql.router;

import com.networknt.info.ServerInfoGetHandler;
import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.util.Methods;

/**
 * Created by stevehu on 2017-03-22.
 */
public class GraphqlRouter implements HandlerProvider {
    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(Methods.GET, "/graphql", new GraphqlGetHandler())
                .add(Methods.POST, "/graphql", new GraphqlPostHandler())
                .add(Methods.GET, "/server/info", new ServerInfoGetHandler())
                ;
    }

}

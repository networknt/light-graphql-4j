package com.networknt.graphql.router;

import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.info.ServerInfoGetHandler;
import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.util.Methods;

/**
 * Router of the graphql request to map different handlers.
 *
 * @author Steve Hu
 */
public class GraphqlRouter implements HandlerProvider {

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(Methods.GET, GraphqlUtil.config.getPath(), new GraphqlGetHandler())
                .add(Methods.POST, GraphqlUtil.config.getPath(), new GraphqlPostHandler())
                .add(Methods.GET, "/server/info", new ServerInfoGetHandler())
                ;
    }
}

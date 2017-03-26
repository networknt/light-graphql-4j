package com.networknt.graphql.router;

import com.networknt.config.Config;
import com.networknt.graphql.common.GraphqlUtil;
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
                .add(Methods.GET, GraphqlUtil.config.getPath(), new GraphqlGetHandler())
                .add(Methods.POST, GraphqlUtil.config.getPath(), new GraphqlPostHandler())
                .add(Methods.GET, "/server/info", new ServerInfoGetHandler())
                ;
    }
}

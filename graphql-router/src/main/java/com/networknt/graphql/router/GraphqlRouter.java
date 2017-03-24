package com.networknt.graphql.router;

import com.networknt.config.Config;
import com.networknt.info.ServerInfoGetHandler;
import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.util.Methods;

/**
 * Created by stevehu on 2017-03-22.
 */
public class GraphqlRouter implements HandlerProvider {
    public static final String CONFIG_NAME = "graphql";
    static GraphqlConfig config = (GraphqlConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, GraphqlConfig.class);

    @Override
    public HttpHandler getHandler() {
        return Handlers.routing()
                .add(Methods.GET, config.getPath(), new GraphqlGetHandler())
                .add(Methods.POST, config.getPath(), new GraphqlPostHandler())
                .add(Methods.GET, "/server/info", new ServerInfoGetHandler())
                ;
    }
}

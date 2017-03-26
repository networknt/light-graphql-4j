package com.networknt.graphql.common;

import com.networknt.config.Config;
import io.undertow.util.AttachmentKey;

/**
 * Created by steve on 25/03/17.
 */
public class GraphqlUtil {
    public static final String CONFIG_NAME = "graphql";

    public static final AttachmentKey<Object> GRAPHQL_PARAMS = AttachmentKey.create(Object.class);

    public static GraphqlConfig config = (GraphqlConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, GraphqlConfig.class);

}

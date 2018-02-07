package com.networknt.graphql.common;

import com.networknt.config.Config;
import io.undertow.util.AttachmentKey;

/**
 * A utility class that contains some static variables and static methods shared
 * by all other modules.
 *
 * @author Steve Hu
 */
public class GraphqlUtil {
    public static final String CONFIG_NAME = "graphql";

    public static final AttachmentKey<Object> GRAPHQL_PARAMS = AttachmentKey.create(Object.class);

    public static GraphqlConfig config = (GraphqlConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, GraphqlConfig.class);
}

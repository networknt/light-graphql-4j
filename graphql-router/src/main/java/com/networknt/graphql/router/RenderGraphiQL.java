package com.networknt.graphql.router;

import com.networknt.config.Config;
import java.util.Map;

/**
 * GraphiQL implementation
 *
 * @author Steve Hu
 */
public class RenderGraphiQL {

    public static String render(Map<String, Object> parameters, String result) {
        Config config = Config.getInstance();
        return config.getStringFromFile("graphiql.html");
    }
}

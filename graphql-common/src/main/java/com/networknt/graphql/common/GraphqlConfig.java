package com.networknt.graphql.common;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema; // REQUIRED IMPORT
import com.networknt.config.schema.OutputFormat; // REQUIRED IMPORT
import com.networknt.config.schema.BooleanField; // REQUIRED IMPORT
import com.networknt.config.schema.StringField; // REQUIRED IMPORT
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Main configuration class for graphql framework that defines the path for
 * graphql endpoint and if GraphiQL is enabled or not.
 *
 * @author Steve Hu
 */
@ConfigSchema(
        configKey = "graphql",
        configName = "graphql",
        configDescription = "GraphQL configuration.",
        outputFormats = {OutputFormat.JSON_SCHEMA, OutputFormat.YAML}
)
public class GraphqlConfig {
    private static final Logger logger = LoggerFactory.getLogger(GraphqlConfig.class);

    public final static String CONFIG_NAME = "graphql";
    private static final String PATH = "path";
    private static final String SUBSCRIPTIONS_PATH = "subscriptionsPath";
    private static final String ENABLE_GRAPHIQL = "enableGraphiQL";

    private final Config config;
    private Map<String, Object> mappedConfig;


    // --- Annotated Fields ---
    @StringField(
            configFieldName = PATH,
            externalizedKeyName = PATH,
            description = "The path of GraphQL endpoint for both GET and POST.",
            defaultValue = "/graphql"
    )
    private String path;

    @StringField(
            configFieldName = SUBSCRIPTIONS_PATH,
            externalizedKeyName = SUBSCRIPTIONS_PATH,
            description = "Path to the websocket endpoint to handle subscription requests.",
            defaultValue = "/subscriptions"
    )
    private String subscriptionsPath;

    @BooleanField(
            configFieldName = ENABLE_GRAPHIQL,
            externalizedKeyName = ENABLE_GRAPHIQL,
            description = "Enable GraphiQL for development environment only. It allows testing from the Browser.",
            defaultValue = "true"
    )
    private boolean enableGraphiQL;


    // --- Constructor and Loading Logic ---

    public GraphqlConfig() {
        this(CONFIG_NAME);
    }

    private GraphqlConfig(String configName) {
        config = Config.getInstance();
        mappedConfig = config.getJsonMapConfigNoCache(configName);
        setConfigData();
    }

    public static GraphqlConfig load() {
        return new GraphqlConfig();
    }

    public static GraphqlConfig load(String configName) {
        return new GraphqlConfig(configName);
    }

    public void reload() {
        mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }


    // --- Private Config Loader ---
    private void setConfigData() {
        Object object = mappedConfig.get(PATH);
        if (object != null) path = (String)object;

        object = mappedConfig.get(SUBSCRIPTIONS_PATH);
        if (object != null) subscriptionsPath = (String)object;

        object = mappedConfig.get(ENABLE_GRAPHIQL);
        if (object != null) enableGraphiQL = Config.loadBooleanValue(ENABLE_GRAPHIQL, object);
    }


    // --- Getters and Setters (Original Methods) ---

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isEnableGraphiQL() {
        return enableGraphiQL;
    }

    public void setEnableGraphiQL(boolean enableGraphiQL) {
        this.enableGraphiQL = enableGraphiQL;
    }

    public String getSubscriptionsPath() {
        return subscriptionsPath;
    }

    public void setSubscriptionsPath(String subscriptionsPath) {
        this.subscriptionsPath = subscriptionsPath;
    }
}

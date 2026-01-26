package com.networknt.graphql.common;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema; // REQUIRED IMPORT
import com.networknt.config.schema.OutputFormat; // REQUIRED IMPORT
import com.networknt.config.schema.BooleanField; // REQUIRED IMPORT
import com.networknt.config.schema.StringField; // REQUIRED IMPORT
import com.networknt.server.ModuleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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


    private Map<String, Object> mappedConfig;
    private static final Map<String, GraphqlConfig> instances = new ConcurrentHashMap<>();


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
        mappedConfig = Config.getInstance().getJsonMapConfigNoCache(configName);
        setConfigData();
    }

    public static GraphqlConfig load() {
        return load(CONFIG_NAME);
    }

    public static GraphqlConfig load(String configName) {
        GraphqlConfig instance = instances.get(configName);
        if (instance != null) {
            return instance;
        }
        synchronized (GraphqlConfig.class) {
            instance = instances.get(configName);
            if (instance != null) {
                return instance;
            }
            instance = new GraphqlConfig(configName);
            instances.put(configName, instance);
            if (CONFIG_NAME.equals(configName)) {
                ModuleRegistry.registerModule(CONFIG_NAME, GraphqlConfig.class.getName(), Config.getNoneDecryptedInstance().getJsonMapConfigNoCache(CONFIG_NAME), null);
            }
            return instance;
        }
    }

    public static void reload() {
        reload(CONFIG_NAME);
    }

    public static void reload(String configName) {
        synchronized (GraphqlConfig.class) {
            GraphqlConfig instance = new GraphqlConfig(configName);
            instances.put(configName, instance);
            if (CONFIG_NAME.equals(configName)) {
                ModuleRegistry.registerModule(CONFIG_NAME, GraphqlConfig.class.getName(), Config.getNoneDecryptedInstance().getJsonMapConfigNoCache(CONFIG_NAME), null);
            }
        }
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

package com.networknt.graphql.validator;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema; // REQUIRED IMPORT
import com.networknt.config.schema.OutputFormat; // REQUIRED IMPORT
import com.networknt.config.schema.BooleanField; // REQUIRED IMPORT
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * GraphQL validator configuration class
 *
 * @author Steve Hu
 */
@ConfigSchema(
        configKey = "graphql-validator",
        configName = "graphql-validator",
        configDescription = "A light-graphql-4j framework specific validator configuration. It is introduced to support multiple\n" +
                "frameworks in the same server instance and it is recommended. The old validator.yml will be loaded\n" +
                "if graphql-validator.yml cannot be found for backward compatibility and it might be removed in the\n" +
                "next major release.\n",
        outputFormats = {OutputFormat.JSON_SCHEMA, OutputFormat.YAML}
)
public class ValidatorConfig {
    private static final Logger logger = LoggerFactory.getLogger(ValidatorConfig.class);

    public static final String CONFIG_NAME = "graphql-validator";
    private static final String ENABLED = "enabled";
    private static final String LOG_ERROR = "logError";

    private Map<String, Object> mappedConfig;
    private final Config config;

    // --- Annotated Fields ---
    @BooleanField(
            configFieldName = ENABLED,
            externalizedKeyName = ENABLED,
            description = "Enable request validation against the specification.",
            defaultValue = "true"
    )
    private boolean enabled;

    @BooleanField(
            configFieldName = LOG_ERROR,
            externalizedKeyName = LOG_ERROR,
            description = "Log error message if validation error occurs.",
            defaultValue = "true"
    )
    private boolean logError;


    // --- Constructor and Loading Logic ---

    private ValidatorConfig(String configName) {
        config = Config.getInstance();
        mappedConfig = config.getJsonMapConfigNoCache(configName);
        setConfigData();
    }
    private ValidatorConfig() {
        this(CONFIG_NAME);
    }

    public static ValidatorConfig load(String configName) {
        return new ValidatorConfig(configName);
    }

    public static ValidatorConfig load() {
        return new ValidatorConfig();
    }

    public void reload() {
        mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    public void reload(String configName) {
        mappedConfig = config.getJsonMapConfigNoCache(configName);
        setConfigData();
    }

    // --- Getters and Setters (Original Methods) ---

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLogError() { return logError; }

    public void setLogError(boolean logError) { this.logError = logError; }

    public Map<String, Object> getMappedConfig() {
        return mappedConfig;
    }

    private void setConfigData() {
        if(getMappedConfig() != null) {
            Object object = getMappedConfig().get(ENABLED);
            if(object != null) enabled = Config.loadBooleanValue(ENABLED, object);
            object = getMappedConfig().get(LOG_ERROR);
            if(object != null) logError = Config.loadBooleanValue(LOG_ERROR, object);
        }
    }

}

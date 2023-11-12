package com.networknt.graphql.validator;

import com.networknt.config.Config;

import java.util.Map;

/**
 * GraphQL validator configuration class
 *
 * @author Steve Hu
 */
public class ValidatorConfig {
    public static final String CONFIG_NAME = "graphql-validator";
    public static final String ENABLED = "enabled";
    public static final String LOG_ERROR = "logError";

    private boolean enabled;
    private boolean logError;

    private Map<String, Object> mappedConfig;
    private final Config config;

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

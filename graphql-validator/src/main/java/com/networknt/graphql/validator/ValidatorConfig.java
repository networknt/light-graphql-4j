package com.networknt.graphql.validator;

/**
 * Created by steve on 24/03/17.
 */
public class ValidatorConfig {
    boolean enabled;
    boolean enableResponseValidator;

    public ValidatorConfig() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnableResponseValidator() {
        return enableResponseValidator;
    }

    public void setEnableResponseValidator(boolean enableResponseValidator) {
        this.enableResponseValidator = enableResponseValidator;
    }
}

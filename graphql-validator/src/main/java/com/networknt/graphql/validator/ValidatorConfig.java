package com.networknt.graphql.validator;

/**
 * GraphQL validator configuration class
 *
 * @author Steve Hu
 */
public class ValidatorConfig {
    private boolean enabled;

    public ValidatorConfig() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

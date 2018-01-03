package com.networknt.graphql.common;

/**
 * Main configuration class for graphql framework that defines the path for
 * graphql endpoint and if GraphiQL is enabled or not.
 *
 * @author Steve Hu
 */
public class GraphqlConfig {
    private String path;
    private boolean enableGraphiQL;

    public GraphqlConfig() {
    }

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
}

package com.networknt.graphql.common;

/**
 * Created by steve on 24/03/17.
 */
public class GraphqlConfig {
    String path;
    boolean enableGraphiQL;

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

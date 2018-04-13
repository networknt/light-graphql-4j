package com.networknt.graphql.common;


import graphql.execution.instrumentation.Instrumentation;

/**
 * @author Nicholas Azar
 * Created on April 9, 2018
 */
public interface InstrumentationProvider {
    Instrumentation getGraphqlInstrumentation();
    Instrumentation getGraphqlSubscriptionInstrumentation();
}

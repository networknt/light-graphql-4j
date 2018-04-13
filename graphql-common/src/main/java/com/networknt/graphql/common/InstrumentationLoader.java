package com.networknt.graphql.common;

import com.networknt.service.SingletonServiceFactory;
import graphql.execution.instrumentation.Instrumentation;


/**
 * @author Nicholas Azar
 * Created on April 09, 2018
 */
public class InstrumentationLoader {

    public static Instrumentation graphqlInstrumentation;
    public static Instrumentation graphqlSubscriptionInstrumentation;

    static {
        InstrumentationProvider instrumentationProvider = SingletonServiceFactory.getBean(InstrumentationProvider.class);
        if (instrumentationProvider != null) {
            graphqlInstrumentation = instrumentationProvider.getGraphqlInstrumentation();
            graphqlSubscriptionInstrumentation = instrumentationProvider.getGraphqlSubscriptionInstrumentation();
        }
    }
}

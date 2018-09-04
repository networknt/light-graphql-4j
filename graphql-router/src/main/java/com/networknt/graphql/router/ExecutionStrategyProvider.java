package com.networknt.graphql.router;

import graphql.execution.ExecutionStrategy;

/**
 * ExecutionStrategyProvider interface that is used to inject execution strategy
 * implementations into the framework. The service module will pass these on to
 * the graphql-java framework.
 *
 * @author Logi Ragnarsson
 */
public interface ExecutionStrategyProvider {

    /**
     * Return an execution strategy to use for queries or null to use the default.
     */
    ExecutionStrategy getQueryExecutionStrategy();

    /**
     * Return an execution strategy to use for mutations or null to use the default.
     */
    ExecutionStrategy getMutationExecutionStrategy();

    /**
     * Return an execution strategy to use for subscriptions or null to use the default.
     */
    ExecutionStrategy getSubscriptionExecutionStrategy();
}

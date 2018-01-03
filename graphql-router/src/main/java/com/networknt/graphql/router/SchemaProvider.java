package com.networknt.graphql.router;

import graphql.schema.GraphQLSchema;

/**
 * SchemaProvider interface that is used to inject schema implementation to the
 * framework. The service module is responsible to inject implementation for each
 * application.
 *
 * @author Steve Hu
 */
public interface SchemaProvider {
    GraphQLSchema getSchema();
}

package com.networknt.graphql.router;

import graphql.schema.GraphQLSchema;

/**
 * Created by steve on 25/03/17.
 */
public interface SchemaProvider {
    GraphQLSchema getSchema();
}

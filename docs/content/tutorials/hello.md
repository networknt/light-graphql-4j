---
date: 2017-03-27T17:05:10-04:00
title: Hello World Tutorial
---

This is the first example application and the [README.md](https://github.com/networknt/light-example-4j/tree/master/graphql/hello) 
shows how to use it.

Unlike [light-rest-4j](https://github.com/networknt/light-rest-4j) has a [swagger-codegen](https://github.com/networknt/swagger-codegen)
to generate the project, we have to construct the project based on the [petstore](https://github.com/networknt/light-java-example/tree/master/petstore)
example and modify some files for GraphQL. 

File changed:

### pom.xml

The artifact and name of the project are changed. Also, add graphql-java as dependency.

```
        <version.light-java-graphql>1.2.6</version.light-java-graphql>
        <version.graphql>2.3.0</version.graphql>

        <dependency>
            <groupId>com.networknt</groupId>
            <artifactId>graphql-common</artifactId>
            <version>${version.light-java-graphql}</version>
        </dependency>
        <dependency>
            <groupId>com.networknt</groupId>
            <artifactId>graphql-router</artifactId>
            <version>${version.light-java-graphql}</version>
        </dependency>
        <dependency>
            <groupId>com.networknt</groupId>
            <artifactId>graphql-security</artifactId>
            <version>${version.light-java-graphql}</version>
        </dependency>
        <dependency>
            <groupId>com.networknt</groupId>
            <artifactId>graphql-validator</artifactId>
            <version>${version.light-java-graphql}</version>
        </dependency>

        <dependency>
            <groupId>com.graphql-java</groupId>
            <artifactId>graphql-java</artifactId>
            <version>${version.graphql}</version>
        </dependency>

```

Remove all the dependencies with swagger-xxx.

### SchemaProvider

Create a file /src/main/resources/META-INF/services/com.networknt.graphql.router.SchemaProvider

```
com.networknt.schema.HelloSchema
```

This is the only file that you need to change if you start another project with another schema. 

### MiddlewareHandler

Update /src/main/resources/META-INF/services/com.networknt.handler.MiddlewareHandler

```
# This file is generated and should not be changed unless you want to plug in more handlers into the handler chain
# for cross cutting concerns. In most cases, you should replace some of the default handlers with your own implementation
# Please note: the sequence of these handlers are very important.

#Validator Validate request based on swagger specification (depending on Swagger and Body)
com.networknt.graphql.validator.ValidatorHandler
#Sanitizer Encode cross site scripting
com.networknt.sanitizer.SanitizerHandler
#SimpleAudit Log important info about the request into audit log
com.networknt.audit.AuditHandler
#Security JWT token verification and scope verification for GraphQL
com.networknt.graphql.security.JwtVerifyHandler
#Correlation Create correlationId if it doesn't exist in the request header and put it into the request header
com.networknt.correlation.CorrelationHandler
#Traceability Put traceabilityId into response header from request header if it exists
com.networknt.traceability.TraceabilityHandler
#Metrics In order to calculate response time accurately, this needs to be the second.
com.networknt.metrics.MetricsHandler
#Exception Global exception handler that needs to be called first.
com.networknt.exception.ExceptionHandler

```

As you can see, swagger related handlers are removed and replaced with graphql handlers.

### HandlerProvider

Update /src/main/resources/META-INF/services/com.networknt.server.HandlerProvider

```
com.networknt.graphql.router.GraphqlRouter
```

### Schema

Create HelloSchema.java in src/main/java/com/networknt/schema/

```
package com.networknt.schema;

import com.networknt.graphql.router.SchemaProvider;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Created by steve on 25/03/17.
 */
public class HelloSchema implements SchemaProvider {
    @Override
    public GraphQLSchema getSchema() {
        GraphQLObjectType queryType = newObject()
                .name("helloWorldQuery")
                .field(newFieldDefinition()
                        .type(GraphQLString)
                        .name("hello")
                        .staticValue("world"))
                .build();

        return GraphQLSchema.newSchema()
                .query(queryType)
                .build();
    }
}

```

### Start Server and Test

See [README.md](https://github.com/networknt/light-java-example/tree/master/graphql/hello)


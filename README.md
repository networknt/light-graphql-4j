# light-java-graphql
GraphQL implementation based on light-java

## Components

### graphql-common
This module controls the configuration for GraphQL service and share some static variables
with other modules to make the dependencies much simpler. 

### graphql-router
This module provides RouteHandler and SchemaProvider interfaces and implement both GET and
POST handlers for GraphQL. 

## Middleware Handlers:

### graphql-security
This is the handler that should be put before graphql-validator. There is no need to
do any validation if JWT token does not exist in the request header.

### graphql-validator
Basic request validation for the graphql path and methods. It is the first line of
validation right after graphql-security and it doesn't have any knowledge about the
graphql query parameter and body.

## Examples

### Hello

To start the server:

```
git clone git@github.com:networknt/light-java-example.git
cd light-java-example/graphql/hello
mvn clean install exec:exec
```

To test the server: 

```
curl -H 'Content-Type:application/json' -XPOST http://localhost:8080/graphql -d '{"query":"{ hello }"}'
```

and the result is:

```
{"hello":"world"}
```

To access GraphiQL, you need to put the following url into your browser's address.

```
http://localhost:8080/graphql
```

On the left panel, enter the following to test the result.
 
```
{ hello }
```


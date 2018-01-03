GraphQL framework based on light-4j

[Developer Chat](https://gitter.im/networknt/light-graphql-4j) |
[Documentation](https://doc.networknt.com/style/light-graphql-4j/) |
[Contribution Guide](CONTRIBUTING.md) |

[![Build Status](https://travis-ci.org/networknt/light-graphql-4j.svg?branch=master)](https://travis-ci.org/networknt/light-graphql-4j)

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

### [Hello](https://github.com/networknt/light-example-4j/tree/master/graphql/hello)

### [Mutation](https://github.com/networknt/light-example-4j/tree/master/graphql/mutation)

### [RelayTodo](https://github.com/networknt/light-example-4j/tree/master/graphql/relaytodo)


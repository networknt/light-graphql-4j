---
date: 2017-03-26T21:03:44-04:00
title: Light GraphQL 4J
type: index
---

### GraphQL Specific Components

* [graphql-common](https://networknt.github.io/light-graphql-4j/components/graphql-common/) 
contains common utilities and static variables that are shared by other components.

* [graphql-router](https://networknt.github.io/light-graphql-4j/components/graphql-router/)
is responsible for handling GraphQL and GraphiQL requests and hooks schema provider.

### GraphQL Specific Middleware Handlers

* [graphql-security](https://networknt.github.io/light-graphql-4j/components/graphql-security/)
verifies JWT token in request header and verifies scopes if it is enabled.

* [graphql-validator](https://networknt.github.io/light-graphql-4j/components/graphql-validator/)
validates the path and methods of the request. Other schema validation will be handled by the
GraphQL component. 

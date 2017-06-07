---
date: 2017-03-27T14:25:32-04:00
title: GraphQL Validator
---

Basic request validation for the graphql path and methods. It is the first line of
validation right after graphql-security and it doesn't have any knowledge about the
graphql query parameter and body. Other schema based validation will be done at
GraphQL level. 

It shares the same configuration file with swagger-validator and here is an example.

```
# Validate request/response for GraphQL request
---
enabled: true
enableResponseValidator: false

```


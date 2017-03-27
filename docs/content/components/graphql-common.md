---
date: 2017-03-27T14:25:02-04:00
title: GraphQL Common
---

This module controls the configuration for GraphQL service and share some static variables
with other modules to make the dependencies much simpler. 

Here is an example of graphql.yml

```
# GraphQL configuration
---
# The path of GraphQL endpoint for both GET and POST
path: /graphql

# Enable GraphiQL for development environment only. It will allow you to test from your Browser.
enableGraphiQL: true

```

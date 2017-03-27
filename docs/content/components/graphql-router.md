---
date: 2017-03-27T14:25:17-04:00
title: GraphQL Router
---

This module provides RouteHandler and SchemaProvider interfaces and implement both GET and
POST handlers for GraphQL. 

The router is a HandlerProvider and it needs to be put into file
/src/main/resources/META-INF/services/com.networknt.server.HandlerProvider
in your GraphQL API/service. 

The [link](https://github.com/networknt/light-java-example/blob/master/graphql/mutation/src/main/resources/META-INF/services/com.networknt.server.HandlerProvider) 
is an example.

The user developed schema needs to be hooked to the GraphqlPostHandler in this module through
SchemaProvider interface. The SPI config file should be located at
/src/main/resources/META-INF/services/com.networknt.graphql.router.SchemaProvider

The [link](https://github.com/networknt/light-java-example/blob/master/graphql/mutation/src/main/resources/META-INF/services/com.networknt.graphql.router.SchemaProvider) 
is an example.


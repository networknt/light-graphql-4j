---
date: 2017-03-26T21:03:44-04:00
title: Light Java GraphQL
---

## Introduction

[Light Java](https://github.com/networknt/light-java) is a framework built on top 
of Undertow core HTTP server that addresses all the cross-cutting concerns for
microservices in the request and response chain. [Light Java REST](https://github.com/networknt/light-java-rest)
is built on top of Light Java and focuses on RESTful services with OpenAPI 
specification loaded at runtime to drive security and validation. [Light Java GraphQL](https://github.com/networknt/light-java-graphql) 
is based on Light Java and is our solution for [GraphQL](http://graphql.org/) services.
  
With Light Java GraphQL, developers can only focus on Schema development and the rest
of the functionalities are provided by the framework. These includes:
 
### Common Components:

* [server](https://networknt.github.io/light-java/other/server/) is
a framework on top of Undertow http core that support plugins to perform 
different middleware handlers. It is light-weight, fast and supports HTTP/2.

* [config](https://networknt.github.io/light-java/other/config/) is a module that 
supports externalized yml/yaml/json configuration for standalone applications and 
docker containers managed by Kubernetes. Config files are managed by 
[light-config-server](https://github.com/networknt/light-config-server) and mapped
to Kubernetes ConfigMap and Secrets.

* [utility](https://networknt.github.io/light-java/other/utility/) contains utility 
classes and static variables that are shared between modules.

* [client](https://networknt.github.io/light-java/other/client/) is a wrapper of 
apache HttpClient and HttpAsyncClient. It supports automatically cache and 
renew client credentials JWT tokens and manages connection pooling. It is also
responsible for passing correlationId and traceabilityId to the next service.

* [info](https://networknt.github.io/light-java/other/info/) is a handler that 
injects an endpoint /server/info to all server instances so that light-portal
can pull the info to certify all the enabled components and their configuration
at runtime. It also helps while debugging issues on the server.

* [mask](https://networknt.github.io/light-java/other/mask/) is used to mask 
sensitive info before logging. 

* [status](https://networknt.github.io/light-java/other/status/) is used to model 
error http response and assist production monitoring with unique error code.

* [security](https://networknt.github.io/light-java/other/status/) is used by 
swagger-security and graphql-security currently but these utilities and helpers can 
be used by other security handlers for Role-Based or Attribute-Based Authorization.

* [balance](https://networknt.github.io/light-java/other/balance/) is a load balance 
module that is used by cluster module with service discovery module. It will be called
from client module and be part of client side discovery. 

* [cluster](https://networknt.github.io/light-java/other/cluster/) ia a module caches 
discovered services and calling load balance module for load balancing. Part of client
side discovery.

* [consul](https://networknt.github.io/light-java/other/consul/) is a module manages 
communication with Consul server for registry and discovery.

* [handler](https://networknt.github.io/light-java/other/handler/) is a module defines 
middleware handler interface for all middleware components.

* [Health](https://networknt.github.io/light-java/other/health/) is a health check module 
that can be called by API portal to determine if the service is healthy. It supports
cascade health check for databases or message queues.

* [registry](https://networknt.github.io/light-java/other/registry/) ia an interface 
definition and generic direct registry implementation for service registry and discovery.

* [service](https://networknt.github.io/light-java/other/service/) is a light weight 
dependency injection framework for testing and startup hooks.
 
* [switcher](https://networknt.github.io/light-java/other/switcher/) is a switcher that 
turns things on and off based on certain conditions.

* [zookeeper](https://networknt.github.io/light-java/other/zookeeper/) is a module manages 
communication with ZooKeeper server for service registry and discovery.

### Middleware Handlers

* [audit](https://networknt.github.io/light-java/middleware/audit/) logs most important info 
about request and response into audit.log in JSON format with config file that controls which
fieds to be logged.

* [body](https://networknt.github.io/light-java/middleware/body/) is a body parser middleware 
that is responsible for parsing the content of the request based on Content-Type in the 
request header. 

* [exception](https://networknt.github.io/light-java/middleware/exception/) is a generic 
exception handler that handles runtime exception, ApiException and other checked exception 
if they are not handled properly in the handler chain.

* [metrics](https://networknt.github.io/light-java/middleware/metrics/) is a module that collects
API runtime info based on clientId and API name. The metrics info is sent to InfluxDB and 
accessible from Grafana Dashboard.

* [sanitizer](https://networknt.github.io/light-java/middleware/sanitizer/) is a 
middleware that address cross site scripting concerns. It encodes header and body based on 
configuration.

* [correlation](https://networknt.github.io/light-java/middleware/correlation/) generates
a UUID in the first API/service and pass it to all other APIs/services in the call tree for
tracking purpose.

* [traceability](https://networknt.github.io/light-java/middleware/traceability/) is an
id passed in from client and will be unique with an application context. The id will be passed
into the backend and return to the consumer for transaction tracing. 

* [cors](https://networknt.github.io/light-java/middleware/cors/) is a module handles 
Cross-Origin Resource Sharing (CORS) pre-flight OPTIONS to support single page applications 
from another domain to access APIs/services.
 
* [dump](https://networknt.github.io/light-java/middleware/dump/) is a full request/response 
log handler to dump everything regarding to request and response into log file for developers. 

* [limit](https://networknt.github.io/light-java/middleware/limit/) is a rate limiting handler 
to limit number of concurrent requests on the server. Once the limit is reached, subsequent 
requests will be queued for later execution. The size of the queue is configurable. 

### GraphQL Specific Components

* [graphql-common](https://networknt.github.io/light-java-graphql/components/graphql-common/) 
contains common utilities and static variables that are shared by other components.

* [graphql-router](https://networknt.github.io/light-java-graphql/components/graphql-router/)
is responsible for handling GraphQL and GraphiQL requests and hooks schema provider.

### GraphQL Specific Middleware Handlers

* [graphql-security](https://networknt.github.io/light-java-graphql/components/graphql-security/)
verifies JWT token in request header and verifies scopes if it is enabled.

* [graphql-validator](https://networknt.github.io/light-java-graphql/components/graphql-validator/)
validates the path and methods of the request. Other schema validation will be handled by the
GraphQL componnent. 

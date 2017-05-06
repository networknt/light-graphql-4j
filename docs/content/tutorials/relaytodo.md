---
date: 2017-03-27T17:05:38-04:00
title: Relay Todo
---

Relay Todo is built on top of mutation example with the following changes.

### pom.xml

Only artifact and name are changed.

### SchemaProvider

```
com.networknt.schema.TodoSchema
```

### TodoSchema

There are several files to support Todo schema and they are located at 

https://github.com/networknt/light-example-4j/tree/master/graphql/relaytodo/src/main/java/com/networknt/schema

### Relay React App

The client app is located at 

https://github.com/networknt/light-example-4j/tree/master/graphql/relaytodo/app

### Start servers and test

See [README.md](https://github.com/networknt/light-example-4j/tree/master/graphql/relaytodo)
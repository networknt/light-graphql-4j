GraphQL framework based on light-4j

[Stack Overflow](https://stackoverflow.com/questions/tagged/light-4j) |
[Google Group](https://groups.google.com/forum/#!forum/light-4j) |
[Gitter Chat](https://gitter.im/networknt/light-graphql-4j) |
[Subreddit](https://www.reddit.com/r/lightapi/) |
[Youtube Channel](https://www.youtube.com/channel/UCHCRMWJVXw8iB7zKxF55Byw) |
[Documentation](https://doc.networknt.com/style/light-graphql-4j/) |
[Contribution Guide](https://doc.networknt.com/contribute/) |

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

## Tutorial

### [Hello World](https://doc.networknt.com/tutorial/graphql/helloworld/)

This is a very simple Hello World query to show you how to get GraphQL up and running with
light-codegen without using GraphQL IDL.

### [Star Wars](https://doc.networknt.com/tutorial/graphql/starwars/)

This is a similar example as Hello World with IDL to trigger the generation. It is utilize the
star wars GraphQL IDL downloaded from the Internet.

### [Mutation](https://doc.networknt.com/tutorial/graphql/mutation/)

This example shows you how to create a full blown GraphQL service with both query and mutation.

### [Mutation IDL](https://doc.networknt.com/tutorial/graphql/mutation-idl/)

This is the same example like the mutation but is generated from a schema.

### [Relay Todo](https://doc.networknt.com/tutorial/graphql/relay-todo/)

This is to show you how to build a GraphQL service that is working with Relayjs.


### Jackson 3 development migration

This development line uses Jackson 3.2.3 and the migrated light-4j/http-client
snapshots. Jackson core/databind APIs use `tools.jackson`; annotations remain
under `com.fasterxml.jackson.annotation`. JSON policies are inherited from the
framework Config mapper. Existing project versions are unchanged during
implementation; coordinate the next-major release before publication.

GraphQL JSON serialization error handlers catch unchecked JacksonException.
WebSocket message parsing and connection acknowledgements preserve Undertow's
IOException callback boundary, so malformed JSON still reaches its onError
handling instead of escaping as an unchecked parser exception. Connection
acknowledgements retain the existing JSON shape. The new callback regressions
exercise malformed input and the actual acknowledgement sender.

The source-build workflow resolves yaml-rule #36 and light-4j #2815 to their
latest heads while open, then follows their base branches after merge. Other
prerequisites use master. Each run logs the exact source commits tested and uses
a fresh Maven repository. Rerun this check after prerequisite changes and before
merging: an earlier green run qualifies only its recorded commits. Manual
workflow dispatch is available. Source-build success does not qualify published
artifact resolution.

Prerequisites are resolved to full commit SHAs before checkout. To replay a
failed dependency combination, manually dispatch the source-build workflow with
its uploaded `source-refs.json` contents as the `source_refs` input. Omitted
repositories resolve their current source head; supplied values must be full
commit SHAs. Each green result qualifies only the recorded combination.

GitHub manual dispatch requires this workflow to exist on the default branch.
Before merge, use the recorded commits to reproduce with local source checkouts.

# light-java-graphql
GraphQL implementation based on light-java

Middleware Handlers:

### graphql-security
This is the handler that should be put before graphql-validator. There is no need to
do any validation if JWT token does not exist in the request header.

### graphql-validator
Basic request validation for the graphql path and methods. It is the first line of
validation right after graphql-security and it doesn't have any knowledge about the
graphql query parameter and body.

### graphql-parser
This middleware handler parses the request and put the params into exchange attachment
for other subsequent handlers to consume. It also returns errors if something is wrong
with the parsing or parsed result. This should be put after graphql-validator which does
only basic validation for the request.
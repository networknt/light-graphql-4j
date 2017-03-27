---
date: 2017-03-27T14:25:26-04:00
title: GraphQL Security
---

This module is very similar with swagger-security but as there is swagger specification
we cannot verify scopes against specification. GraphQL recommend authorization outside
of schema so that we can only verify query scope and mutation scope for read and write
access. 

This is the handler that should be put before graphql-validator. There is no need to
do any validation if JWT token does not exist in the request header.

The module share the same security.yml and here is an example.

```
# Security configuration in light framework.
---
# Enable JWT verification flag.
enableVerifyJwt: true

# Enable JWT scope verification. Only valid when enableVerifyJwt is true.
enableVerifyScope: true

# User for test only. should be always be false on official environment.
enableMockJwt: false

# JWT signature public certificates. kid and certificate path mappings.
jwt:
  certificate:
    '100': oauth/primary.crt
    '101': oauth/secondary.crt
  clockSkewInSeconds: 60

# Enable or disable JWT token logging
logJwtToken: true

# Enable or disable client_id, user_id and scope logging.
logClientUserScope: false

```

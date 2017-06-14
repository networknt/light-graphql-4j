/*
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.graphql.security;

import com.networknt.audit.AuditHandler;
import com.networknt.config.Config;
import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.security.JwtHelper;
import com.networknt.status.Status;
import com.networknt.utility.Constants;
import com.networknt.exception.ExpiredTokenException;
import com.networknt.utility.ModuleRegistry;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the JWT token verifier for GraphQL. Given there is no OpenAPI spec available for
 * scopes, we have to verify the scope just based on query and mutation which is read and write.
 *
 * Regarding to the authorization, GraphQL spec doesn't have anything built-in and it is
 * recommended to handle at the business logic layer. As we are trying to address the cross-cutting
 * concerns at middleware level within the framework, we don't want to inject anything extra into
 * the schema for authorization.
 *
 * Created by steve on 01/09/16.
 *
 */
public class JwtVerifyHandler implements MiddlewareHandler {
    static final Logger logger = LoggerFactory.getLogger(JwtVerifyHandler.class);

    static final String ENABLE_VERIFY_SCOPE = "enableVerifyScope";

    static final String STATUS_INVALID_AUTH_TOKEN = "ERR10000";
    static final String STATUS_AUTH_TOKEN_EXPIRED = "ERR10001";
    static final String STATUS_MISSING_AUTH_TOKEN = "ERR10002";
    static final String STATUS_INVALID_SCOPE_TOKEN = "ERR10003";
    static final String STATUS_SCOPE_TOKEN_EXPIRED = "ERR10004";
    static final String STATUS_AUTH_TOKEN_SCOPE_MISMATCH = "ERR10005";
    static final String STATUS_SCOPE_TOKEN_SCOPE_MISMATCH = "ERR10006";

    static final Map<String, Object> config = Config.getInstance().getJsonMapConfig(JwtHelper.SECURITY_CONFIG);

    private volatile HttpHandler next;

    public JwtVerifyHandler() {}

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        HeaderMap headerMap = exchange.getRequestHeaders();
        String authorization = headerMap.getFirst(Headers.AUTHORIZATION);
        String jwt = JwtHelper.getJwtFromAuthorization(authorization);
        if(jwt != null) {
            try {
                JwtClaims claims = JwtHelper.verifyJwt(jwt);
                Map<String, Object> auditInfo = new HashMap<>();
                auditInfo.put(Constants.ENDPOINT, GraphqlUtil.config.getPath());
                auditInfo.put(Constants.CLIENT_ID, claims.getStringClaimValue(Constants.CLIENT_ID));
                auditInfo.put(Constants.USER_ID, claims.getStringClaimValue(Constants.USER_ID));
                exchange.putAttachment(AuditHandler.AUDIT_INFO, auditInfo);
                if(config != null && (Boolean)config.get(ENABLE_VERIFY_SCOPE)) {
                    // need a way to figure out this is query or mutation, is it possible to have multiple queries
                    // and mutations? If yes, then each one will have a scope with operation_name.r or operation_name.w


                    // is there a scope token
                    String scopeHeader = headerMap.getFirst(Constants.SCOPE_TOKEN);
                    String scopeJwt = JwtHelper.getJwtFromAuthorization(scopeHeader);
                    List<String> secondaryScopes = null;
                    if(scopeJwt != null) {
                        try {
                            JwtClaims scopeClaims = JwtHelper.verifyJwt(scopeJwt);
                            secondaryScopes = scopeClaims.getStringListClaimValue("scope");
                            auditInfo.put(Constants.SCOPE_CLIENT_ID, scopeClaims.getStringClaimValue(Constants.CLIENT_ID));
                        } catch (InvalidJwtException | MalformedClaimException e) {
                            logger.error("InvalidJwtException", e);
                            Status status = new Status(STATUS_INVALID_SCOPE_TOKEN);
                            exchange.setStatusCode(status.getStatusCode());
                            exchange.getResponseSender().send(status.toString());
                            return;
                        } catch (ExpiredTokenException e) {
                            Status status = new Status(STATUS_SCOPE_TOKEN_EXPIRED);
                            exchange.setStatusCode(status.getStatusCode());
                            exchange.getResponseSender().send(status.toString());
                            return;
                        }
                    }

                    // find out which operation is accessed and what is the scope based one the convention.
                    List<String> specScopes = null;

                    // validate scope
                    if (scopeHeader != null) {
                        if (secondaryScopes == null || !matchedScopes(secondaryScopes, specScopes)) {
                            if(logger.isDebugEnabled()) {
                                logger.debug("Scopes " + secondaryScopes  + " and specificatio token " +
                                        specScopes + " are not matched in scope token");
                            }
                            Status status = new Status(STATUS_SCOPE_TOKEN_SCOPE_MISMATCH, secondaryScopes, specScopes);
                            exchange.setStatusCode(status.getStatusCode());
                            exchange.getResponseSender().send(status.toString());
                            return;
                        }
                    } else {
                        // no scope token, verify scope from auth token.
                        List<String> primaryScopes;
                        try {
                            primaryScopes = claims.getStringListClaimValue("scope");
                        } catch (MalformedClaimException e) {
                            logger.error("MalformedClaimException", e);
                            Status status = new Status(STATUS_INVALID_AUTH_TOKEN);
                            exchange.setStatusCode(status.getStatusCode());
                            exchange.getResponseSender().send(status.toString());
                            return;
                        }
                        if (!matchedScopes(primaryScopes, specScopes)) {
                            if(logger.isDebugEnabled()) {
                                logger.debug("Authorization jwt token scope " + primaryScopes +
                                        " is not matched with " + specScopes);
                            }
                            Status status = new Status(STATUS_AUTH_TOKEN_SCOPE_MISMATCH, primaryScopes, specScopes);
                            exchange.setStatusCode(status.getStatusCode());
                            exchange.getResponseSender().send(status.toString());
                            return;
                        }
                    }
                }
                next.handleRequest(exchange);
            } catch (InvalidJwtException e) {
                // only log it and unauthorized is returned.
                logger.error("Exception: ", e);
                Status status = new Status(STATUS_INVALID_AUTH_TOKEN);
                exchange.setStatusCode(status.getStatusCode());
                exchange.getResponseSender().send(status.toString());
            } catch (ExpiredTokenException e) {
                Status status = new Status(STATUS_AUTH_TOKEN_EXPIRED);
                exchange.setStatusCode(status.getStatusCode());
                exchange.getResponseSender().send(status.toString());
            }
        } else {
            Status status = new Status(STATUS_MISSING_AUTH_TOKEN);
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseSender().send(status.toString());
        }
    }

    protected boolean matchedScopes(List<String> jwtScopes, List<String> specScopes) {
        boolean matched = false;
        if(specScopes != null && specScopes.size() > 0) {
            if(jwtScopes != null && jwtScopes.size() > 0) {
                for(String scope: specScopes) {
                    if(jwtScopes.contains(scope)) {
                        matched = true;
                        break;
                    }
                }
            }
        } else {
            matched = true;
        }
        return matched;
    }

    @Override
    public HttpHandler getNext() {
        return next;
    }

    @Override
    public MiddlewareHandler setNext(final HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }

    @Override
    public boolean isEnabled() {
        Object object = config.get(JwtHelper.ENABLE_VERIFY_JWT);
        return object != null && (Boolean) object;
    }

    @Override
    public void register() {
        ModuleRegistry.registerModule(JwtVerifyHandler.class.getName(), config, null);
    }

}

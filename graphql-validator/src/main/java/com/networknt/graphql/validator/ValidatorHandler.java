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

package com.networknt.graphql.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.config.Config;
import com.networknt.graphql.common.GraphqlUtil;
import com.networknt.handler.Handler;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.status.Status;
import com.networknt.server.ModuleRegistry;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a validator middleware handler for GraphQL. It validate the following:
 *
 * 1. The path is /graphql
 * 2. Method must be get, post, or options (for introspection)
 * 3. The query parameter is a valid GraphQL query
 * 4. The body is a valid GraphQL json body
 *
 * @author Steve Hu
 *
 */
public class ValidatorHandler implements MiddlewareHandler {

    static final String STATUS_GRAPHQL_INVALID_PATH = "ERR11500";
    static final String STATUS_GRAPHQL_INVALID_METHOD = "ERR11501";

    static final Logger logger = LoggerFactory.getLogger(ValidatorHandler.class);

    private volatile HttpHandler next;

    public ValidatorHandler() {
        if(logger.isDebugEnabled()) logger.debug("ValidatorHandler is constructed");
        ValidatorConfig.load();
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        ValidatorConfig config = ValidatorConfig.load();
        String path = exchange.getRequestPath();
        if(!path.equals(GraphqlUtil.config.getPath()) && !path.equals(GraphqlUtil.config.getSubscriptionsPath())) {
            // invalid GraphQL path
            setExchangeStatus(exchange, STATUS_GRAPHQL_INVALID_PATH, path, GraphqlUtil.config.getPath());
            return;
        }
        // verify the method is get or post.
        HttpString method = exchange.getRequestMethod();
        if(Methods.GET.equals(method)) {
            // validate query parameter exists
            Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
            final Map<String, Object> requestParameters = new HashMap<>();
            queryParameters.forEach((k, v) -> requestParameters.put(k, v.getFirst()));
            exchange.putAttachment(GraphqlUtil.GRAPHQL_PARAMS, requestParameters);
            Handler.next(exchange, next);
        } else if(Methods.POST.equals(method) || Methods.OPTIONS.equals(method)) {
            exchange.getRequestReceiver().receiveFullString((exchange1, s) -> {
                try {
                    logger.debug("s = " + s);
                    if (s != null && s.length() > 0) {
                        Map<String, Object> requestParameters = Config.getInstance().getMapper().readValue(s,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        logger.debug("requestParameters = {}", requestParameters);
                        exchange1.putAttachment(GraphqlUtil.GRAPHQL_PARAMS, requestParameters);
                    }
                    Handler.next(exchange1, next);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else {
            // invalid GraphQL method
            Status status = new Status(STATUS_GRAPHQL_INVALID_METHOD, method);
            logger.error("ValidationError:{}", status.toString());
            exchange.setStatusCode(status.getStatusCode());
            exchange.getResponseHeaders().put(Headers.ALLOW, "GET, POST, OPTIONS");
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(status.toString());
        }
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
        return ValidatorConfig.load().isEnabled();
    }
}

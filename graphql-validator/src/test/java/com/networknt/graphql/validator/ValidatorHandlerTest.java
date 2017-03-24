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

import com.networknt.config.Config;
import com.networknt.status.Status;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by steve on 01/09/16.
 */
public class ValidatorHandlerTest {
    static final Logger logger = LoggerFactory.getLogger(ValidatorHandlerTest.class);

    static Undertow server = null;

    @BeforeClass
    public static void setUp() {
        if(server == null) {
            logger.info("starting server");
            HttpHandler handler = getTestHandler();
            ValidatorHandler validatorHandler = new ValidatorHandler();
            validatorHandler.setNext(handler);
            server = Undertow.builder()
                    .addHttpListener(8080, "localhost")
                    .setHandler(validatorHandler)
                    .build();
            server.start();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if(server != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            server.stop();
            logger.info("The server is stopped.");
        }
    }

    static RoutingHandler getTestHandler() {
        return Handlers.routing()
                .add(Methods.GET, "/graphql", exchange -> exchange.getResponseSender().send("get"))
                .add(Methods.POST, "/graphql", exchange -> exchange.getResponseSender().send("post"));
    }

    @Test
    public void testInvalidGetPath() throws Exception {
        String url = "http://localhost:8080/v1/graphql";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            String body = IOUtils.toString(response.getEntity().getContent(), "utf8");
            Assert.assertEquals(400, statusCode);
            if(statusCode == 400) {
                Status status = Config.getInstance().getMapper().readValue(body, Status.class);
                Assert.assertEquals("ERR11500", status.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidPostPath() throws Exception {
        String url = "http://localhost:8080/v1/graphql";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(Headers.CONTENT_TYPE.toString(), "application/json");
        StringEntity entity = new StringEntity("Hello");
        httpPost.setEntity(entity);
        try {
            CloseableHttpResponse response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String body = IOUtils.toString(response.getEntity().getContent(), "utf8");
            Assert.assertEquals(400, statusCode);
            if(statusCode == 400) {
                Status status = Config.getInstance().getMapper().readValue(body, Status.class);
                Assert.assertEquals("ERR11500", status.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInvalidPostMethod() throws Exception {
        String url = "http://localhost:8080/graphql";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        try {
            CloseableHttpResponse response = client.execute(httpDelete);
            int statusCode = response.getStatusLine().getStatusCode();
            String body = IOUtils.toString(response.getEntity().getContent(), "utf8");
            Assert.assertEquals(405, statusCode);
            if(statusCode == 405) {
                Status status = Config.getInstance().getMapper().readValue(body, Status.class);
                Assert.assertEquals("ERR11501", status.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    @Test
    public void testGetMissingQueryParameter() throws Exception {
        String url = "http://localhost:8080/graphql";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            String body = IOUtils.toString(response.getEntity().getContent(), "utf8");
            Assert.assertEquals(400, statusCode);
            if(statusCode == 400) {
                Status status = Config.getInstance().getMapper().readValue(body, Status.class);
                Assert.assertEquals("ERR11502", status.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}

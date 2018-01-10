package com.networknt.graphql.router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.config.Config;
import com.networknt.graphql.common.GraphqlConstants;
import com.networknt.graphql.router.models.QueryParameters;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.execution.reactive.CompletionStageMappingPublisher;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

import static com.networknt.graphql.common.GraphqlConstants.GraphqlSubscriptionConstants;

public class GraphqlSubscriptionHandler implements WebSocketConnectionCallback {
    private Logger logger = LoggerFactory.getLogger(GraphqlSubscriptionHandler.class);

    @Override
    public void onConnect(WebSocketHttpExchange webSocketHttpExchange, WebSocketChannel webSocketChannel) {
        webSocketChannel.getReceiveSetter().set(new AbstractReceiveListener() {

            @Override
            protected void onError(WebSocketChannel channel, Throwable error) {
                if(logger.isDebugEnabled()) logger.debug("Websocket connection error.");
                super.onError(channel, error);
            }

            @Override
            protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) throws IOException {
                if(logger.isDebugEnabled()) logger.debug("Websocket connection close.");
                super.onClose(webSocketChannel, channel);
            }

            /**
             * Responsible for parsing the different types of requests and generating appropriate responses.
             *
             * @param channel
             * @param message
             * @throws IOException
             */
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                String messageData = message.getData();
                if(logger.isDebugEnabled()) logger.debug("Message = " + messageData);
                Map inputData = Config.getInstance().getMapper().readValue(messageData, Map.class);

                String requestType = (String) inputData.get(GraphqlSubscriptionConstants.GRAPHQL_REQ_TYPE_KEY);
                // We receive an init when graphiql is initially loaded (no subscription query sent). We
                // respond with init success.
                if (GraphqlSubscriptionConstants.GRAPHQL_INIT_REQ_TYPE.equals(requestType)) {
                    sendInitSuccess(channel);
                } else if (GraphqlSubscriptionConstants.GRAPHQL_START_REQ_TYPE.equals(requestType)) {
                    Integer operationId = (Integer) inputData.get(GraphqlSubscriptionConstants.GRAPHQL_OP_ID_KEY);
                    ExecutionResult executionResult = getExecutionResult(inputData);
                    if (executionResult.getErrors() != null && executionResult.getErrors().size() > 0) {
                        // If we fail to initially get the result, send an error.
                        sendDataResponse(channel, executionResult, operationId);
                    } else {
                        // We successfully got a subscription, send a subscription success.
                        subscribeToResults(executionResult, channel, operationId);
                        sendSubscriptionSuccess(channel, operationId);
                    }
                }
            }
        });
        webSocketChannel.resumeReceives();
    }

    /**
     * Execute the given query and return the response.
     *
     * @param inputData The input parameters.
     */
    private ExecutionResult getExecutionResult(Map inputData) {
        QueryParameters parameters = QueryParameters.from(inputData);
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(parameters.getQuery())
                .variables(parameters.getVariables())
                .build();
        Instrumentation instrumentation = new ChainedInstrumentation(Collections.singletonList(new TracingInstrumentation()));
        return GraphQL.newGraphQL(GraphqlPostHandler.schema)
                .instrumentation(instrumentation)
                .build()
                .execute(executionInput);
    }

    /**
     * Helper method to send data to the client.
     */
    private void sendDataResponse(WebSocketChannel channel, ExecutionResult executionResult, Integer operationId) {
        Map<String, Object> nextPayload = new HashMap<>();
        if (executionResult.getData() != null) {
            nextPayload.put(GraphqlConstants.GraphqlRouterConstants.GRAPHQL_RESPONSE_DATA_KEY, executionResult.getData());
        }
        if (executionResult.getErrors() != null && executionResult.getErrors().size() > 0) {
            nextPayload.put(GraphqlConstants.GraphqlRouterConstants.GRAPHQL_RESPONSE_ERROR_KEY, executionResult.getErrors());
        }

        Map<String, Object> result = new HashMap<>();
        result.put(GraphqlSubscriptionConstants.GRAPHQL_OP_ID_KEY, operationId);
        result.put(GraphqlSubscriptionConstants.GRAPHQL_REQ_TYPE_KEY, GraphqlSubscriptionConstants.GRAPHQL_DATA_RES_TYPE);
        result.put(GraphqlConstants.GraphqlRouterConstants.GRAPHQL_RESPONSE_PAYLOAD_KEY, nextPayload);
        try {
            WebSockets.sendText(Config.getInstance().getMapper().writeValueAsString(result), channel, null);
        } catch (JsonProcessingException e) {
            logger.error("Error while processing data response", e);
        }
    }

    /**
     * Generic subscription manager to propagate data from the action.
     */
    private void subscribeToResults(ExecutionResult executionResult, WebSocketChannel channel, Integer id) {
        CompletionStageMappingPublisher<ExecutionResult, CompletionStage> mappingPublisher = executionResult.getData();

        mappingPublisher.subscribe(new Subscriber<ExecutionResult>() {
            private final AtomicReference<Subscription> subscriptionRef = new AtomicReference<>();

            @Override
            public void onSubscribe(Subscription subscription) {
                subscriptionRef.set(subscription);
                subscription.request(1);
            }

            @Override
            public void onNext(ExecutionResult nextExecutionResult) {
                sendDataResponse(channel, nextExecutionResult, id);
                subscriptionRef.get().request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.info("Subscription onError", throwable);
                subscriptionRef.get().cancel();
            }

            @Override
            public void onComplete() {
                logger.info("Subscription onComplete");
                subscriptionRef.get().cancel();
            }
        });
    }

    /**
     * Helper method to respond with init_success
     */
    private void sendInitSuccess(WebSocketChannel channel) throws JsonProcessingException {
        Map<String, Object> outputData = new HashMap<>();
        outputData.put(GraphqlSubscriptionConstants.GRAPHQL_REQ_TYPE_KEY, GraphqlSubscriptionConstants.GRAPHQL_INIT_SUCCESS_REQ_TYPE);
        WebSockets.sendText(Config.getInstance().getMapper().writeValueAsString(outputData), channel, null);
    }

    /**
     * Helper method to respond with subscription_success.
     */
    private void sendSubscriptionSuccess(WebSocketChannel channel, Integer operationId) throws JsonProcessingException {
        Map<String, Object> outputData = new HashMap<>();
        outputData.put(GraphqlSubscriptionConstants.GRAPHQL_REQ_TYPE_KEY, GraphqlSubscriptionConstants.GRAPHQL_SUCCESS_REQ_TYPE);
        outputData.put(GraphqlSubscriptionConstants.GRAPHQL_OP_ID_KEY, operationId);
        WebSockets.sendText(Config.getInstance().getMapper().writeValueAsString(outputData), channel, null);
    }
}

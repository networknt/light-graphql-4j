package com.networknt.graphql.router;

import com.networknt.handler.config.EndpointSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GraphqlEndpointSourceTest {

    @Test
    public void testPetstoreEndpoints() {
        GraphqlEndpointSource source = new GraphqlEndpointSource();
        Iterable<EndpointSource.Endpoint> endpoints = source.listEndpoints();

        // Extract a set of string representations of endpoints
        Set<String> endpointStrings = StreamSupport
            .stream(endpoints.spliterator(), false)
            .map(Object::toString)
            .collect(Collectors.toSet());

        // Assert that we got what we wanted
        Assert.assertEquals(
            new HashSet<>(Arrays.asList(
                "/graphql@GET",
                "/graphql@POST",
                "/graphql@OPTIONS"
            )),
            endpointStrings
        );
    }

}
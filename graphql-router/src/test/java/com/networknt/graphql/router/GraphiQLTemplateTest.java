package com.networknt.graphql.router;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

class GraphiQLTemplateTest {
    private static final Pattern EXTERNAL_RESOURCE_PATTERN =
            Pattern.compile("(src|href)\\s*=\\s*['\"](?:https?:)?//", Pattern.CASE_INSENSITIVE);

    @Test
    void graphiqlTemplateDoesNotLoadExternalCode() throws Exception {
        String html = loadTemplate();

        Assertions.assertFalse(EXTERNAL_RESOURCE_PATTERN.matcher(html).find());
    }

    @Test
    void graphiqlTemplateDoesNotAssignUrlKeysIntoObjectProperties() throws Exception {
        String html = loadTemplate();

        Assertions.assertFalse(html.contains("parameters["));
        Assertions.assertFalse(html.contains("Object.assign"));
        Assertions.assertFalse(html.contains("decodeURIComponent(entry.slice(0, eq))"));
    }

    private static String loadTemplate() throws Exception {
        try (InputStream inputStream = GraphiQLTemplateTest.class.getResourceAsStream("/config/graphiql.html")) {
            Assertions.assertNotNull(inputStream);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

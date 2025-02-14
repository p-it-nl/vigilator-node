/**
 * Copyright (c) p-it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.p.it.vigilatornode.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for request helper
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class RequestHelperTest {

    @Mock
    private HttpExchange exchange;

    @Mock
    private NodeConfig config;

    private RequestHelper classUnderTest;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String KEY_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ORIGINS = "mock";
    private static final String URI_WITHOUT_QUERY = "mock";
    private static final String QUERY_ONE_KEY = "mock";
    private static final String QUERY_ONE_VALUE = "mock";
    private static final String QUERY_TWO_KEY = "article";
    private static final String QUERY_TWO_VALUE = "mock";
    private static final String URI_WITH_QUERY = "mock?" + QUERY_ONE_KEY + "=" + QUERY_ONE_VALUE;
    private static final String URI_WITH_QUERIES = "mock?" + QUERY_ONE_KEY + "=" + QUERY_ONE_VALUE + "&" + QUERY_TWO_KEY + "=" + QUERY_TWO_VALUE;

    @BeforeEach
    public void setUp() {
        classUnderTest = new RequestHelper();
    }

    @Test
    public void isGetMethod_withoutExchange() {
        boolean expected = false;

        boolean result = classUnderTest.isGetMethod(null);

        assertTrue(expected == result);
    }

    @Test
    public void isGetMethod_withExchangeNotGetMethod() {
        boolean expected = false;

        boolean result = classUnderTest.isGetMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isGetMethod_withExchangeNotBeingPostMethod() {
        boolean expected = false;
        when(exchange.getRequestMethod()).thenReturn(METHOD_POST);

        boolean result = classUnderTest.isGetMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isGetMethod_withExchangeBeingGetMethod() {
        boolean expected = true;
        when(exchange.getRequestMethod()).thenReturn(METHOD_GET);

        boolean result = classUnderTest.isGetMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isPostMethod_withoutExchange() {
        boolean expected = false;

        boolean result = classUnderTest.isPostMethod(null);

        assertTrue(expected == result);
    }

    @Test
    public void isPostMethod_withExchangeNotPostMethod() {
        boolean expected = false;

        boolean result = classUnderTest.isPostMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isPostMethod_withExchangeNotBeingPostMethod() {
        boolean expected = false;
        when(exchange.getRequestMethod()).thenReturn(METHOD_GET);

        boolean result = classUnderTest.isPostMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isPostMethod_withExchangeBeingPostMethod() {
        boolean expected = true;
        when(exchange.getRequestMethod()).thenReturn(METHOD_POST);

        boolean result = classUnderTest.isPostMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isOptionsMethod_withoutExchange() {
        boolean expected = false;

        boolean result = classUnderTest.isOptionsMethod(null);

        assertTrue(expected == result);
    }

    @Test
    public void isOptionsMethod_withExchangeNotOptionsMethod() {
        boolean expected = false;

        boolean result = classUnderTest.isOptionsMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isOptionsMethod_withExchangeNotBeingOptionsMethod() {
        boolean expected = false;
        when(exchange.getRequestMethod()).thenReturn(METHOD_GET);

        boolean result = classUnderTest.isOptionsMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void isOptionsMethod_withExchangeBeingPostMethod() {
        boolean expected = true;
        when(exchange.getRequestMethod()).thenReturn(METHOD_OPTIONS);

        boolean result = classUnderTest.isOptionsMethod(exchange);

        assertTrue(expected == result);
    }

    @Test
    public void enableCrossOriginRequests_withoutExchangeAndConfig() throws IOException {
        classUnderTest.enableCrossOriginRequests(null, null);
    }

    @Test
    public void enableCrossOriginRequests_withExchangeAndNotHavingConfig() throws IOException {
        classUnderTest.enableCrossOriginRequests(exchange, null);
    }

    @Test
    public void enableCrossOriginRequests_withExchangeAndConfig() throws IOException {
        Headers headers = getHeaders(null, null);
        when(exchange.getResponseHeaders()).thenReturn(headers);
        when(config.getAllowedOrigins()).thenReturn(ORIGINS);

        classUnderTest.enableCrossOriginRequests(exchange, config);

        assertTrue(exchange.getResponseHeaders().containsKey(KEY_ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void getParams_withoutExchange() {
        Map<String, String> result = classUnderTest.getParams(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getParams_withExchangeNotHavingRequestURI() {
        Map<String, String> result = classUnderTest.getParams(exchange);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getParams_withExchangeHavingRequestURIButNoQuery() throws URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI(URI_WITHOUT_QUERY));

        Map<String, String> result = classUnderTest.getParams(exchange);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getParams_withExchangeHavingRequestURIWithQuery() throws URISyntaxException {
        String expected = QUERY_ONE_VALUE;
        when(exchange.getRequestURI()).thenReturn(new URI(URI_WITH_QUERY));

        Map<String, String> result = classUnderTest.getParams(exchange);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.containsKey(QUERY_ONE_KEY));
        assertEquals(expected, result.get(QUERY_ONE_KEY));
    }

    @Test
    public void getParams_withExchangeHavingRequestURIWithQueries() throws URISyntaxException {
        String expected = QUERY_ONE_VALUE;
        String expectedAlso = QUERY_TWO_VALUE;
        when(exchange.getRequestURI()).thenReturn(new URI(URI_WITH_QUERIES));

        Map<String, String> result = classUnderTest.getParams(exchange);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expected, result.get(QUERY_ONE_KEY));
        assertEquals(expectedAlso, result.get(QUERY_TWO_KEY));
    }

    private Headers getHeaders(final String key, final String value) {
        Headers headers = new Headers();
        if (key != null) {
            headers.set(key, value);
        }

        return headers;
    }
}

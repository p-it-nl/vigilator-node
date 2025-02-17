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
package nl.p.it.vigilatornode.domain.out;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;
import nl.p.it.vigilatornode.exception.HttpClientException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for request
 *
 * @author Patrick
 */
public class RequestTest {

    private static final String REQUEST = "request";
    private static final String ACCEPTOR = "acceptor";
    private static final String CLIENT = "client";

    @Test
    public void requestInstanceWithoutAnyValues() {
        HttpRequest httpRequest = null;
        Acceptor<MonitoredData> acceptor = null;
        HttpClient client = null;

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> new Request(httpRequest, acceptor, client));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(REQUEST));
        assertTrue(exception.getMessage().contains(ACCEPTOR));
        assertTrue(exception.getMessage().contains(CLIENT));
    }

    @Test
    public void requestInstanceWithoutRequest() {
        HttpRequest httpRequest = null;
        Acceptor<MonitoredData> acceptor = (MonitoredData data) -> {
        };
        HttpClient client = HttpClient.newHttpClient();

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> new Request(httpRequest, acceptor, client));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(REQUEST));
    }

    @Test
    public void requestInstanceWithoutHttpClient() throws URISyntaxException {
        HttpRequest httpRequest = getMinimalRequest();
        Acceptor<MonitoredData> acceptor = (MonitoredData data) -> {
        };
        HttpClient client = null;

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> new Request(httpRequest, acceptor, client));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(CLIENT));
    }

    @Test
    public void requestInstanceWithoutAcceptor() throws URISyntaxException {
        HttpRequest httpRequest = getMinimalRequest();
        Acceptor<MonitoredData> acceptor = null;
        HttpClient client = HttpClient.newHttpClient();

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> new Request(httpRequest, acceptor, client));

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(ACCEPTOR));
    }

    private HttpRequest getMinimalRequest() throws URISyntaxException {
        return HttpRequest.newBuilder().uri(new URI("https://localhost/")).build();
    }
}

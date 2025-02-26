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
package nl.p.it.vigilatornode.domain.monitor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.domain.resources.ExposedResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResourceConfig;
import nl.p.it.vigilatornode.exception.MonitorException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for monitor
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class MonitorIntegrationTest {

    @Mock
    private NodeConfig config;

    @Mock
    private HttpClient client;

    private OutgoingClient outgoing;

    private Monitor monitor;

    @BeforeEach
    public void setUp() throws MonitorException, IOException, InterruptedException {
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(100);
        outgoing = OutgoingClient.getInstance(config);
        monitor = new Monitor(getExposedResources(), config);
        outgoing.switchClient(client);
        monitor.connectToOutgoingClient(outgoing);
        prepareResponses();
    }

    @Test
    public void monitorTest() throws MonitorException, InterruptedException {
        monitor.start();

        Thread.sleep(300);
        assertTrue(monitor.isActive());
        List<MonitoredResource> resources = monitor.getResources();
        for (MonitoredResource resource : resources) {
            System.out.println(resource);

            for (MonitoredData entry : resource.getData()) {
                System.out.println(entry);
            }
        }
    }

    private List<MonitoredResource> getExposedResources() {
        List<MonitoredResource> resources = new ArrayList<>();
        ExposedResource resource = new ExposedResource();
        resource.setName("ResourceOne");
        resource.decorate(MonitoredResourceConfig.TYPE, "active", "true");
        resource.decorate(MonitoredResourceConfig.TYPE, "url", "http://resource-one.com/");
        resource.decorate("ResourceOne", "isProcessing", "true");
        resource.decorate("ResourceOne", "hasExceptions", "false");
        resources.add(resource);

        resource = new ExposedResource();
        resource.setName("ResourceTwo");
        resource.decorate(MonitoredResourceConfig.TYPE, "active", "false");
        resource.decorate(MonitoredResourceConfig.TYPE, "url", "http://resource-two.com/");
        resource.decorate("ResourceTwo", "isProcessing", "true");
        resources.add(resource);

        resource = new ExposedResource();
        resource.setName("ResourceThree");
        resource.decorate(MonitoredResourceConfig.TYPE, "active", "true");
        resource.decorate(MonitoredResourceConfig.TYPE, "url", "http://resource-three.com/");
        resource.decorate("ResourceThree", "database", "running");
        resource.decorate("ResourceThree", "threads queued", "> 10 W");
        resource.decorate("ResourceThree", "threads broken", "> 0");
        resources.add(resource);

        return resources;
    }

    private void prepareResponses() throws IOException, InterruptedException {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofByteArray())))
                .thenReturn(new MockResponse(200, RESPONSE_RESOURCE_ONE_OK.getBytes()))
                .thenReturn(new MockResponse(200, RESPONSE_RESOURCE_THREE_NOK.getBytes()))
                .thenReturn(new MockResponse(200, RESPONSE_RESOURCE_ONE_NOK.getBytes()))
                .thenReturn(new MockResponse(200, RESPONSE_RESOURCE_THREE_NOK.getBytes()))
                .thenReturn(new MockResponse(200, RESPONSE_RESOURCE_ONE_OK.getBytes()))
                .thenReturn(new MockResponse(200, RESPONSE_RESOURCE_THREE_NOK.getBytes()));
    }

    private static final String RESPONSE_RESOURCE_ONE_OK = """
        {
            "environment": "mock",
            "status": [
                {
                    "name": "ResourceOne",
                    "items": {
                        "isProcessing": "true",
                        "hasExceptions": "false"
                    }
                }
            ]
        }
        """;
    private static final String RESPONSE_RESOURCE_ONE_NOK = """
        {
            "environment": "mock",
            "status": [
                {
                    "name": "ResourceOne",
                    "items": {
                        "isProcessing": "false",
                        "hasExceptions": "true"
                    }
                }
            ]
        }
        """;
    private static final String RESPONSE_RESOURCE_THREE_NOK = """
        {
            "environment": "mock",
            "status": [
                {
                    "name": "ResourceThree",
                    "items": {
                        "database": "broken",
                        "threads queued": "1987",
                        "threads broken": "99",
                    }
                }
            ]
        }
    """;

    private void linkThreadpool() {
        when(config.getSingleThreadExecutor()).thenReturn(
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10)));
        when(config.getPoolExecutor()).thenReturn(
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10)));
    }

    private class MockResponse implements HttpResponse {

        private final int status;
        private final byte[] body;

        public MockResponse(final int status, final byte[] body) {
            this.status = status;
            this.body = body;
        }

        @Override
        public int statusCode() {
            return status;
        }

        @Override
        public Object body() {
            return body;
        }

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public Optional previousResponse() {
            return null;
        }

        @Override
        public HttpHeaders headers() {
            return null;
        }

        @Override
        public Optional sslSession() {
            return null;
        }

        @Override
        public URI uri() {
            return null;
        }

        @Override
        public HttpClient.Version version() {
            return null;
        }

    }
}

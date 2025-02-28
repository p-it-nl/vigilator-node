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
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.domain.resources.ExposedResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResourceConfig;
import nl.p.it.vigilatornode.exception.MonitorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static nl.p.it.vigilatornode.domain.monitor.MonitorIntegrationTestConstants.*;

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

    private Monitor monitor;

    @BeforeEach
    public void setUp() throws MonitorException, IOException, InterruptedException {
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(100);
        OutgoingClient outgoing = OutgoingClient.getInstance(config);
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
        MonitoredResource one = resources.get(0);
        MonitoredResource two = resources.get(1);
        MonitoredResource three = resources.get(2);

        assertTrue(one.isHealthy());
        assertFalse(two.isHealthy());
        assertFalse(three.isHealthy());
    }

    private List<MonitoredResource> getExposedResources() {
        List<MonitoredResource> resources = new ArrayList<>();
        ExposedResource resource = new ExposedResource();
        resource.setName(RESOURCE_ONE);
        resource.decorate(MonitoredResourceConfig.TYPE, KEY_ACTIVE, TRUE);
        resource.decorate(MonitoredResourceConfig.TYPE, KEY_URL, "http://resource-one.com/");
        resource.decorate(RESOURCE_ONE, KEY_IS_PROCESSING, CONDITION_EQUALS_FALSE);
        resource.decorate(RESOURCE_ONE, KEY_HAS_EXCEPTIONS, CONDITION_EQUALS_TRUE);
        resources.add(resource);

        resource = new ExposedResource();
        resource.setName(RESOURCE_TWO);
        resource.decorate(MonitoredResourceConfig.TYPE, KEY_ACTIVE, FALSE);
        resource.decorate(MonitoredResourceConfig.TYPE, KEY_URL, "http://resource-two.com/");
        resource.decorate(RESOURCE_TWO, KEY_IS_PROCESSING, CONDITION_EQUALS_TRUE);
        resources.add(resource);

        resource = new ExposedResource();
        resource.setName(RESOURCE_THREE);
        resource.decorate(MonitoredResourceConfig.TYPE, KEY_ACTIVE, TRUE);
        resource.decorate(MonitoredResourceConfig.TYPE, KEY_URL, "http://resource-three.com/");
        resource.decorate(RESOURCE_THREE, KEY_DATABASE, CONDITION_NOT_RUNNING);
        resource.decorate(RESOURCE_THREE, KEY_THREADS_QUEUED, CONDITION_BIGGER_THEN_TEN_WARNING);
        resource.decorate(RESOURCE_THREE, KEY_THREADS_BROKEN, CONDITION_BIGGER_THEN_ZERO);
        resources.add(resource);

        return resources;
    }

    private void prepareResponses() throws IOException, InterruptedException {
        when(client.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofByteArray())))
                .thenReturn(new TestResponse(200, RESPONSE_RESOURCE_ONE_OK.getBytes()))
                .thenReturn(new TestResponse(200, RESPONSE_RESOURCE_THREE_NOK.getBytes()))
                .thenReturn(new TestResponse(200, RESPONSE_RESOURCE_ONE_NOK.getBytes()))
                .thenReturn(new TestResponse(200, RESPONSE_RESOURCE_THREE_NOK.getBytes()))
                .thenReturn(new TestResponse(200, RESPONSE_RESOURCE_ONE_OK.getBytes()))
                .thenReturn(new TestResponse(200, RESPONSE_RESOURCE_THREE_NOK.getBytes()));
    }

    private void linkThreadpool() {
        when(config.getSingleThreadExecutor()).thenReturn(
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10)));
        when(config.getPoolExecutor()).thenReturn(
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10)));
    }

    private class TestResponse implements HttpResponse<byte[]> {

        private final int status;
        private final byte[] body;

        public TestResponse(final int status, final byte[] body) {
            this.status = status;
            this.body = body;
        }

        @Override
        public int statusCode() {
            return status;
        }

        @Override
        public byte[] body() {
            return body;
        }

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public Optional previousResponse() {
            return Optional.of(null);
        }

        @Override
        public HttpHeaders headers() {
            return null;
        }

        @Override
        public Optional sslSession() {
            return Optional.of(null);
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

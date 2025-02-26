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

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.domain.resources.ExposedResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.MonitorException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for monitor
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class MonitorTest {

    @Mock
    private OutgoingClient outgoing;

    @Mock
    private NodeConfig config;

    @Mock
    private MonitoredResource resourceMock;

    private static final String ERROR = "error";
    private static final String WARNING = "warning";

    @Test
    public void createMonitorWithoutValues() {
        CustomException expectedException = CustomException.CONFIG_REQUIRED;

        VigilatorNodeException exception = assertThrows(MonitorException.class, () -> new Monitor(null, null));

        assertEquals(expectedException.getMessage(), exception.getMessage());
    }

    @Test
    public void createMonitorWithoutConfig() {
        CustomException expectedException = CustomException.CONFIG_REQUIRED;

        VigilatorNodeException exception = assertThrows(MonitorException.class,
                () -> new Monitor(List.of(resourceMock), null));

        assertEquals(expectedException.getMessage(), exception.getMessage());
    }

    @Test
    public void createMonitorWithoutResources() {
        assertDoesNotThrow(() -> new Monitor(null, config));
    }

    @Test
    public void start() throws MonitorException {
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(1000);
        Monitor monitor = new Monitor(List.of(resourceMock), config);

        assertDoesNotThrow(() -> monitor.start());
        assertTrue(monitor.isActive());
    }

    @Test
    public void stop() throws MonitorException {
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(1000);
        Monitor monitor = new Monitor(List.of(resourceMock), config);

        assertDoesNotThrow(() -> monitor.stop());
        assertFalse(monitor.isActive());
    }

    @Test
    public void start_oneResourceUpdatedThreeTimes_healthy() throws MonitorException, InterruptedException {
        int expectedTakes = 3;
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(100);
        Monitor monitor = new Monitor(List.of(new OkResource()), config);

        assertDoesNotThrow(() -> monitor.start());

        Thread.sleep(300);
        assertTrue(monitor.isActive());
        List<MonitoredResource> resources = monitor.getResources();
        MonitoredResource resource = resources.get(0);
        int completedTakes = 0;
        for (MonitoredData data : resource.getData()) {
            assertTrue(data.isHealthy());
            completedTakes++;
        }
        assertEquals(expectedTakes, completedTakes);
    }

    @Test
    public void start_oneResourceUpdatedThreeTimes_unhealthy() throws MonitorException, InterruptedException {
        int expectedTakes = 3;
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(100);
        Monitor monitor = new Monitor(List.of(new ErrorResource()), config);

        assertDoesNotThrow(() -> monitor.start());

        Thread.sleep(300);
        assertTrue(monitor.isActive());
        List<MonitoredResource> resources = monitor.getResources();
        MonitoredResource resource = resources.get(0);
        int completedTakes = 0;
        for (MonitoredData data : resource.getData()) {
            assertTrue(data.getErrors().contains(ERROR));
            assertFalse(data.isHealthy());
            completedTakes++;
        }
        assertEquals(expectedTakes, completedTakes);
    }

    @Test
    public void start_oneResourceUpdatedThreeTimes_warning() throws MonitorException, InterruptedException {
        int expectedTakes = 3;
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(100);
        Monitor monitor = new Monitor(List.of(new WarningResource()), config);

        assertDoesNotThrow(() -> monitor.start());

        Thread.sleep(300);
        assertTrue(monitor.isActive());
        List<MonitoredResource> resources = monitor.getResources();
        MonitoredResource resource = resources.get(0);
        int completedTakes = 0;
        for (MonitoredData data : resource.getData()) {
            assertTrue(data.getWarnings().contains(WARNING));
            assertTrue(data.isHealthy());
            completedTakes++;
        }
        assertEquals(expectedTakes, completedTakes);
    }

    @Test
    public void start_multipleResourcesUpdatedThreeTimes() throws MonitorException, InterruptedException {
        int expectedTakes = 3;
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(100);
        Monitor monitor = new Monitor(List.of(new OkResource(), new ErrorResource(), new WarningResource()), config);

        assertDoesNotThrow(() -> monitor.start());

        Thread.sleep(300);
        assertTrue(monitor.isActive());
        for (MonitoredResource resource : monitor.getResources()) {
            int completedTakes = 0;
            for (MonitoredData data : resource.getData()) {
                assertTrue(data.getWarnings() != null);
                assertTrue(data.getErrors() != null);
                assertTrue(data.getSince() != null);
                completedTakes++;
            }
            assertEquals(expectedTakes, completedTakes);
        }
    }

    @Test
    public void start_oneResourcesUpdatedThreeTimes_resultDiffering() throws MonitorException, InterruptedException {
        int expectedTakes = 3;
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(100);
        Monitor monitor = new Monitor(List.of(new FlickeringResource()), config);

        assertDoesNotThrow(() -> monitor.start());

        Thread.sleep(300);
        assertTrue(monitor.isActive());
        MonitoredResource resource = monitor.getResources().get(0);
        int completedTakes = 0;
        for (MonitoredData data : resource.getData()) {
            assertTrue(data.getWarnings() != null);
            assertTrue(data.getErrors() != null);
            assertTrue(data.getSince() != null);
            completedTakes++;
        }
        assertEquals(expectedTakes, completedTakes);
    }

    @Test
    public void startWithExposedResourcesWithoutOutgoingClient_expectingException() throws MonitorException {
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(1000);
        CustomException expectedException = CustomException.REQUIRMENTS_EXPOSED_RESOURCE_NOT_MET;

        Monitor monitor = new Monitor(List.of(new ExposedResource()), config);
        VigilatorNodeException exception = assertThrows(MonitorException.class, () -> monitor.start());

        assertEquals(expectedException.getMessage(), exception.getMessage());
    }

    @Test
    public void startWithExposedResourcesWithOutgoingClient_expectingException() throws MonitorException {
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(1000);
        Monitor monitor = new Monitor(List.of(new ExposedResource()), config);
        monitor.connectToOutgoingClient(outgoing);

        assertDoesNotThrow(() -> monitor.start());
        assertTrue(monitor.isActive());
    }

    private void linkThreadpool() {
        when(config.getSingleThreadExecutor()).thenReturn(
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10)));
    }

    private class OkResource extends MonitoredResource {

        public OkResource() {
            this.config.setActive(true);
        }

        @Override
        public void updateStatus() {
            take++;
            MonitoredData result = new MonitoredData(new byte[0]);
            result.label(take);
            data.add(result);
        }
    }

    private class ErrorResource extends MonitoredResource {

        public ErrorResource() {
            this.config.setActive(true);
        }

        @Override
        public void updateStatus() {
            take++;
            MonitoredData result = new MonitoredData(new byte[0]);
            result.addError(ERROR);
            result.label(take);
            data.add(result);
        }
    }

    private class WarningResource extends MonitoredResource {

        public WarningResource() {
            this.config.setActive(true);
        }

        @Override
        public void updateStatus() {
            take++;
            MonitoredData result = new MonitoredData(new byte[0]);
            result.addWarning(WARNING);
            result.label(take);
            data.add(result);
        }
    }

    private class FlickeringResource extends MonitoredResource {

        public FlickeringResource() {
            this.config.setActive(true);
        }
        
        @Override
        public void updateStatus() {
            take++;

            MonitoredData result = new MonitoredData(new byte[0]);
            int d = (int) (Math.random() * 3 + 1);
            switch (d) {
                case 1 -> {
                    result.addError(ERROR);
                }
                case 2 -> {
                    result.addWarning(WARNING);
                }
            }

            result.label(take);
            data.add(result);
        }
    }
}

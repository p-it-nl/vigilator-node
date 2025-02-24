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
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.MonitorException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests for monitor
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class MonitorTest {

    @Mock
    private NodeConfig config;

    @Mock
    private MonitoredResource resource;

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
                () -> new Monitor(List.of(resource), null));

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
        Monitor monitor = new Monitor(List.of(resource), config);

        assertDoesNotThrow(() -> monitor.start());
    }

    // TODO: Add tests validating the monitor does start something
    
    @Test
    public void stop() throws MonitorException {
        linkThreadpool();
        when(config.getDefaultUpdateFrequency()).thenReturn(1000);
        Monitor monitor = new Monitor(List.of(resource), config);

        assertDoesNotThrow(() -> monitor.start());

    }

    // TODO: Add tests validating the monitor does stop something

    private void linkThreadpool() {   
        when(config.getSingleThreadExecutor()).thenReturn(
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10)));
    }
}

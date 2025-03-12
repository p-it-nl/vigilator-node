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
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.resources.ExposedResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.MonitorException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for monitor store
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class MonitorStoreTest {

    @Mock
    private NodeConfig config;

    private static MonitorStore classUnderTest;

    @BeforeAll
    public static void setUp() {
        classUnderTest = MonitorStore.getInstance();
    }

    @Test
    public void buildMonitorForWithoutValues() {
        CustomException expectedException = CustomException.CONFIG_REQUIRED;

        VigilatorNodeException exception = assertThrows(MonitorException.class, () -> classUnderTest.buildMonitorFor(null, null));

        assertEquals(expectedException.getMessage(), exception.getMessage());
    }

    @Test
    public void buildMonitorForWithoutConfig() {
        CustomException expectedException = CustomException.CONFIG_REQUIRED;
        MonitoredResource resource = new ExposedResource();

        VigilatorNodeException exception = assertThrows(MonitorException.class,
                () -> classUnderTest.buildMonitorFor(List.of(resource), null));

        assertEquals(expectedException.getMessage(), exception.getMessage());
    }

    @Test
    public void buildMonitorForWithoutResources() {
        assertDoesNotThrow(() -> classUnderTest.buildMonitorFor(null,
                config));
    }

    @Test
    public void buildMonitorForWithResourcesAndConfig() throws MonitorException {
        MonitoredResource resource = new ExposedResource();

        Monitor monitor = classUnderTest.buildMonitorFor(List.of(resource), config);

        assertNotNull(monitor);
        assertEquals(resource, monitor.getResources().get(0));
    }

    @Test
    public void getMonitorsWithoutARegisteredMonitor() throws MonitorException {
        List<Monitor> monitors = classUnderTest.getMonitors();

        assertNotNull(monitors);
        assertTrue(monitors.isEmpty());
    }

    @Test
    public void getMonitorsWithMonitorNotHavingAnyResources() throws MonitorException {
        classUnderTest.buildMonitorFor(null, config);
        List<Monitor> monitors = classUnderTest.getMonitors();

        assertNotNull(monitors);
        assertTrue(monitors.get(0).getResources().isEmpty());
    }

    @Test
    public void getMonitorsWithMonitorHavingAResource() throws MonitorException {
        MonitoredResource resource = new ExposedResource();

        classUnderTest.buildMonitorFor(List.of(resource), config);
        List<Monitor> monitors = classUnderTest.getMonitors();

        assertNotNull(monitors);
        List<MonitoredResource> resources = monitors.get(0).getResources();
        assertEquals(resource, resources.get(0));
    }
    
    @AfterEach
    public void clear() {
        classUnderTest.clear();
    }
}

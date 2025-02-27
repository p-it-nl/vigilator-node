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
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for monitor task
 *
 * @author Patrick
 */
public class MonitorTaskTest {

    @Test
    public void createMonitorTaskWithoutValues() {
        MonitorTask task = new MonitorTask(null, null);

        assertDoesNotThrow(() -> task.run());
    }

    @Test
    public void createMonitorTaskWithoutResources() {
        MonitorTask task = new MonitorTask(null, mock());

        assertDoesNotThrow(() -> task.run());
    }

    @Test
    public void createMonitorTaskWithoutNotifier() {
        MonitorTask task = new MonitorTask(List.of(new TestResource(true)), null);

        assertDoesNotThrow(() -> task.run());
    }

    @Test
    public void createMonitorTaskWithActiveAndNotActiveResource_expectingOnlyActiveToBeCalled() {
        TestResource active = new TestResource(true);
        TestResource inActive = new TestResource(false);
        MonitorTask task = new MonitorTask(List.of(active, inActive), null);

        assertDoesNotThrow(() -> task.run());

        assertTrue(active.hasBeenCalled());
        assertFalse(inActive.hasBeenCalled());
    }

    private class TestResource extends MonitoredResource {

        private boolean isCalled;

        public TestResource(final boolean isActive) {
            config.setActive(isActive);
        }

        @Override
        public void updateStatus() {
            isCalled = true;
        }

        public boolean hasBeenCalled() {
            return isCalled;
        }
    }

    private Notifier mock() {
        return () -> {
        };
    }
}

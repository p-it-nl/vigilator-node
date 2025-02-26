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

    private static final String ACTIVE = "active";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

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
        MonitorTask task = new MonitorTask(List.of(new MockResource(TRUE)), null);

        assertDoesNotThrow(() -> task.run());
    }

    @Test
    public void createMonitorTaskWithActiveAndNotActiveResource_expectingOnlyActiveToBeCalled() {
        MockResource active = new MockResource(TRUE);
        MockResource inActive = new MockResource(FALSE);
        MonitorTask task = new MonitorTask(List.of(active, inActive), null);

        assertDoesNotThrow(() -> task.run());

        assertTrue(active.hasBeenCalled());
        assertFalse(inActive.hasBeenCalled());
    }

    private class MockResource extends MonitoredResource {

        private boolean isCalled;

        public MockResource(final String isActive) {
            config.set(ACTIVE, isActive);
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

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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for wait task
 *
 * @author Patrick
 */
public class WaitTaskTest {

    private long start;

    private static final int WAIT_TIME = 200;
    private static final int GRACEFULL_PERIOD = 50;
    
    @Test
    public void createWaitTaskWithoutValues() {
        WaitTask task = new WaitTask(0, null);

        assertDoesNotThrow(() -> task.run());
    }

    @Test
    public void createWaitTaskWithoutNotifier() {
        WaitTask task = new WaitTask(WAIT_TIME, null);

        assertDoesNotThrow(() -> task.run());
    }

    @Test
    public void runWaitTask() {
        start = System.currentTimeMillis();
        WaitTask task = new WaitTask(WAIT_TIME, mock());
        
        assertDoesNotThrow(() -> task.run());
    }
    
    private Notifier mock() {
        return () -> {
            int timePassed = (int) (start - System.currentTimeMillis()) * -1;

            assertTrue(timePassed > WAIT_TIME);
            assertTrue(timePassed < WAIT_TIME + GRACEFULL_PERIOD);
        };
    }
}

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

import static java.lang.System.Logger.Level.ERROR;
import java.util.List;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;

/**
 * Task to wait and notify when wait completed
 *
 * @author Patrick
 */
public class MonitorTask implements Runnable {

    private final List<MonitoredResource> resources;
    private final Notifier notifier;

    private static final System.Logger LOGGER = System.getLogger(MonitorTask.class.getName());

    public MonitorTask(final List<MonitoredResource> resources, final Notifier notifier) {
        this.resources = resources;
        this.notifier = notifier;
    }

    @Override
    public void run() {
        try {
            if (resources != null) {
                for (MonitoredResource resource : resources) {
                    if (resource.getConfig().isActive()) {
                        resource.updateStatus();
                    } else {
                        // only updating resources that are activated to monitor
                    }
                }

            }
            if (notifier != null) {
                notifier.doNotify();
            }
        } catch (Exception ex) {
            LOGGER.log(ERROR, "Exception in monitor task: {0}", ex);
        }
    }
}

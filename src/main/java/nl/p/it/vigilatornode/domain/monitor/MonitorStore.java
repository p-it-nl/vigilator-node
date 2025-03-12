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

import java.util.ArrayList;
import java.util.List;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.exception.MonitorException;

/**
 * Stores reference to monitors, allowing retrieving status updates and other
 * interactions
 *
 * @author Patrick
 */
public class MonitorStore {

    private final List<Monitor> registered;

    private static MonitorStore instance;

    private MonitorStore() {
        registered = new ArrayList<>();
    }

    /**
     * Returns an `singleton` instance of the monitor store.
     *
     * @return the monitor store
     */
    public static synchronized MonitorStore getInstance() {
        if (instance == null) {
            instance = new MonitorStore();
        }

        return instance;
    }

    /**
     * Build a new monitor instance and registers it in the store
     *
     * @param resources the resources to monitor
     * @param config the configuration of this vigilator node
     * @return the build monitor
     * @throws MonitorException when the monitor cannot be created
     */
    public Monitor buildMonitorFor(final List<MonitoredResource> resources, final NodeConfig config) throws MonitorException {
        Monitor monitor = new Monitor(resources, config);
        registered.add(monitor);

        return monitor;
    }

    /**
     * @return the registered monitors in the store
     */
    public List<Monitor> getMonitors() {
        return registered;
    }

    /**
     * removes all registered monitors
     */
    public void clear() {
        registered.clear();
    }

}

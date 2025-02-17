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

import static java.lang.System.Logger.Level.INFO;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.domain.resources.ExposedResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.MonitorException;

/**
 * Monitor, schedules monitor tasks for resources and keeps track of results
 *
 * @author Patrick
 */
public class Monitor {

    private boolean prepared;

    private final ThreadPoolExecutor executor;
    private final int defaultUpdateFrequency;
    private final OutgoingClient outgoing;
    private final List<MonitoredResource> resources;

    private static final System.Logger LOGGER = System.getLogger(Monitor.class.getName());

    public Monitor(final List<MonitoredResource> resources, final NodeConfig config) throws MonitorException {
        if (config == null) {
            throw new MonitorException(CustomException.CONFIG_REQUIRED);
        }

        outgoing = OutgoingClient.getInstance(config);
        this.resources = resources;
        this.executor = config.getSingleThreadExecutor();
        this.defaultUpdateFrequency = config.getDefaultUpdateFrequency();
        prepared = false;
    }

    /**
     * Start monitoring the resources known to this monitor
     */
    public void start() {
        LOGGER.log(INFO, "Starting monitor process");
        if (!prepared) {
            prepare();
        }

        executor.submit(new MonitorTask(resources, monitorTaskFinished()));
    }

    /**
     * Stop the monitor, this gracefully ends active processes and stops
     * executing
     */
    public void stop() {
        executor.shutdown();
    }

    private Notifier monitorTaskFinished() {
        return () -> timeout();
    }

    private void timeout() {
        executor.submit(new WaitTask(defaultUpdateFrequency, timeoutFinished()));
    }

    private Notifier timeoutFinished() {
        return () -> start();
    }

    private void prepare() {
        for (MonitoredResource resource : resources) {
            switch (resource) {
                case ExposedResource exposed -> {
                    exposed.connect(outgoing);
                }
                default -> {
                    // no preparation required
                }
            }

        }
        prepared = true;
    }

}

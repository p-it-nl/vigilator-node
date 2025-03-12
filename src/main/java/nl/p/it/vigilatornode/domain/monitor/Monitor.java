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
import java.util.concurrent.ThreadPoolExecutor;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.domain.resources.ExposedResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.MonitorException;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import java.util.ArrayList;

/**
 * Monitor, schedules monitor tasks for resources and keeps track of results
 *
 * @author Patrick
 */
public class Monitor {

    private boolean prepared;
    private OutgoingClient outgoing;

    private final ThreadPoolExecutor executor;
    private final int defaultUpdateFrequency;
    private final List<MonitoredResource> resources;

    private static final System.Logger LOGGER = System.getLogger(Monitor.class.getName());

    Monitor(final List<MonitoredResource> resources, final NodeConfig config) throws MonitorException {
        if (config == null) {
            throw new MonitorException(CustomException.CONFIG_REQUIRED);
        }

        this.executor = config.getSingleThreadExecutor();
        this.defaultUpdateFrequency = config.getDefaultUpdateFrequency();
        this.resources = (resources != null ? resources : new ArrayList<>());
        prepared = false;
    }

    /**
     * Connect the monitor to an outgoing client. This is only required if the
     * monitor has exposed resources
     * <br>
     * If the monitor has exposed resources but not a outgoing client, then the
     * monitor will throw a exception
     *
     * @see ExposedResource
     * @param outgoing the outgoing client
     */
    public void connectToOutgoingClient(final OutgoingClient outgoing) {
        this.outgoing = outgoing;
    }

    /**
     * Start monitoring the resources known to this monitor
     *
     * FUTURE_WORK: When the need arises, its might be worth while to start
     * monitor tasks per X resources.Currently one task will handle the list of
     * resources. This does not mean that the monitor task will validate the
     * resources one by one. It will schedule requests to the outgoing client
     * which will run request at parallel
     *
     * @throws nl.p.it.vigilatornode.exception.MonitorException when
     * requirements for a resource type in this monitor are not met
     */
    public void start() throws MonitorException {
        LOGGER.log(INFO, "Starting monitor process");
        if (!prepared) {
            prepare();
        }

        executor.submit(new MonitorTask(resources, monitorTaskFinished()));
    }

    /**
     * @return whether the monitor is active monitoring
     */
    public boolean isActive() {
        return !executor.isShutdown();
    }

    /**
     * Stop the monitor, this gracefully ends active processes and stops
     * executing
     */
    public void stop() {
        executor.shutdown();
    }

    /**
     * @return the monitored resources, with state
     */
    public List<MonitoredResource> getResources() {
        return resources;
    }

    private Notifier monitorTaskFinished() {
        return () -> timeout();
    }

    private void timeout() {
        LOGGER.log(INFO, "Waiting before next update");

        executor.submit(new WaitTask(defaultUpdateFrequency, timeoutFinished()));
    }

    private Notifier timeoutFinished() {
        return () -> {
            try {
                start();
            } catch (MonitorException ex) {
                LOGGER.log(ERROR, "Excepting while running monitor: {0}", ex);
            }
        };
    }

    private void prepare() throws MonitorException {
        for (MonitoredResource resource : resources) {
            switch (resource) {
                case ExposedResource exposed -> {
                    if (outgoing == null) {
                        throw new MonitorException(CustomException.REQUIRMENTS_EXPOSED_RESOURCE_NOT_MET);
                    }
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

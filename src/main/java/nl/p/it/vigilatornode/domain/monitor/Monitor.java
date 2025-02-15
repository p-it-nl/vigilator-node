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
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;

/**
 * Monitor, schedules monitor tasks for resources and keeps track of results
 *
 * @author Patrick
 */
public class Monitor {

    private List<MonitoredResource> resources;
    private OutgoingClient outgoing;

    private static final System.Logger LOGGER = System.getLogger(Monitor.class.getName());

    public Monitor(final NodeConfig config) {
        outgoing = OutgoingClient.getInstance(config);
    }

    public void monitor() {

        // TODO: Move this to resource that needs it
        //MonitoredData result = outgoing.scheduleRequest(null, getAcceptor())
    }

    /* TODO: Move this to resource that needs it
    private Acceptor<MonitoredData> getAcceptor() {
        return (final MonitoredData data) -> {
            LOGGER.log(INFO, "Monitor finished request");
            
            data.label(take);
        };
    }*/
}

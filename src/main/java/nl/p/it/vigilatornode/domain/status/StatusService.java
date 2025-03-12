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
package nl.p.it.vigilatornode.domain.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nl.p.it.vigilatornode.domain.Service;
import nl.p.it.vigilatornode.domain.monitor.Monitor;
import nl.p.it.vigilatornode.domain.monitor.MonitorStore;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;
import nl.p.it.vigilatornode.domain.resources.MonitoredResourceStatus;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import static java.lang.System.Logger.Level.ERROR;

/**
 * Service for processing status updates
 *
 * @author Patrick
 */
public class StatusService implements Service {

    private final MonitorStore monitorStore;

    private static final System.Logger LOGGER = System.getLogger(StatusService.class.getName());

    public StatusService() {
        monitorStore = MonitorStore.getInstance();
    }

    @Override
    public void processRequest(
            final byte[] bytes,
            final Map<String, String> params,
            final HttpExchange exchange) throws IOException, VigilatorNodeException {
        byte[] result;
        List<Monitor> monitors = monitorStore.getMonitors();
        if (!monitors.isEmpty()) {
            List<MonitoredResourceStatus> resourceStatus = new ArrayList<>();
            for (Monitor monitor : monitors) {
                resourceStatus.addAll(monitor.getResources().stream().map(MonitoredResource::getStatus).toList());
            }
            result = asJson(resourceStatus);
        } else {
            result = new byte[0];
        }

        exchange.sendResponseHeaders(200, result.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(result);
            os.flush();
        }
    }

    private byte[] asJson(final List<MonitoredResourceStatus> resources) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsBytes(resources);
        } catch (JsonProcessingException ex) {
            LOGGER.log(ERROR, "Error while processing JSON for respone, exception: {0}", ex);
        }

        return new byte[0];
    }
}

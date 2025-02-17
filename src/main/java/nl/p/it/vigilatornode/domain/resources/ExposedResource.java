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
package nl.p.it.vigilatornode.domain.resources;

import static java.lang.System.Logger.Level.ERROR;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.exception.HttpClientException;

/**
 * The ExposedResource class is a monitored resource available via internet
 *
 * @author Patrick
 */
public class ExposedResource extends MonitoredResource {

    private OutgoingClient client;
    private String resourceMonitorEndpoint;

    private static final String CONFIG_WEB = "Web";
    private static final String KEY_URL = "url";

    private static final System.Logger LOGGER = System.getLogger(ExposedResource.class.getName());

    /**
     * Connect the exposed resource to a outgoing client. This will be used to
     * retrieve monitoring information
     *
     * @param client the outgoing client to connect to
     */
    public void connect(final OutgoingClient client) {
        this.client = client;
    }

    /**
     * Updates the status of the resource by sending requests to endpoints and
     * inferring the replied data
     *
     * @see MonitoredResource.updateStatus()
     */
    @Override
    public void updateStatus() {
        if (resourceMonitorEndpoint == null) {
            retrieveUpdateFromResource(resourceMonitorEndpoint);
        } else {
            resourceMonitorEndpoint = config.getEndpoint();
        }

        MonitoredPart webPart = parts.get(CONFIG_WEB);
        if (webPart != null) {
            String webUrl = webPart.getItems().get(KEY_URL);
            if (webUrl != null && !webUrl.isEmpty()) {
                retrieveUpdateFromResource(webUrl);
            } else {
                healthy = false;
                errors.add(Error.NO_WEB_URL);
            }
        } else {
            // resource does not require web availability checks
        }

        //finaliseUpdate();
    }

    private void retrieveUpdateFromResource(final String url) {
        try {
            client.scheduleRequest(url, getAcceptor());
        } catch (HttpClientException ex) {
            LOGGER.log(ERROR, "Excepting during request from {0} with "
                    + "exception being: {1}", getClass().getSimpleName(), ex);
            healthy = false;
            errors.add(Error.withArgs(Error.NO_RESPONE, name, url));
        }
    }

    private Acceptor<MonitoredData> getAcceptor() {
        return (final MonitoredData result) -> {
            resetValues();
            result.label(take);
            data.add(result);

            if (result.hasData()) {
                if (resourceMonitorEndpoint.equals(result.getUrl())) {
                    validateMonitorReply(result);
                } else {
                    validateWebReply(result);
                }
            } else {
                healthy = false;
                errors.add(Error.withArgs(Error.NO_RESPONE, name, result.getUrl()));
            }
        };
    }

    private void validateMonitorReply(final MonitoredData result) {

    }

    private void validateWebReply(final MonitoredData result) {

    }
}

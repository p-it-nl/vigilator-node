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

import nl.p.it.vigilatornode.domain.resources.validation.MonitorValidator;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;
import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import nl.p.it.vigilatornode.exception.HttpClientException;
import static java.lang.System.Logger.Level.ERROR;

/**
 * The ExposedResource class is a monitored resource available via internet
 *
 * @author Patrick
 */
public class ExposedResource extends MonitoredResource {

    private OutgoingClient client;
    private String resourceMonitorEndpoint;

    private final MonitorValidator monitorValidator;

    private static final String CONFIG_WEB = "Web";
    private static final String KEY_URL = "url";

    private static final System.Logger LOGGER = System.getLogger(ExposedResource.class.getName());

    public ExposedResource() {
        this.monitorValidator = new MonitorValidator();
    }

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
        resourceMonitorEndpoint = (resourceMonitorEndpoint != null ? resourceMonitorEndpoint : config.getUrl());
        if (resourceMonitorEndpoint != null) {
            retrieveUpdateFromResource(resourceMonitorEndpoint);
        }

        MonitoredPart webPart = parts.get(CONFIG_WEB);
        if (webPart != null) {
            String webUrl = webPart.getItems().get(KEY_URL);
            if (webUrl != null && !webUrl.isEmpty()) {
                retrieveUpdateFromResource(webUrl);
            } else {
                take++;
                MonitoredData result = new MonitoredData(new byte[0]);
                result.addError(Error.NO_WEB_URL);
                result.label(take);
                data.add(result);
            }
        } else {
            // resource does not require web availability checks
        }

        finaliseUpdate();
    }

    private void retrieveUpdateFromResource(final String url) {
        try {
            client.scheduleRequest(url, getAcceptor());
        } catch (HttpClientException ex) {
            LOGGER.log(ERROR, "Excepting during request from {0} with "
                    + "exception being: {1}", getClass().getSimpleName(), ex);
            take++;
            MonitoredData result = new MonitoredData(ex.getMessage().getBytes(), url);
            result.addError(Error.withArgs(Error.NO_RESPONE, name, url));
            result.label(take);
            data.add(result);
        }
    }

    private Acceptor<MonitoredData> getAcceptor() {
        return (final MonitoredData result) -> {
            take++;
            result.label(take);
            data.add(result);
            
            if (result.hasData()) {
                if (resourceMonitorEndpoint.equals(result.getUrl())) {
                    monitorValidator.validate(result, parts, name);
                } else {
                    monitorValidator.validateWebReply(result, parts, name);
                }
            } else {
                result.addError(Error.withArgs(Error.NO_RESPONE, name, result.getUrl()));
            }
        };
    }
}

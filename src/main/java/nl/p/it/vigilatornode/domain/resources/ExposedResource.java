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

import nl.p.it.vigilatornode.domain.out.OutgoingClient;

/**
 * The ExposedResource class is a monitored resource available via internet
 *
 * @author Patrick
 */
public class ExposedResource extends MonitoredResource {

    private OutgoingClient client;

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
    protected void updateStatus() {
        
    }
}

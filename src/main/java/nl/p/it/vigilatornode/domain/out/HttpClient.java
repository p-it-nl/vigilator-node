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
package nl.p.it.vigilatornode.domain.out;

import static java.lang.System.Logger.Level.INFO;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;

/**
 * Client for outgoing requests
 *
 * @author Patrick
 */
public class HttpClient {

    private final ThreadPoolExecutor executor;
    private final NodeConfig config;

    private static HttpClient instance;

    private static final System.Logger LOGGER = System.getLogger(HttpClient.class.getName());

    private HttpClient() {
        this.config = NodeConfig.getInstance();

        executor = config.getPoolExecutor();
    }

    /**
     * Returns an `singleton` instance of the http client. This will simplify
     * optimizing outgoing requests and prevent this responsibility of being
     * spread throughout the application
     *
     * @return the http client
     */
    public static synchronized HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }

        return instance;
    }

    /**
     * Start retrieving articles
     *
     * @param url the url to send request to
     * @param acceptor the method to accept the result
     */
    public void scheduleRequest(final String url, final Acceptor<MonitoredData> acceptor) {
        LOGGER.log(INFO, "Queuing request");

        executor.submit(new (url, acceptor));
    }

    /**
     * Stop the http client, this gracefully ends active processes and stops
     * executing
     */
    public void stopProcess() {
        executor.shutdown();
    }
}

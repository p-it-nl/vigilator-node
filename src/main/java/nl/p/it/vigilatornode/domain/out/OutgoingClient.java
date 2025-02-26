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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.HttpClientException;

/**
 * Client for outgoing requests
 *
 * @author Patrick
 */
public class OutgoingClient {

    private HttpClient client;
    private final NodeConfig config;
    private final HttpRequest.Builder builder;
    private final ThreadPoolExecutor executor;

    private static OutgoingClient instance;

    private static final int DEFAULT_TIMEOUT_IN_MINUTES = 1;

    private static final System.Logger LOGGER = System.getLogger(OutgoingClient.class.getName());

    private OutgoingClient(final NodeConfig config) {
        this.config = config;
        this.builder = HttpRequest.newBuilder();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .build();

        executor = config.getPoolExecutor();
    }

    /**
     * Returns an `singleton` instance of the http client.This will simplify
     * optimizing outgoing requests and prevent this responsibility of being
     * spread throughout the application
     *
     * @param config the configuration of the node
     * @return the http client
     */
    public static synchronized OutgoingClient getInstance(final NodeConfig config) {
        if (instance == null) {
            instance = new OutgoingClient(config);
        }

        return instance;
    }

    /**
     * Replace the current http client with a different one. This is useful to
     * unify http clients in other parts of the system with the outgoing client
     *
     * @param client the client
     */
    public void switchClient(final HttpClient client) {
        this.client = client;
    }

    /**
     * Start retrieving articles
     *
     * @param url the url to send request to
     * @param acceptor the method to accept the result
     * @throws nl.p.it.vigilatornode.exception.HttpClientException when issues
     * occur while sending the request
     */
    public void scheduleRequest(final String url, final Acceptor<MonitoredData> acceptor) throws HttpClientException {
        LOGGER.log(INFO, "Queuing request");

        try {
            HttpRequest request = builder.GET()
                    .uri(new URI(url))
                    .timeout(Duration.ofMinutes(DEFAULT_TIMEOUT_IN_MINUTES))
                    .build();

            executor.submit(new Request(request, acceptor, client));
        } catch (IllegalArgumentException | NullPointerException | URISyntaxException ex) {
            throw new HttpClientException(CustomException.INVALID_URL, url);
        }
    }

    /**
     * Stop the http client, this gracefully ends active processes and stops
     * executing
     */
    public void stopProcess() {
        executor.shutdown();
        client.close();
    }
}

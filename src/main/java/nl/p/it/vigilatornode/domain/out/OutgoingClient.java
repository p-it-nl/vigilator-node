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

import static java.lang.System.Logger.Level.ERROR;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
    private final HttpClient clientIgnoringTLSIssues;
    private final HttpRequest.Builder builder;
    private final ThreadPoolExecutor executor;

    private static OutgoingClient instance;

    private static final String TLS = "TLS";
    private static final int DEFAULT_TIMEOUT_IN_MINUTES = 1;
    private static final System.Logger LOGGER = System.getLogger(Request.class.getName());

    /**
     * FUTURE_WORK: Rather be using a HTTP client that allows changes after
     * creation instead of duplicating the HTTP client with a different
     * configuration added. For now it will do and its purpose is clear enough
     *
     * @param config
     */
    private OutgoingClient(final NodeConfig config) {
        this.builder = HttpRequest.newBuilder();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        this.clientIgnoringTLSIssues = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .sslContext(getByPassingContext())
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
     * @param options additional options for the request, which override default
     * behaviour @see Option
     * @throws nl.p.it.vigilatornode.exception.HttpClientException when issues
     * occur while sending the request
     */
    public void scheduleRequest(final String url, final Acceptor<MonitoredData> acceptor, Option... options) throws HttpClientException {
        try {
            HttpRequest request = builder.GET()
                    .uri(new URI(url))
                    .timeout(Duration.ofMinutes(DEFAULT_TIMEOUT_IN_MINUTES))
                    .build();

            boolean ignoreTLSIssues = false;
            if (options != null) {
                for (Option option : options) {
                    if (option != null) {
                        switch (option) {
                            case IGNORE_TLS_ISSUES -> {
                                ignoreTLSIssues = true;
                            }
                        }
                    }
                }
            }
            
            Request toSend = ignoreTLSIssues
                    ? new Request(request, acceptor, clientIgnoringTLSIssues)
                    : new Request(request, acceptor, client);
            executor.submit(toSend);
        } catch (NullPointerException ex) {
            throw new HttpClientException(CustomException.INVALID_INPUT_FOR_REQUEST, url);
        } catch (IllegalArgumentException | URISyntaxException ex) {
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

    private SSLContext getByPassingContext() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // Trust all client certificates
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // Trust all server certificates
                }
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance(TLS);
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            return sc;
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            LOGGER.log(ERROR, "Exception setting up bypass SSL context, ex: {0}", ex);
        }

        return null;
    }
}

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

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.HttpClientException;
import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;

/**
 * Request to execute
 *
 * @author Patrick
 */
public class Request implements Runnable {

    private final HttpRequest httpRequest;
    private final HttpClient client;
    private final Acceptor<MonitoredData> acceptor;

    private static final System.Logger LOGGER = System.getLogger(Request.class.getName());

    public Request(final HttpRequest request, final Acceptor<MonitoredData> acceptor, final HttpClient client) throws HttpClientException {
        if (request == null || acceptor == null || client == null) {
            throw new HttpClientException(CustomException.REQUIRED_VALUES_NOT_PROVIDED,
                    (request == null ? "request" : "")
                    + (acceptor == null ? "acceptor" : "")
                    + (client == null ? "client" : ""));
        }

        this.httpRequest = request;
        this.acceptor = acceptor;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            byte[] responseData = readResponse(
                    client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray()));
            if (responseData != null) {
                acceptor.accept(new MonitoredData(responseData, httpRequest.uri().toString()));
                return;
            } else {
                LOGGER.log(DEBUG, "Empty response received, this can happen no data was relevant for the request");
            }
        } catch (IOException ex) {
            LOGGER.log(ERROR, "Request failed with exception: {1}", ex);
        } catch (InterruptedException ex) {
            LOGGER.log(ERROR, "Request got interrupted: {1}", ex);
            Thread.currentThread().interrupt();
        }

        acceptor.accept(new MonitoredData(new byte[0], httpRequest.uri().toString()));
    }

    /**
     * FUTURE_WORK: decide later Was:
     * <p>
     * {@snippet
     * switch (response.statusCode()) {
     *  case 200, 201 -> {
     *      return response.body();
     *  }
     *  case 401, 403 -> {
     *      throw new HttpClientException(CustomException.THE_REQUEST_WAS_NOT_AUTHORIZED);
     *  }
     *  default -> {
     *      LOGGER.log(ERROR, "Unexpected response code received from url: {0} being: {1}, "
     *      + "containing message: {2}", response.uri(), response.statusCode(), new String(response.body()));
     *  }
     * }
     *
     * return new byte[0];
     * }
     *
     * But is changed because as long as there is response, we want to validate
     * it. However, the status code might still be relevant so maybe add
     * accepted status code to resource configuration or something similar.
     * Vital though to keep it simple, maybe best is to just accept all status
     * codes and allow configuring something like, statusCode: == 500 and when a
     * condition for status code exists, take the status code into account with
     * validating the responsed
     *
     * @param response the received response
     * @return the bytes
     */
    private byte[] readResponse(final HttpResponse<byte[]> response) {
        return response.body();
    }
}

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
package nl.p.it.vigilatornode.server;

import com.sun.net.httpserver.HttpExchange;//NOSONAR, com.sun is fine here
import com.sun.net.httpserver.HttpHandler;//NOSONAR, com.sun is fine here
import java.io.IOException;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.status.StatusService;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.MethodNotAllowedException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;
import static java.lang.System.Logger.Level.ERROR;

/**
 * Article handler provides the endpoint for retrieving articles
 *
 * @author Patrick
 */
public class StatusHandler implements HttpHandler {

    private final NodeConfig config;
    private final StatusService statusService;
    private final RequestHelper helper;

    private static final System.Logger LOGGER = System.getLogger(StatusHandler.class.getName());

    public StatusHandler(final NodeConfig config) {
        this.config = config;
        statusService = new StatusService();
        helper = new RequestHelper();
    }

    /**
     * Handle request
     *
     * @param exchange the incoming exchanges
     * @throws IOException potential exception while performing IO actions
     */
    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        if (exchange != null) {
            try (exchange) {
                perform(exchange);
            } catch (Exception ex) {
                LOGGER.log(ERROR, "Unexpected exception occurred, {0}", ex);
                helper.writeExceptionToExchange(null, exchange);
            }

            LOGGER.log(INFO, "Request finished");
        } else {
            LOGGER.log(WARNING, "Request received but no HttpExchange has been provided");
        }
    }

    private void perform(final HttpExchange exchange) throws IOException {
        try {
            LOGGER.log(INFO, "{0} request received with url: {1}, processing...",
                    exchange.getRequestMethod(), exchange.getRequestURI().getPath());

            helper.acceptRequest(exchange, config);

            if (helper.isPostMethod(exchange) || helper.isGetMethod(exchange)) {
                statusService.processRequest(
                        helper.readBytes(exchange),
                        helper.getParams(exchange),
                        exchange);
            } else if (!helper.isOptionsMethod(exchange)) {
                throw new MethodNotAllowedException(CustomException.UNEXPECTED_REQUEST_METHOD, exchange.getRequestMethod());
            }
        } catch (VigilatorNodeException ex) {
            helper.writeExceptionToExchange(ex, exchange);
        }
    }
}

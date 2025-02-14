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

import com.sun.net.httpserver.HttpExchange;//NOSONAR, com.sun is fine
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.RequestException;
import nl.p.it.vigilatornode.exception.ServiceUnavailableException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;

/**
 * Helper for httpserver requests
 *
 * @author Patrick
 */
public class RequestHelper {
    
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String KEY_CONTENT_TYPE = "content-type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String KEY_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String KEY_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String KEY_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String PARAM_AMP = "&";
    private static final String PARAM_IS = "=";
    private static final byte[] UNEXPECTED_EXCEPTION = "A unexpected exception has occurred, please contact the administrators if this continues to occur".getBytes();

    private static final System.Logger LOGGER = System.getLogger(RequestHelper.class.getName());

    /**
     * Performs all consistent actions on requests that will be
     * processed.Consisting of:
     * <ul>
     * <li>enable cross origin requests</li>
     * <li>set the content type on the response</li>
     * </ul>
     * 
     * FUTURE_WORK: replace JSON with something more optimal like protocol buffers
     *
     * @param exchange the 'exchange' to accept for
     * @param config configuration of the server
     * @throws IOException when sending response headers fails
     * @see enableCrossOriginRequests
     */
    public void acceptRequest(final HttpExchange exchange, final NodeConfig config) throws IOException {
        if (config != null) {
            enableCrossOriginRequests(exchange, config);
        }
        
        if(exchange != null) {        
            exchange.getResponseHeaders().add(KEY_CONTENT_TYPE, CONTENT_TYPE_JSON);
        }
    }

    /**
     * Determine if request is a GET request
     *
     * @param exchange the exchange to validate
     * @return whether the request is a get request
     */
    public boolean isGetMethod(final HttpExchange exchange) {
        if (exchange != null) {
            return METHOD_GET.equals(exchange.getRequestMethod());
        } else {
            return false;
        }
    }

    /**
     * Determine if request is a POST request
     *
     * @param exchange the exchange to validate
     * @return whether the request is a post request
     */
    public boolean isPostMethod(final HttpExchange exchange) {
        if (exchange != null) {
            return METHOD_POST.equals(exchange.getRequestMethod());
        } else {
            return false;
        }
    }

    /**
     * Determine if request is a OPTIONS request
     *
     * @param exchange the exchange to validate
     * @return whether the request is a post request
     */
    public boolean isOptionsMethod(final HttpExchange exchange) {
        if (exchange != null) {
            return METHOD_OPTIONS.equals(exchange.getRequestMethod());
        } else {
            return false;
        }
    }

    /**
     * Enable cross origin requests
     *
     * @param exchange the exchange to set cross origin requests for
     * @param config configuration of the server
     * @throws IOException when sending response headers fails
     */
    public void enableCrossOriginRequests(final HttpExchange exchange, final NodeConfig config) throws IOException {
        if (exchange != null && exchange.getResponseHeaders() != null && config != null) {
            exchange.getResponseHeaders().add(KEY_ACCESS_CONTROL_ALLOW_ORIGIN, config.getAllowedOrigins());

            if (isOptionsMethod(exchange)) {
                exchange.getResponseHeaders().add(KEY_ACCESS_CONTROL_ALLOW_METHODS, config.getAllowedMethods());
                exchange.getResponseHeaders().add(KEY_ACCESS_CONTROL_ALLOW_HEADERS, config.getAllowedHeaders());

                exchange.sendResponseHeaders(204, -1);
            }
        } else {
            LOGGER.log(DEBUG, """
                No exchange or config provided, this indicates an unexpected flow 
                of the code, verify the process leading up to this method call""");
        }
    }

    /**
     * Enable cross origin requests
     *
     * @param exchange the exchange to set cross origin requests for
     * @return the params of the request or empty
     */
    public Map<String, String> getParams(final HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        if (exchange != null && exchange.getRequestURI() != null) {
            String query = exchange.getRequestURI().getRawQuery();
            if (query != null) {
                for (String param : query.split(PARAM_AMP)) {
                    String[] pair = param.split(PARAM_IS);
                    if (pair.length > 1) {
                        params.put(pair[0], pair[1]);
                    } else {
                        params.put(pair[0], "");
                    }
                }
            }
        }

        return params;
    }

    /**
     * Write exception to the exchange
     *
     * @param ex the exception that has occured or null, when null will write
     * unexpected message
     * @see RequestHelper.UNEXPECTED_EXCEPTION
     * @param exchange the exchange to write to
     * @throws java.io.IOException when exception occurs while writing to
     * exchange
     */
    public void writeExceptionToExchange(final VigilatorNodeException ex, final HttpExchange exchange) throws IOException {
        if (ex != null && ex instanceof RequestException requestEx) {
            LOGGER.log(ERROR, "Error in request, exception: {0}", ex);

            byte[] exception = requestEx.getLocalizedMessage().getBytes();
            exchange.sendResponseHeaders(requestEx.getStatusCode(), exception.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(exception);
                os.flush();
            }
        } else {
            LOGGER.log(ERROR, "Error while processing request, exception: {0}", ex);

            exchange.sendResponseHeaders(500, UNEXPECTED_EXCEPTION.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(UNEXPECTED_EXCEPTION);
                os.flush();
            }
        }
    }

    /**
     * Read the request body as byte array from the exchange
     *
     * @param exchange the exchange to read from
     * @return the byte array
     * @throws ServiceUnavailableException when the exchange could not be read
     * from
     */
    public byte[] readBytes(final HttpExchange exchange) throws ServiceUnavailableException {
        byte[] bytes;
        try (InputStream is = exchange.getRequestBody()) {
            bytes = is.readAllBytes();
        } catch (IOException ex) {
            LOGGER.log(ERROR, "Error reading request body, being: {0}", ex);
            throw new ServiceUnavailableException(CustomException.THE_REQUEST_RESULTED_IN_IO_ISSUES);
        }

        return bytes;
    }
}

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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.UnstartableException;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Http server implementation for monitoring requests
 *
 * @author Patrick
 */
public class HttpServer {

    private com.sun.net.httpserver.HttpServer server;//NOSONAR, com.sun is fine here

    private final NodeConfig config;

    private static HttpServer instance;

    private static final System.Logger LOGGER = System.getLogger(HttpServer.class.getName());

    private HttpServer() {
        this.config = NodeConfig.getInstance();

        configure();
    }

    /**
     * Will create an instance only once and return the this same instance
     * perpetually
     *
     * @return the instance
     */
    public static synchronized HttpServer getInstance() {
        if (instance == null) {
            instance = new HttpServer();
        }

        return instance;
    }

    /**
     * Starts the server
     *
     * @throws UnstartableException when the server fails to start
     */
    public void start() throws UnstartableException {
        LOGGER.log(INFO, "Listening on port: {0}", config.getPort());

        try {
            this.server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(config.getPort()), 0);//NOSONAR, com.sun is fine here
            ThreadPoolExecutor executor = config.getPoolExecutor();
            server.setExecutor(executor);
            server.createContext("/status", new StatusHandler(config));
            server.start();
        } catch (IOException ex) {
            LOGGER.log(ERROR, "Not able to start httpserver, exception: {0}", ex);
            throw new UnstartableException(CustomException.SERVER_FAILED_TO_BOOT);
        }

        LOGGER.log(INFO, "....Vigilator node is up and running at port {0}", config.getPort());
    }

    /**
     * Stop the server gracefully
     */
    public void stop() {
        LOGGER.log(WARNING, "....Vigilator node running at port {0} is being stopped", config.getPort());
        server.stop(60);
    }

    // FUTURE_WORK: Do this somewhere else
    private void configure() {
        System.setProperty("sun.net.httpserver.maxReqTime", "60000");// 1 min
        System.setProperty("sun.net.httpserver.maxRspTime", "60000");// 1 min
        System.setProperty("sun.net.httpserver.maxIdleConnections", "100");//default = 200 (if 0 or -1 then default), setting to 100 we have a very small server
    }
}

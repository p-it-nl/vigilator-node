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
package nl.p.it.vigilatornode;

import nl.p.it.vigilatornode.server.HttpServer;
import nl.p.it.vigilatornode.configuration.LogConfig;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.resources.MonitoredResourcesReader;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.IncorrectResourceFileException;
import nl.p.it.vigilatornode.exception.UnstartableException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import static java.lang.System.Logger.Level.INFO;
import java.util.List;
import nl.p.it.vigilatornode.domain.monitor.Monitor;
import nl.p.it.vigilatornode.domain.resources.MonitoredResource;

/**
 * Initializes the application
 *
 * @author Patrick
 */
public class App {

    private static HttpServer httpServer;
    private static Monitor monitor;
    private static final String ARG_DIVIDER = "=";
    private static final System.Logger LOGGER = System.getLogger(App.class.getName());

    /**
     * @param args arguments
     * @throws VigilatorNodeException when the application fails to boot
     */
    public static void main(final String[] args) throws VigilatorNodeException {
        String environment = getEnvironment(args);
        NodeConfig config = NodeConfig.load(environment);

        LogConfig.configure(config);
        startServer(environment);
        startMonitoring(config);

        /**
         * FUTURE_WORK: since all functionality is in executors which will be
         * called by the JVM to exit anyway this might not be necessary. How
         * ever it does make the intent clear.
         */
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
    }

    private static void startServer(final String environment) throws VigilatorNodeException {
        LOGGER.log(INFO, "Starting Vigilator node with environement {0}....", environment);

        httpServer = HttpServer.getInstance();
        httpServer.start();
    }

    private static void startMonitoring(final NodeConfig config) throws IncorrectResourceFileException {
        List<MonitoredResource> resources = new MonitoredResourcesReader().read(config.getResourceFilesLocation());
        monitor = new Monitor(config, resources);
        monitor.start();
    }

    private static String getEnvironment(final String[] args) throws VigilatorNodeException {
        if (args != null && args.length > 0 && args[0] != null) {
            String[] environment = args[0].split(ARG_DIVIDER);
            return (environment != null && environment[1] != null) ? environment[1] : null;
        } else {
            throw new UnstartableException(CustomException.NO_ENVIRONMENT_SPECIFIED);
        }
    }

    static class ShutDownHook extends Thread {

        @Override
        public void run() {
        System.out.println("Shut Down Hook Called");
            monitor.stop();
            httpServer.stop();
            super.start();
        }
    }
}

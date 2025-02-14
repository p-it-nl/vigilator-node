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
import static java.lang.System.Logger.Level.INFO;
import nl.p.it.vigilatornode.configuration.LogConfig;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.resources.MonitoredResourcesReader;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.IncorrectResourceFileException;
import nl.p.it.vigilatornode.exception.UnstartableException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;

/**
 * Initializes the application
 *
 * @author Patrick
 */
public class App {

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
    }

    private static void startServer(final String environment) throws VigilatorNodeException {
        LOGGER.log(INFO, "Starting Vigilator node with environement {0}....", environment);

        HttpServer.getInstance().start();
    }

    private static void startMonitoring(final NodeConfig config) throws IncorrectResourceFileException {
        // TODO: read resource files and start monitoring        

        new MonitoredResourcesReader().read(config.getResourceFilesLocation());
    }

    private static String getEnvironment(final String[] args) throws VigilatorNodeException {
        if (args != null && args.length > 0 && args[0] != null) {
            String[] environment = args[0].split(ARG_DIVIDER);
            return (environment != null && environment[1] != null) ? environment[1] : null;
        } else {
            throw new UnstartableException(CustomException.NO_ENVIRONMENT_SPECIFIED);
        }
    }
}

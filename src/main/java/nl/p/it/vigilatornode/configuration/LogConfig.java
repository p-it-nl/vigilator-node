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
package nl.p.it.vigilatornode.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import nl.p.it.vigilatornode.exception.UnstartableException;

/**
 * Configuration for logger
 *
 * @see java.lang.System.Logger
 * @author Patrick
 */
public class LogConfig {

    private LogConfig() {
    }

    /**
     * Configures the logger without additional property files or dependencies
     *
     * @param config the configuration for this Vigilator node
     * @throws nl.p.it.vigilatornode.exception.VigilatorNodeException when
     * logger could not be configured
     */
    public static void configure(final NodeConfig config) throws VigilatorNodeException {
        try {
            System.setProperty(
                    "java.util.logging.SimpleFormatter.format",
                    "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");

            Logger logger = Logger.getLogger("");

            String logfile = config.getLogfile();
            Files.createDirectories(Paths.get(logfile.substring(0, logfile.lastIndexOf("/"))));
            FileHandler file = new FileHandler(logfile);
            file.setFormatter(new SimpleFormatter());
            file.setLevel(Level.INFO);
            logger.addHandler(file);
        } catch (IOException exception) {
            System.out.println(exception);
            throw new UnstartableException(CustomException.COULD_NOT_CONFIGURE_LOGGER);
        }
    }

}

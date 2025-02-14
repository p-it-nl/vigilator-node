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
package nl.p.it.vigilatornode.domain.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;
import java.util.ArrayList;
import java.util.List;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.IncorrectResourceFileException;
import nl.p.it.vigilatornode.exception.UnstartableException;

/**
 * Reader for resources files
 *
 * @author Patrick
 */
public class MonitoredResourcesReader {

    private static final System.Logger LOGGER = System.getLogger(MonitoredResourcesReader.class.getName());

    /**
     * Reads the files in the specified location, typically the
     * {resourceFilesLocation} in the config
     *
     * FUTURE_WORK: currently this is fail fast, a business case might be
     * created to log and discard failures and continue
     *
     * @param resourcesFilesLocation location to read from
     * @return the read files or empty
     * @throws IncorrectResourceFileException when an exception occurs while
     * reading any resource file
     */
    public List<MonitoredResource> read(final String resourcesFilesLocation) throws IncorrectResourceFileException {
        List<MonitoredResource> resources = new ArrayList<>();
        if (resourcesFilesLocation != null && !resourcesFilesLocation.isEmpty()) {

            // get resorucefile path
            // for each file: read
            // if read succesfull add resource to list
            String resourceFile = null;
            try (InputStream resourceFileStream = new FileInputStream(resourceFile)) {
                read(resourceFileStream, resources);
            } catch (IOException ex) {
                LOGGER.log(ERROR, "Not able to read resource files in {0}, exception: {1}", resourcesFilesLocation, ex);
                throw new IncorrectResourceFileException(CustomException.MISSING_APP_PROPERTIES);
            }
        } else {
            LOGGER.log(WARNING, "MonitoredResourcesReader.read() was called without an resources files location, the action will be ignored");
        }

        return resources;
    }

    private void read(final InputStream resourceFileStream, final List<MonitoredResource> resources) throws IncorrectResourceFileException {

    }
}

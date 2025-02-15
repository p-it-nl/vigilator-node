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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;
import java.util.ArrayList;
import java.util.List;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.IncorrectResourceFileException;

/**
 * Reader for resources files
 *
 * @author Patrick
 */
public class MonitoredResourcesReader {

    private MonitoredResource current;
    private String currentDecorator;
    private final List<MonitoredResource> resources;

    private static final int DEPTH_RESOURCE = 0;
    private static final int DEPTH_RESOURCE_NAME = 1;
    private static final int DEPTH_RESOURCE_PART = 2;
    private static final int DEPTH_RESOURCE_PART_ENTRY = 3;
    private static final int DEPTH_RESOURCE_PART_ITEM = 4;

    private static final String RESOURCE_EXPOSED = "ExposedResource";
    private static final String RESOURCE_ONBOARD = "OnboardResource";
    private static final String RESOURCE_INTERNAL = "InternalResource";
    private static final String DELIMITER_KEY_VALUE = ":";

    private static final int EMPTY = 0;
    private static final int TAB = 9;
    private static final int ENTER = 13;
    private static final int NEW_LINE = 10;

    private static final System.Logger LOGGER = System.getLogger(MonitoredResourcesReader.class.getName());

    public MonitoredResourcesReader() {
        resources = new ArrayList<>();
    }

    /**
     * Reads the files in the specified location, typically the
     * {resourceFilesLocation} in the config
     *
     * The function returns an immutable copy of resources read, both to prevent
     * mutations and making sure no pointer to the internal list of resources
     * exists outside the class which would stop the class from being garbage
     * collected
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
        if (resourcesFilesLocation != null && !resourcesFilesLocation.isEmpty()) {

            File directory = new File(resourcesFilesLocation);
            if (directory.exists()) {
                for (File entry : directory.listFiles()) {
                    try (InputStream resourceFileStream = new FileInputStream(entry)) {
                        read(resourceFileStream);
                    } catch (IncorrectResourceFileException ex) {
                        throw new IncorrectResourceFileException(CustomException.INVALID_RESOURCE_FILE, entry.getName(), ex.getMessage());
                    } catch (IOException ex) {
                        LOGGER.log(ERROR, "Not able to read resource files in {0}, exception: {1}", resourcesFilesLocation, ex);
                        throw new IncorrectResourceFileException(CustomException.COULD_NOT_READ_RESOURCE_FILES);
                    }
                }
            }

        } else {
            LOGGER.log(WARNING, "MonitoredResourcesReader.read() was called without an resources files location, the action will be ignored");
        }

        return List.copyOf(resources);
    }

    private void read(final InputStream resourceFileStream) throws IOException, IncorrectResourceFileException {
        int buffSize = 1000;
        byte[] buffer = new byte[buffSize];// Size matters but 1 kb is reasonble to start with, optimizing later
        int depth = 0;
        int line = 1;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            while (resourceFileStream.read(buffer) > 0) {
                for (byte b : buffer) {// FUTURE_WORK: This works with ASCII maybe not unicode?
                    if (EMPTY == b) {
                        break;
                    }

                    switch (b) {
                        case TAB ->
                            depth++;
                        case NEW_LINE -> {
                            line++;
                            if (output.size() > 1) {
                                String entry = new String(output.toByteArray());
                                referenceToResource(entry, depth);
                            } else {
                                // ignoring empty line
                            }

                            output.reset();
                            depth = 0;
                        }
                        case ENTER -> {
                            continue;
                        }
                        default -> {
                            output.write(b);
                        }
                    }
                }
            }
            if (output.size() > 0) {
                String entry = new String(output.toByteArray());
                referenceToResource(entry, depth);
            }
        }
    }

    private void referenceToResource(final String entry, final int depth) throws IncorrectResourceFileException {
        switch (depth) {
            case DEPTH_RESOURCE ->
                construct(entry);
            case DEPTH_RESOURCE_NAME -> {
                if (current == null) {
                    throw new IncorrectResourceFileException(CustomException.OUT_OF_CONTEXT_RESOURCE_NAME);
                }
                current.setName(entry);
            }
            case DEPTH_RESOURCE_PART ->
                currentDecorator = entry;
            case DEPTH_RESOURCE_PART_ENTRY ->
                decorate(entry);
            case DEPTH_RESOURCE_PART_ITEM ->
                decorate(entry);
            default ->
                throw new IncorrectResourceFileException(CustomException.TO_DEEP_TABBING);
        }
    }

    private void construct(final String type) throws IncorrectResourceFileException {
        switch (type) {
            case RESOURCE_EXPOSED ->
                current = new ExposedResource();
            case RESOURCE_ONBOARD ->
                current = new OnboardResource();
            case RESOURCE_INTERNAL ->
                current = new InternalResource();
            default ->
                throw new IncorrectResourceFileException(CustomException.UNEXPECTED_RESOURCE);
        }

        if (current != null) {
            resources.add(current);
        }
    }

    private void decorate(final String value) {
        int pos = value.indexOf(DELIMITER_KEY_VALUE);

        if (pos > 0) {
            String pairKey = value.substring(0, pos);
            int take;
            if (Character.isSpaceChar(value.charAt(pos + 1))) {
                take = pos + 2;
            } else {
                take = pos + 1;
            }
            String pairValue = value.substring(take, value.length());
            current -> decorate(currentDecorator, pairKey, pairValue);
        } else {
            current -> decorate(currentDecorator, value);
        }
    }
}

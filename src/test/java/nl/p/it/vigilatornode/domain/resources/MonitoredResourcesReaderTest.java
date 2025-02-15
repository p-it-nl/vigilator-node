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

import java.util.List;
import java.util.Set;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.IncorrectResourceFileException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;

/**
 * Tests for configuration reader
 *
 * @author Patrick
 */
public class MonitoredResourcesReaderTest {

    private static final String DOES_NOT_EXIST = "/thisdoesnotexist";
    private static final String FOLDER_EMPTY = "test-empty-folder";
    private static final String FOLDER_INCORRECT = "test-incorrect-files";
    private static final String FOLDER_CORRECT = "test-correct-files";
    private static final String PATH_TO_RESOURCES = "src/test/resources/";
    private static final String FIRST_INCORRECT_MESSAGE = "Incorrect resource file: inproperformat.conf, error: Unexpected resource type: NameOfTheResource, expected is either: ExposedResource, OnboardResource or InternalResource";
    private static final Set<String> correctNames = Set.of("ResourceOne", "ResourceTwo", "ResourceThree");

    private MonitoredResourcesReader classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new MonitoredResourcesReader();
    }

    @Test
    public void read_withoutResourcesFilesLocation() throws IncorrectResourceFileException {
        String resourcesFilesLocation = null;

        List<MonitoredResource> result = classUnderTest.read(resourcesFilesLocation);

        assertTrue(result.isEmpty());
    }

    @Test
    public void read_withEmptyResourcesFilesLocation() throws IncorrectResourceFileException {
        String resourcesFilesLocation = "";

        List<MonitoredResource> result = classUnderTest.read(resourcesFilesLocation);

        assertTrue(result.isEmpty());
    }

    @Test
    public void read_withNotExistingResourcesFilesLocation() {
        CustomException expectedException = CustomException.DIRECTORY_DOES_NOT_EXIST;
        String resourcesFilesLocation = DOES_NOT_EXIST;

        VigilatorNodeException exception = assertThrows(IncorrectResourceFileException.class, () -> {
            classUnderTest.read(resourcesFilesLocation);
        });

        assertEquals(String.format(expectedException.getMessage(), resourcesFilesLocation), exception.getMessage());
    }

    @Test
    public void read_withEmptyFolder() throws IncorrectResourceFileException {
        String resourcesFilesLocation = PATH_TO_RESOURCES + FOLDER_EMPTY;

        List<MonitoredResource> result = classUnderTest.read(resourcesFilesLocation);

        assertTrue(result.isEmpty());
    }

    @Test
    public void read_withIncorrectResourcesFiles() {// FUTURE_WORK: more incorrect tests, maybe not fail fast?
        String expectedException = FIRST_INCORRECT_MESSAGE;
        String resourcesFilesLocation = PATH_TO_RESOURCES + FOLDER_INCORRECT;

        VigilatorNodeException exception = assertThrows(IncorrectResourceFileException.class, () -> {
            classUnderTest.read(resourcesFilesLocation);
        });

        assertEquals(expectedException, exception.getMessage());
    }

    @Test
    public void read_withCorrectResourcesFiles() throws IncorrectResourceFileException {
        int expectedSize = 3;
        String resourcesFilesLocation = PATH_TO_RESOURCES + FOLDER_CORRECT;

        List<MonitoredResource> result = classUnderTest.read(resourcesFilesLocation);

        assertFalse(result.isEmpty());
        assertEquals(expectedSize, result.size());

        for (MonitoredResource resource : result) {
            assertTrue(correctNames.contains(resource.getName()));
            assertNotNull(resource.getConfig());
        }
    }

}

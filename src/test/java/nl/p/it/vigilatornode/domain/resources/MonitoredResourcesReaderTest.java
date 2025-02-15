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
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.IncorrectResourceFileException;
import nl.p.it.vigilatornode.exception.UnstartableException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        CustomException expectedException = CustomException.COULD_NOT_READ_RESOURCE_FILES;
        String resourcesFilesLocation = DOES_NOT_EXIST;

        VigilatorNodeException exception = assertThrows(IncorrectResourceFileException.class, () -> {
            classUnderTest.read(resourcesFilesLocation);
        });

        assertEquals(expectedException.getMessage(), exception.getMessage());
    }

    @Test
    public void read_withEmptyFolder() throws IncorrectResourceFileException {
        // TODO: read from resources folder
        String resourcesFilesLocation = FOLDER_EMPTY;

        List<MonitoredResource> result = classUnderTest.read(resourcesFilesLocation);

        assertTrue(result.isEmpty());
    }

    @Test
    public void read_withIncorrectResourcesFiles() {
        CustomException expectedException = CustomException.INVALID_RESOURCE_FILE;
        String resourcesFilesLocation = FOLDER_INCORRECT;

        VigilatorNodeException exception = assertThrows(IncorrectResourceFileException.class, () -> {
            classUnderTest.read(resourcesFilesLocation);
        });

        assertEquals(expectedException.getMessage(), exception.getMessage());
    }

    @Test
    public void read_withCorrectResourcesFiles() throws IncorrectResourceFileException {
        int expectedSize = 1;
        String resourcesFilesLocation = PATH_TO_RESOURCES + FOLDER_CORRECT;

        List<MonitoredResource> result = classUnderTest.read(resourcesFilesLocation);
        
        assertTrue(result.isEmpty());
        assertEquals(expectedSize, result.size());
        
        // TODO: validate more
    }

}

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for monitor validator
 *
 * @author Patrick
 */
public class MonitorValidatorTest {

    private MonitorValidator classUnderTest;

    private static final String NAME = "mock";
    private static final String PART_NAME = "mock";

    private static final String ITEM_ONE_KEY = "status";
    private static final String ITEM_ONE_CONDITION = "!ACTIVE";
    private static final String ITEM_ONE_CORRECT = "ACTIVE";
    private static final String ITEM_ONE_INCORRECT = "NOT_ACTIVE";
    private static final String ITEM_TWO_KEY = "pool size";
    private static final String ITEM_TWO_CONDITION = "> 50";
    private static final String ITEM_TWO_CORRECT = "10";
    private static final String ITEM_TWO_INCORRECT = "51";
    private static final String ITEM_DATETIME_KEY = "datetime";
    private static final String ITEM_DATETIME_CONDITION = "< 5min";
    private static final long ITEM_DATETIME_INCORRECT = 1739957108; // 19/02/2025 10:25

    private static final String RESPONSE_WITH_EMPTY_JSON_OBJECT = "{}";
    private static final String RESPONSE_WITH_ONE_STATUS_COMPONENT = """
        {
            "environment": "prod",
            "status": [
                {
                    "name": "HttpServer",
                    "items": {
                        "threads active": "1",
                        "status": "%s",
                        "threads completed": "82388",
                        "maximum pool size": "100",
                        "threads queued": "0",
                        "pool size": "%s"
                    },
                    "datetime": "%s"
                }
            ]
        }""";

    @BeforeEach
    public void setUp() {
        classUnderTest = new MonitorValidator();
    }

    @Test
    public void testValidateWithoutValues() {
        MonitoredData result = null;
        Map<String, MonitoredPart> parts = null;
        String name = null;

        assertDoesNotThrow(() -> classUnderTest.validate(result, parts, name));
    }

    @Test
    public void testValidateWithoutResult() {
        MonitoredData result = null;
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        assertDoesNotThrow(() -> classUnderTest.validate(result, parts, name));
    }

    @Test
    public void testValidateWithEmptyResult() {
        String expected = Error.EMPTY_RESPONSE.formatted(NAME, null);
        MonitoredData result = new MonitoredData(new byte[0]);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);
        
        assertFalse(result.isHealthy());
        List<String> errors = result.getErrors();
        assertFalse(errors.isEmpty());
        assertTrue(result.getWarnings().isEmpty());
        assertTrue(errors.contains(expected));
    }

    @Test
    public void testValidateWithEmptyJSONResult() {
        String expected = Error.EMPTY_RESPONSE.formatted(NAME, null);
        MonitoredData result = new MonitoredData(RESPONSE_WITH_EMPTY_JSON_OBJECT.getBytes());
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);
        System.out.println(errors);
        assertFalse(result.isHealthy());
        List<String> errors = result.getErrors();
        assertFalse(errors.isEmpty());
        assertTrue(result.getWarnings().isEmpty());
        assertTrue(errors.contains(expected));
    }
    
    @Test
    public void testValidateWithoutParts() {
        MonitoredData result = new MonitoredData(getResponseWith(
                RESPONSE_WITH_ONE_STATUS_COMPONENT, true, true, true));
        Map<String, MonitoredPart> parts = null;
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWithoutName() {
        MonitoredData result = new MonitoredData(getResponseWith(
                RESPONSE_WITH_ONE_STATUS_COMPONENT, true, true, true));
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = null;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    private byte[] getResponseWith(final String base, final boolean correctItemOne, final boolean correctItemTwo, final boolean correctDatetimeValue) {
        String itemOne = (correctItemOne ? ITEM_ONE_CORRECT : ITEM_ONE_INCORRECT);
        String itemTwo = (correctItemTwo ? ITEM_TWO_CORRECT : ITEM_TWO_INCORRECT);
        long datetime = (correctDatetimeValue ? System.currentTimeMillis() : ITEM_DATETIME_INCORRECT);

        return base.formatted(itemOne, itemTwo, datetime).getBytes();
    }

    private Map<String, MonitoredPart> getPartsWith(final boolean itemOne, final boolean itemTwo, final boolean datetimeValue) {
        Map<String, MonitoredPart> parts = new HashMap<>();
        MonitoredPart part = new MonitoredPart();
        if (itemOne) {
            part.addItem(ITEM_ONE_KEY, ITEM_ONE_CONDITION);
            parts.put(PART_NAME, part);
        }
        if (itemTwo) {
            part = new MonitoredPart();
            part.addItem(ITEM_TWO_KEY, ITEM_TWO_CONDITION);
            parts.put(PART_NAME, part);
        }
        if (datetimeValue) {
            part = new MonitoredPart();
            part.addItem(ITEM_DATETIME_KEY, ITEM_DATETIME_CONDITION);
            parts.put(PART_NAME, part);
        }

        return parts;
    }
}

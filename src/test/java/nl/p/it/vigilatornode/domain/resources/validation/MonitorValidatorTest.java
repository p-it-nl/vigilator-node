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
package nl.p.it.vigilatornode.domain.resources.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.resources.Error;
import nl.p.it.vigilatornode.domain.resources.MonitoredPart;
import nl.p.it.vigilatornode.domain.resources.Warning;
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
    private static final String PART_NAME = "HttpServer";
    private static final String PART_URL = "mock";
    private static final String KEY_JSON_NAME = "name";
    private static final String KEY_JSON_ITEMS = "items";
    private static final String KEY_JSON_DATETIME = "datetime";

    private static final String ITEM_ONE_KEY = "status";
    private static final String ITEM_TWO_KEY = "pool size";
    private static final String ITEM_DATETIME_KEY = "datetime";
    private static final String ITEM_CONDITION_ONE = "== ACTIVE";
    private static final String ITEM_CONDITION_TWO = "== 10";
    private static final String ITEM_CONDITION_DATETIME = "< 5min";

    private static final String RESPONSE_WITH_EMPTY_JSON_OBJECT = "{}";
    private static final String RESPONSE_WITH_EMPTY_STATUS = """
        {
            "environment": "prod",
            "status": []
        }""";
    private static final String RESPONSE_WITH_STATUS_NOT_HAVING_NAME = """
        {
            "environment": "prod",
            "status": [
                {
                    "mock": "HttpServer",
                    "items": {
                        "threads active": "1",
                        "status": "ACTIVE"
                    },
                    "datetime": "1739957108"
                }
            ]
        }""";
    private static final String RESPONSE_WITH_STATUS_NOT_HAVING_ITEMS = """
        {
            "environment": "prod",
            "status": [
                {
                    "name": "HttpServer",
                    "mock": {
                        "threads active": "1",
                        "status": "ACTIVE"
                    },
                    "datetime": "1739957108"
                }
            ]
        }""";
    private static final String RESPONSE_WITH_STATUS_NOT_HAVING_DATETIME = """
        {
            "environment": "prod",
            "status": [
                {
                    "name": "HttpServer",
                    "items": {
                        "threads active": "1",
                        "status": "ACTIVE"
                    },
                    "mock": "1739957108"
                }
            ]
        }""";
    private static final String RESPONSE_WITH_ONE_STATUS_COMPONENT = """
        {
            "environment": "prod",
            "status": [
                {
                    "name": "HttpServer",
                    "items": {
                        "threads active": "1",
                        "status": "ACTIVE",
                        "threads completed": "82388",
                        "maximum pool size": "100",
                        "threads queued": "0",
                        "pool size": "10"
                    },
                    "datetime": "1739957108"
                }
            ]
        }""";
    private static final String RESPONSE_WITH_STATUS_COMPONENTS = """
        {
            "environment": "prod",
            "status": [
                {
                    "name": "HttpServer",
                    "items": {
                        "threads active": "1",
                        "status": "ACTIVE",
                        "threads completed": "82388",
                        "maximum pool size": "100",
                        "threads queued": "0",
                        "pool size": "10"
                    },
                    "datetime": "1739957108"
                },
                {
                    "name": "HttpServer",
                    "items": {
                        "threads active": "0",
                        "threads completed": "0",
                        "maximum pool size": "1",
                        "threads queued": "0",
                        "status": "ACTIVE",
                        "pool size": "0"
                    },
                    "datetime": "1739957108"
                }
            ]
        }""";
    private static final String RESPONSE_WITH_STATUS_COMPONENT_FAILING_CONDITIONS = """
        {
            "environment": "prod",
            "status": [
                {
                    "name": "HttpServer",
                    "items": {
                        "threads active": "1",
                        "status": "ACTIVE",
                        "threads completed": "82388",
                        "maximum pool size": "100",
                        "threads queued": "0",
                        "pool size": "10"
                    },
                    "datetime": "1739957108"
                }
            ]
        }""";
    private static final String JSON_EXCEPTION_STATUS_NOT_FOUND = "JSONObject[\"status\"] not found.";

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
    public void testValidateWithEmptyJSON() {
        String expected = Error.NOT_VALID_JSON.formatted(NAME, JSON_EXCEPTION_STATUS_NOT_FOUND);
        MonitoredData result = getResultWith(RESPONSE_WITH_EMPTY_JSON_OBJECT);
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
    public void testValidateWithJSONContainingEmptyStatus() {
        String expected = Error.EMPTY_STATUS.formatted(NAME, PART_URL);
        MonitoredData result = getResultWith(RESPONSE_WITH_EMPTY_STATUS);
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
    public void testValidateWithJSONStatusObjectNotHavingNameAttribute() {
        String expected = Warning.STATUS_MISSING_FIELD.formatted(NAME, KEY_JSON_NAME);
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_NOT_HAVING_NAME);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getErrors().isEmpty());
        List<String> warnings = result.getWarnings();
        assertFalse(warnings.isEmpty());
        assertTrue(warnings.contains(expected));
    }

    @Test
    public void testValidateWithJSONStatusObjectNotHavingItemsAttribute() {
        String expected = Warning.STATUS_MISSING_FIELD.formatted(NAME, KEY_JSON_ITEMS);
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_NOT_HAVING_ITEMS);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getErrors().isEmpty());
        List<String> warnings = result.getWarnings();
        assertFalse(warnings.isEmpty());
        assertTrue(warnings.contains(expected));
    }

    @Test
    public void testValidateWithJSONStatusObjectNotHavingDatetimeAttribute() {
        String expected = Warning.STATUS_MISSING_FIELD.formatted(NAME, KEY_JSON_DATETIME);
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_NOT_HAVING_DATETIME);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getErrors().isEmpty());
        List<String> warnings = result.getWarnings();
        assertFalse(warnings.isEmpty());
        assertTrue(warnings.contains(expected));
    }

    @Test
    public void testValidateWithValidJSON() {
        MonitoredData result = getResultWith(RESPONSE_WITH_ONE_STATUS_COMPONENT);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWithJSONHavingTwoStatusComponents() {
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_COMPONENTS);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWithConditionsFailing() {
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_COMPONENT_FAILING_CONDITIONS);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertFalse(result.isHealthy());
        System.out.println(result.getErrors());
        
        // TODO: validate errors
    }

    @Test
    public void testValidateWithoutParts() {
        MonitoredData result = getResultWith(RESPONSE_WITH_ONE_STATUS_COMPONENT);
        Map<String, MonitoredPart> parts = null;
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWithoutName() {
        MonitoredData result = getResultWith(RESPONSE_WITH_ONE_STATUS_COMPONENT);
        Map<String, MonitoredPart> parts = getPartsWith(true, true, true);
        String name = null;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    private MonitoredData getResultWith(final String data) {
        MonitoredData monitoredData = new MonitoredData(data.getBytes());
        monitoredData.url(PART_URL);

        return monitoredData;
    }

    private Map<String, MonitoredPart> getPartsWith(final boolean itemOne, final boolean itemTwo, final boolean datetimeValue) {
        Map<String, MonitoredPart> parts = new HashMap<>();
        MonitoredPart part = new MonitoredPart();
        if (itemOne) {
            part.addItem(ITEM_ONE_KEY, ITEM_CONDITION_ONE);
        }
        if (itemTwo) {
            part.addItem(ITEM_TWO_KEY, ITEM_CONDITION_TWO);
        }
        if (datetimeValue) {
            part.addItem(ITEM_DATETIME_KEY, ITEM_CONDITION_DATETIME);
        }
        parts.put(PART_NAME, part);
        
        return parts;
    }
}

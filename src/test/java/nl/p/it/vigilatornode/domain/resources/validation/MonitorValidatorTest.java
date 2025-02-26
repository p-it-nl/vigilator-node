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
    private static final String KEY_CONFIG_WEB = "Web";
    private static final String KEY_CONFIG_WEB_TITLE_KEY = "title";
    private static final String KEY_CONFIG_WEB_TITLE_VALUE = "mock";

    private static final String ITEM_ONE_KEY = "status";
    private static final String ITEM_TWO_KEY = "pool size";
    private static final String ITEM_WARNING_KEY = "threads queued";
    private static final String ITEM_DATETIME_KEY = "datetime";
    private static final String ITEM_CONDITION_ONE = "!ACTIVE";
    private static final String ITEM_CONDITION_TWO = "> 50";
    private static final String ITEM_CONDITION_WARNING = "> 10 W";
    private static final String ITEM_CONDITION_DATETIME = "< 5min";
    private static final String INDICATION_DATETIME_CONDITION_FAILED
            = "Received update data exceeds specified time constraints in object";
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
                    "datetime": "%s"
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
                    "datetime": "%s"
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
                    "datetime": "%s"
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
                    "datetime": "%s"
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
                        "status": "NOT_ACTIVE",
                        "threads completed": "82388",
                        "maximum pool size": "100",
                        "threads queued": "11",
                        "pool size": "51"
                    },
                    "datetime": "1739957108"
                }
            ]
        }""";
    private static final String JSON_EXCEPTION_STATUS_NOT_FOUND = "JSONObject[\"status\"] not found.";
    private static final String RESPONSE_WEB_REPLY_VALID = "<html><head><title>mock</title></head><body></body></html>";
    private static final String RESPONSE_WEB_REPLY_INVALID = "<html><head><title>somethingelse</title></head><body></body></html>";

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
        Map<String, MonitoredPart> parts = getItemParts();
        String name = NAME;

        assertDoesNotThrow(() -> classUnderTest.validate(result, parts, name));
    }

    @Test
    public void testValidateWithEmptyResult() {
        String expected = Error.EMPTY_RESPONSE.formatted(NAME, null);
        MonitoredData result = new MonitoredData(new byte[0]);
        Map<String, MonitoredPart> parts = getItemParts();
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
        Map<String, MonitoredPart> parts = getItemParts();
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
        Map<String, MonitoredPart> parts = getItemParts();
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
        Map<String, MonitoredPart> parts = getItemParts();
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
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_NOT_HAVING_ITEMS.formatted(System.currentTimeMillis()));
        Map<String, MonitoredPart> parts = getItemParts();
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
        Map<String, MonitoredPart> parts = getItemParts();
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
        MonitoredData result = getResultWith(RESPONSE_WITH_ONE_STATUS_COMPONENT.formatted(System.currentTimeMillis()));
        Map<String, MonitoredPart> parts = getItemParts();
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWithJSONHavingTwoStatusComponents() {
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_COMPONENTS.formatted(System.currentTimeMillis(), System.currentTimeMillis()));
        Map<String, MonitoredPart> parts = getItemParts();
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWithConditionsFailing() {
        MonitoredData result = getResultWith(RESPONSE_WITH_STATUS_COMPONENT_FAILING_CONDITIONS);
        Map<String, MonitoredPart> parts = getItemParts();
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertFalse(result.isHealthy());
        List<String> errors = result.getErrors();
        assertTrue(errors.get(0).contains(ITEM_CONDITION_ONE));
        assertTrue(errors.get(1).contains(ITEM_CONDITION_TWO));
        assertTrue(errors.get(2).contains(INDICATION_DATETIME_CONDITION_FAILED));
        assertTrue(result.getWarnings().get(0).contains(ITEM_CONDITION_WARNING));
    }

    @Test
    public void testValidateWithoutParts() {
        MonitoredData result = getResultWith(RESPONSE_WITH_ONE_STATUS_COMPONENT.formatted(System.currentTimeMillis()));
        Map<String, MonitoredPart> parts = null;
        String name = NAME;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWithoutName() {
        MonitoredData result = getResultWith(RESPONSE_WITH_ONE_STATUS_COMPONENT.formatted(System.currentTimeMillis()));
        Map<String, MonitoredPart> parts = getItemParts();
        String name = null;

        classUnderTest.validate(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWebReplyWithoutValues() {
        MonitoredData result = null;
        Map<String, MonitoredPart> parts = null;
        String name = null;

        assertDoesNotThrow(() -> classUnderTest.validateWebReply(result, parts, name));
    }

    @Test
    public void testValidateWebReplyWithoutResult() {
        MonitoredData result = null;
        Map<String, MonitoredPart> parts = getPartsWithWeb(true);
        String name = NAME;

        assertDoesNotThrow(() -> classUnderTest.validateWebReply(result, parts, name));
    }

    @Test
    public void testValidateWebReplyWithEmptyResult() {
        String expected = Error.WEB_VALIDATION_EMPTY.formatted(PART_URL);
        MonitoredData result = new MonitoredData(new byte[0]);
        result.url(PART_URL);
        Map<String, MonitoredPart> parts = getPartsWithWeb(true);
        String name = NAME;

        classUnderTest.validateWebReply(result, parts, name);

        assertFalse(result.isHealthy());
        assertTrue(result.getErrors().contains(expected));
    }

    @Test
    public void testValidateWebReplyWithoutParts() {
        MonitoredData result = new MonitoredData(RESPONSE_WEB_REPLY_VALID.getBytes());
        Map<String, MonitoredPart> parts = null;
        String name = NAME;

        classUnderTest.validateWebReply(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWebReplyWithoutName() {
        MonitoredData result = new MonitoredData(RESPONSE_WEB_REPLY_VALID.getBytes());
        Map<String, MonitoredPart> parts = getPartsWithWeb(true);
        String name = null;

        classUnderTest.validateWebReply(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWebReplyWithValidResponse() {
        MonitoredData result = new MonitoredData(RESPONSE_WEB_REPLY_VALID.getBytes());
        Map<String, MonitoredPart> parts = getPartsWithWeb(true);
        String name = NAME;

        classUnderTest.validateWebReply(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void testValidateWebReplyWithValidResponseMissingTitleCondition() {
        String expected = Warning.WEB_VALIDATION_MISSING_TITLE.formatted(PART_URL);
        MonitoredData result = new MonitoredData(RESPONSE_WEB_REPLY_VALID.getBytes());
        result.url(PART_URL);
        Map<String, MonitoredPart> parts = getPartsWithWeb(false);
        String name = NAME;

        classUnderTest.validateWebReply(result, parts, name);

        assertTrue(result.isHealthy());
        assertTrue(result.getWarnings().contains(expected));
    }

    @Test
    public void testValidateWebReplyWithInvalidResponse() {
        String expected = Error.WEB_VALIDATION_FAILED.formatted(PART_URL);
        MonitoredData result = new MonitoredData(RESPONSE_WEB_REPLY_INVALID.getBytes());
        result.url(PART_URL);
        Map<String, MonitoredPart> parts = getPartsWithWeb(true);
        String name = NAME;

        classUnderTest.validateWebReply(result, parts, name);

        assertFalse(result.isHealthy());
        assertTrue(result.getErrors().get(0).contains(expected));
    }

    private MonitoredData getResultWith(final String data) {
        MonitoredData monitoredData = new MonitoredData(data.getBytes());
        monitoredData.url(PART_URL);

        return monitoredData;
    }

    private Map<String, MonitoredPart> getItemParts() {
        Map<String, MonitoredPart> parts = new HashMap<>();
        MonitoredPart part = new MonitoredPart();
        part.addItem(ITEM_ONE_KEY, ITEM_CONDITION_ONE);
        part.addItem(ITEM_TWO_KEY, ITEM_CONDITION_TWO);
        part.addItem(ITEM_WARNING_KEY, ITEM_CONDITION_WARNING);
        part.addItem(ITEM_DATETIME_KEY, ITEM_CONDITION_DATETIME);
        parts.put(PART_NAME, part);

        return parts;
    }

    private Map<String, MonitoredPart> getPartsWithWeb(final boolean withTitleCondition) {
        Map<String, MonitoredPart> parts = new HashMap<>();
        MonitoredPart part = new MonitoredPart();
        if (withTitleCondition) {
            part.addItem(KEY_CONFIG_WEB_TITLE_KEY, KEY_CONFIG_WEB_TITLE_VALUE);
        }
        parts.put(KEY_CONFIG_WEB, part);

        return parts;
    }
}

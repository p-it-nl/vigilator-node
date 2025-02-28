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
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 * General tests for monitored resource
 *
 * @author Patrick
 */
public class MonitoredResourceTest {

    private MonitoredResource classUnderTest;

    private static final String DECORATOR = "mock";
    private static final String KEY = "mock";
    private static final String VALUE = "mock";
    private static final String NAME = "mock";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_ENDPOINT = "url";
    private static final String ACTIVE = "true";
    private static final String ERROR = "mock";

    @BeforeEach
    public void setUp() {
        classUnderTest = new TestResource();
    }

    @Test
    public void decorate_withoutValues() {
        String decorator = null;
        String value = null;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void decorate_withoutDecorator() {
        String decorator = null;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void decorate_withoutValue() {
        String decorator = DECORATOR;
        String value = null;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertFalse(classUnderTest.parts.isEmpty());
        assertTrue(classUnderTest.parts.get(DECORATOR).getItems().isEmpty());
    }

    @Test
    public void decorate_withValues_expectingMonitoredPartWithItem() {
        String decorator = DECORATOR;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertFalse(classUnderTest.parts.isEmpty());
        MonitoredPart created = classUnderTest.parts.get(DECORATOR);
        assertFalse(created.getItems().isEmpty());
        assertEquals(value, created.getItems().get(value));
    }

    @Test
    public void decorate_3args_withoutValues() {
        String decorator = null;
        String key = null;
        String value = null;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void decorate_3args_withoutDecorator() {
        String decorator = null;
        String key = KEY;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void decorate_3args_withoutKey() {
        String decorator = DECORATOR;
        String key = null;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertFalse(classUnderTest.parts.isEmpty());
        assertTrue(classUnderTest.parts.get(DECORATOR).getItems().isEmpty());
    }

    @Test
    public void decorate_3args_withoutValue() {
        String decorator = DECORATOR;
        String key = KEY;
        String value = null;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertFalse(classUnderTest.parts.isEmpty());
        MonitoredPart created = classUnderTest.parts.get(DECORATOR);
        assertFalse(created.getItems().isEmpty());
        assertTrue(created.getItems().get(KEY) == null);
    }

    @Test
    public void decorate_3args_withValues_expectingMonitoredPartWithItemContainingKeyAndValue() {
        String decorator = DECORATOR;
        String key = KEY;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertFalse(classUnderTest.parts.isEmpty());
        MonitoredPart created = classUnderTest.parts.get(DECORATOR);
        assertFalse(created.getItems().isEmpty());
        assertEquals(VALUE, created.getItems().get(KEY));
    }

    @Test
    public void getName_notHavingName() {
        String result = classUnderTest.getName();

        assertNull(result);
    }

    @Test
    public void getName_havingEmptyName() {
        String name = "";

        classUnderTest.setName(name);
        String result = classUnderTest.getName();

        assertTrue(result.isEmpty());
    }

    @Test
    public void getName_havingName() {
        String name = NAME;

        classUnderTest.setName(name);
        String result = classUnderTest.getName();

        assertEquals(NAME, result);
    }

    @Test
    public void getConfig_notHavingSetConfig_expectingDefault() {
        MonitoredResourceConfig result = classUnderTest.getConfig();

        assertNotNull(result);
        assertFalse(result.isActive());
        assertTrue(result.getEndpoint() == null);
    }

    @Test
    public void getConfig_havingConfigSetToActiveButNoUrl() {
        boolean expected = true;
        String active = ACTIVE;

        classUnderTest.decorate(MonitoredResourceConfig.TYPE, KEY_ACTIVE, active);
        MonitoredResourceConfig result = classUnderTest.getConfig();

        assertNotNull(result);
        assertEquals(expected, result.isActive());
        assertTrue(result.getEndpoint() == null);
    }

    @Test
    public void getConfig_havingConfigSetToActiveWithUrl() {
        boolean expected = true;
        String expectedUrl = VALUE;
        String active = ACTIVE;
        String url = VALUE;

        classUnderTest.decorate(MonitoredResourceConfig.TYPE, KEY_ACTIVE, active);
        classUnderTest.decorate(MonitoredResourceConfig.TYPE, KEY_ENDPOINT, url);
        MonitoredResourceConfig result = classUnderTest.getConfig();

        assertNotNull(result);
        assertEquals(expected, result.isActive());
        assertEquals(expectedUrl, result.getEndpoint());
    }

    @Test
    public void getData_notHavingAny_expectingDefault() {
        List<MonitoredData> result = classUnderTest.getData();

        assertTrue(result.isEmpty());
    }

    @Test
    public void getData_havingOneEntry() {
        classUnderTest.data.add(new MonitoredData(new byte[0]));

        List<MonitoredData> result = classUnderTest.getData();

        assertFalse(result.isEmpty());
    }

    @Test
    public void isHealthy_withoutValues_expectingDefault() {
        boolean expected = false;

        boolean result = classUnderTest.isHealthy();

        assertEquals(expected, result);
    }

    @Test
    public void isHealthy_withLastMonitoredDataHealthy_expectingTrue() {
        boolean expected = true;

        classUnderTest.data.add(new MonitoredData(new byte[0]));
        boolean result = classUnderTest.isHealthy();

        assertEquals(expected, result);
    }

    @Test
    public void isHealthy_withLastMonitoredDataBeingUnhealthy_expectingFalse() {
        boolean expected = false;

        MonitoredData data = new MonitoredData(new byte[0]);
        data.addError(ERROR);
        classUnderTest.data.add(data);
        boolean result = classUnderTest.isHealthy();

        assertEquals(expected, result);
    }

    public class TestResource extends MonitoredResource {

        public void updateStatus() {
            // nothing to do for the test
        }
    }

}

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

    @BeforeEach
    public void MonitoredResourceTest() {
        classUnderTest = new TestResource();
    }

    @Test
    public void testDecorate_withoutValues() {
        String decorator = null;
        String value = null;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void testDecorate_withoutDecorator() {
        String decorator = null;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void testDecorate_withoutValue() {
        String decorator = DECORATOR;
        String value = null;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertFalse(classUnderTest.parts.isEmpty());
        assertTrue(classUnderTest.parts.get(DECORATOR).getItems().isEmpty());
    }

    @Test
    public void testDecorate_withValues_expectingMonitoredPartWithItem() {
        String decorator = DECORATOR;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, value));

        assertFalse(classUnderTest.parts.isEmpty());
        MonitoredPart created = classUnderTest.parts.get(DECORATOR);
        assertFalse(created.getItems().isEmpty());
        assertTrue(created.getItems().get(value).equals(value));
    }

    @Test
    public void testDecorate_3args_withoutValues() {
        String decorator = null;
        String key = null;
        String value = null;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void testDecorate_3args_withoutDecorator() {
        String decorator = null;
        String key = KEY;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertTrue(classUnderTest.parts.isEmpty());
    }

    @Test
    public void testDecorate_3args_withoutKey() {
        String decorator = DECORATOR;
        String key = null;
        String value = VALUE;

        assertDoesNotThrow(() -> classUnderTest.decorate(decorator, key, value));

        assertFalse(classUnderTest.parts.isEmpty());
        assertTrue(classUnderTest.parts.get(DECORATOR).getItems().isEmpty());
    }

    @Test
    public void testDecorate_3args_withoutValue() {
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
    public void testGetName() {
    }

    @Test
    public void testSetName() {
    }

    @Test
    public void testGetConfig() {
    }

    @Test
    public void testGetData() {
    }

    @Test
    public void testUpdateStatus() {
    }

    @Test
    public void testToString() {
    }

    public class TestResource extends MonitoredResource {

        public void updateStatus() {
        }
    }

}

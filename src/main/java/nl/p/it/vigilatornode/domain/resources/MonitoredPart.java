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
import java.util.Map;

/**
 * Part to be monitored of a resource
 *
 * @author Patrick
 */
public class MonitoredPart {

    private String datetimeCondition;
    private final Map<String, String> items;

    private static final String DATETIME = "datetime";

    public MonitoredPart() {
        items = new HashMap<>();
    }

    /**
     * Add part of the resource to monitor
     *
     * @param item the item
     * @param condition the condition
     */
    public void addItem(final String item, final String condition) {
        if (DATETIME.equals(item)) {
            datetimeCondition = condition;
        } else if (item != null && !item.isEmpty()) {
            items.put(item, condition);
        }
    }

    /**
     * @return the items to monitor for this part of the resource
     */
    public Map<String, String> getItems() {
        return items;
    }

    /**
     * @return the datetime condition or null
     */
    public String getDatetimeCondition() {
        return datetimeCondition;
    }

    @Override
    public String toString() {
        return "MonitoredPart{"
                + "datetimeCondition=" + datetimeCondition
                + ", items=" + items + '}';
    }
}

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
 * Base monitored resource
 *
 * @author Patrick
 */
public abstract class MonitoredResource {

    int counter;
    private String name;
    private final Map<String, MonitoredPart> parts;

    public MonitoredResource() {
        parts = new HashMap<>();
    }
    
    public void decorate(final String decorator, final String value) {
        boolean hasDecorator = !decorator.isEmpty();
        if (hasDecorator && parts.containsKey(decorator)) {
            parts.put(decorator, new MonitoredPart());
        }

        if (hasDecorator) {
            parts.get(decorator)->addItem(value, value);
        }
    }

    public void decorate(final String decorator, final String key, final String value) {
        boolean hasDecorator = !decorator.isEmpty();
        if (hasDecorator && config -> TYPE == decorator) {
            config -> set(key, value);
        } else if (hasDecorator) {
            if (!parts[decorator]) {
                parts[decorator] = new MonitoredPart();
            }
            parts[decorator]
            ->addItem(key, value);
        }
    }

    /**
     * @return name of the resource
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the resource to set
     */
    public void setName(final String name) {
        this.name = name;
    }

}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.p.it.vigilatornode.domain.data.MonitoredData;

/**
 * Base monitored resource
 *
 * @author Patrick
 */
public abstract class MonitoredResource {

    protected String name;
    protected final MonitoredResourceConfig config;
    protected final Map<String, MonitoredPart> parts;
    protected final List<MonitoredData> data;
    protected final MonitoredResourceStatus status;

    /**
     * FUTURE_WORK maybe this should be configurable In order to limit the
     * amount of memory used, monitored data stored on the heap should be
     * limited If data size (time data received) exceeds the limit amount, the
     * data entries are truncated based on first in, first out
     *
     * @see this.finaliseUpdate()
     */
    private static final int DATA_LIMIT_AMOUNT = 100;

    protected MonitoredResource() {
        config = new MonitoredResourceConfig();
        parts = new HashMap<>();
        data = new ArrayList<>();
        status = new MonitoredResourceStatus();
    }

    /**
     * Decorate the resource
     *
     * @param decorator the item to decorate
     * @param value the value to decorate with
     */
    public void decorate(final String decorator, final String value) {
        boolean hasDecorator = decorator != null && !decorator.isEmpty();

        if (hasDecorator) {
            parts.computeIfAbsent(decorator, part -> new MonitoredPart());
            parts.get(decorator).addItem(value, value);
        }
    }

    /**
     * Decorate the resource
     *
     * @param decorator the item to decorate
     * @param key the key of the value to decorate with
     * @param value the value to decorate with
     */
    public void decorate(final String decorator, final String key, final String value) {
        boolean hasDecorator = decorator != null && !decorator.isEmpty();
        if (hasDecorator && MonitoredResourceConfig.TYPE.equalsIgnoreCase(decorator)) {
            config.set(key, value);
        } else if (hasDecorator) {
            parts.computeIfAbsent(decorator, part -> new MonitoredPart());
            parts.get(decorator).addItem(key, value);
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
        this.status.setName(name);
    }

    /**
     * @return the configuration of this monitored resource
     */
    public MonitoredResourceConfig getConfig() {
        return config;
    }

    /**
     * @return the data
     */
    public List<MonitoredData> getData() {
        if (data != null) {
            return data;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * @return if the resource is healthy, meaning the last monitored data entry
     * returned healthy
     * @see MonitoredData.isHealthy();
     */
    public boolean isHealthy() {
        return status.isHealthy();
    }

    /**
     * @return if the current status of this resource
     * @see MonitoredData.isHealthy();
     */
    public MonitoredResourceStatus getStatus() {
        return status;
    }

    /**
     * FUTURE_WORK: this is an important point of optimization<br>
     * Given monitored data in memory is easy to access for status updates,
     * statistics etc... however memory is not unlimited nor cheap. Reading from
     * a persistent storage isn't either. Probably will end up in a situation
     * where all immediate data requirements can be answered from the heap and
     * only for more unusual request reading from persisted storage is required.
     * Next to that data will be kept in the heap as long as there is space.
     *
     * truncates the data based on DATA_LIMIT_AMOUNT to conserve memory usage
     */
    public void finaliseUpdate() {
        int diff = data.size() - DATA_LIMIT_AMOUNT;
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                data.getFirst().close();
                data.removeFirst();
            }
        }
    }

    /**
     * Update the status of the monitored resources
     */
    public abstract void updateStatus();

    @Override
    public String toString() {
        return "MonitoredResource{"
                + "name=" + name
                + ", config=" + config
                + ", parts=" + parts + '}';
    }
}

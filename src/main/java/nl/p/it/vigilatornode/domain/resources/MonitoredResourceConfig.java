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

import static java.lang.System.Logger.Level.WARNING;

/**
 * Configuration of a monitored resource
 *
 * @author Patrick
 */
public class MonitoredResourceConfig {

    public static final String TYPE = "config";

    private boolean active;
    private boolean ignoreTLSIssues;
    private String url;

    private static final String KEY_ACTIVE = "active";
    private static final String VALUE_TRUE = "true";
    private static final String KEY_URL = "url";
    private static final String KEY_IGNORE_TLS_ISSUES = "ignoreTLSIssues";

    private static final System.Logger LOGGER = System.getLogger(MonitoredResourceConfig.class.getName());

    public MonitoredResourceConfig() {
        active = false;
    }

    /**
     * @param active whether the monitored resource is active
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * @return whether monitoring the monitored resource is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return the url of this monitored resource
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return whether to ignore TLS issues for this resource
     */
    public boolean getIgnoreTLSIssues() {
        return ignoreTLSIssues;
    }

    /**
     * @param key key to set for the config
     * @param value the value to set for the config
     */
    public void set(final String key, final String value) {
        switch (key) {
            case KEY_ACTIVE -> {
                active = VALUE_TRUE.equals(value);
            }
            case KEY_URL -> {
                url = value;
            }
            case KEY_IGNORE_TLS_ISSUES -> {
                ignoreTLSIssues = VALUE_TRUE.equals(value);
            }
            default -> {
                LOGGER.log(WARNING, "Unexpected key detected: " + key + " the value will be ignored");
            }
        }
    }
}

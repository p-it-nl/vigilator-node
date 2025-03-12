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

import java.util.Collections;
import java.util.List;

/**
 * Status object for monitored resource, containing a succinct version of the
 * monitored resource indicating its current status
 *
 * @author Patrick
 */
public class MonitoredResourceStatus {

    private final String name;
    private final boolean healthy;
    private final List<String> errors;
    private final List<String> warnings;

    public MonitoredResourceStatus(
            final String name,
            final boolean healthy,
            final List<String> errors,
            final List<String> warnings) {
        this.name = name;
        this.healthy = healthy;
        this.errors = errors;
        this.warnings = warnings;
    }

    /**
     * @return the name of the monitored resource this status is from
     */
    public String getName() {
        return name;
    }

    /**
     * @return if the monitored resource is healthy
     */
    public boolean isHealthy() {
        return healthy;
    }

    /**
     * @return the errors of the monitored resource
     */
    public List<String> getErrors() {
        if (errors != null) {
            return errors;
        }

        return Collections.emptyList();
    }

    /**
     * @return the warnings of the monitored resource
     */
    public List<String> getWarnings() {
        if (warnings != null) {
            return warnings;
        }

        return Collections.emptyList();
    }
}

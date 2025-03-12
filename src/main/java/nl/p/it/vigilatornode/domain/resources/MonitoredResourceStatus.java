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
import java.util.List;

/**
 * Status object for monitored resource, containing a succinct version of the
 * monitored resource indicating its current status
 *
 * @author Patrick
 */
public class MonitoredResourceStatus {

    private String name;
    private final List<String> errors;
    private final List<String> warnings;

    public MonitoredResourceStatus() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    /**
     * @param name the name of the monitored resource this status is from
     */
    public void setName(final String name) {
        this.name = name;
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
        return errors.isEmpty();
    }

    /**
     * @param errors errors to add to the errors
     */
    public void addErrors(final List<String> errors) {
        if (errors != null) {
            this.errors.addAll(errors);
        }
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
     * @param warnings warnings to add to the warnings
     */
    public void addWarnings(final List<String> warnings) {
        if (warnings != null) {
            this.warnings.addAll(warnings);
        }
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

    /**
     * Clears the errors and warnings
     * <p>
     * Using this method is preferred to constructing a new instance since
     * object creation is expensive and there is always only one current status
     * of a resource
     */
    public void clear() {
        errors.clear();
        warnings.clear();
    }

    @Override
    public String toString() {
        return "MonitoredResourceStatus{" 
                + "name=" + name 
                + ", errors=" + errors 
                + ", warnings=" + warnings + '}';
    }
}

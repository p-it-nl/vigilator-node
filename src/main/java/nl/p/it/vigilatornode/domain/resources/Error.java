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

/**
 * Errors that can occur in a resource. These errors are human readable entries
 * that might be visible to the user.
 * <p>
 * Any technical information or long stacktraces should be logged.
 * </p>
 *
 * @author Patrick
 */
public class Error {

    public static final String ERROR = "Error: %s";
    public static final String NO_WEB_URL = "item: Web, no web url provided";
    public static final String NO_RESPONE = "Failed to get response for resource: %s on url: %s, see stacktrace for details";
    public static final String EMPTY_RESPONSE = "Empty response received from resource: %s on url: %s";
    public static final String EMPTY_STATUS = "Empty status object received from resource: %s on url: %s";
    public static final String NOT_VALID_JSON = "Response for resource: %s is not valid JSON with message: %s";
    public static final String POTENTIAL_VALUE_ERROR = "Received value: %s for '%s' in entry: %s indicating issues with the resource.";
    public static final String EXCEEDS_TIME_CONSTRAINTS = "Received update data exceeds specified time constraints in object: %s";
    /**
     * Format a warning
     *
     * @param message the warning to format
     * @param args the arguments to use
     * @return the formatted string (unchanged if no arguments provided or
     * string had no placeholders)
     */
    public static String withArgs(String message, final Object... args) {
        if (args != null) {
            message = message.formatted(args);
        }
        return message;
    }
}

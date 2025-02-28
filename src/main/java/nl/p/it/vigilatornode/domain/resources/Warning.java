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
 * Warning that can occur in a resource. These warning are human readable
 * entries that might be visible to the user. When something results in a
 * warning is configured in the resource configuration
 *
 * @author Patrick
 */
public class Warning {

    public static final String GENERAL_WARNING = "Warning: %s";
    public static final String STATUS_MISSING_FIELD = "A reply for resource: %s is missing a status object field: %s";
    public static final String WEB_VALIDATION_MISSING_TITLE = """
        The web reply validation for url: %s received a response but no title is 
        provided in the configuration to validate this response. The response might 
        not be what is expected""";

    private Warning() {
    }
        
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

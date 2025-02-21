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
package nl.p.it.vigilatornode.domain.resources.validation;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;
import java.util.Iterator;
import java.util.Map;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.resources.Error;
import nl.p.it.vigilatornode.domain.resources.MonitoredPart;
import nl.p.it.vigilatornode.domain.resources.Warning;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Validator to validate monitored data
 *
 * @author Patrick
 */
public class MonitorValidator {

    private final ConditionValidator conditionValidator;

    private static final String KEY_JSON_STATUS = "status";
    private static final String KEY_JSON_NAME = "name";
    private static final String KEY_JSON_ITEMS = "items";
    private static final String KEY_JSON_DATETIME = "datetime";
    private static final char WARNING_INDICATION = 'W';
    private static final String KEY_CONFIG_WEB = "Web";

    private static final System.Logger LOGGER = System.getLogger(MonitorValidator.class.getName());

    public MonitorValidator() {
        conditionValidator = new ConditionValidator();
    }

    /**
     * FUTURE_WORK: replace json.org later, it throws runtime exceptions and
     * does not provide line numbers or other relevant information on exception
     * and what we need is pretty simple. Will be done later for now its fine.
     * Not using Jackson since object deserialization is costly and we have not
     * fully settled on a response data structure
     *
     * @param result the result to validate
     * @param parts the parts to validate against
     * @param name the name of the resource being validated (this is used for
     * logging and error information)
     */
    public void validate(final MonitoredData result, final Map<String, MonitoredPart> parts, final String name) {
        if (result != null) {
            try {
                if (result.hasData()) {
                    validateJSON(result, parts, name);
                } else {
                    LOGGER.log(ERROR, "Empty response received in response from {0}", name);
                    result.addError(Error.withArgs(Error.EMPTY_RESPONSE, name, result.getUrl()));
                }
            } catch (JSONException ex) {
                LOGGER.log(ERROR, "Invalid JSON received in response from {0} with "
                        + "exception being: {1}", name, ex);
                result.addError(Error.withArgs(Error.NOT_VALID_JSON, name, ex.getMessage()));
            }
        } else {
            LOGGER.log(WARNING, "validate called without monitored data");
        }
    }

    public void validateWebReply(final MonitoredData result, final Map<String, MonitoredPart> parts, final String name) {
        // TODO: implement this
    }

    /**
     * FUTURE_WORK: Currently the response is compared to what is expected (the
     * parts) this allows the parts to be configured for multiple different
     * responses of a resource and allows to configure resources preemptively.
     *
     * How ever, this comes at the cost of not being able to validate mandatory
     * status objects and missing data
     *
     * @param result the result of a monitoring request
     * @param parts the validation parts
     * @param name the name of the resource
     */
    private void validateJSON(final MonitoredData result, final Map<String, MonitoredPart> parts, final String name) {
        JSONObject document = new JSONObject(new String(result.getData()));
        JSONArray status = (JSONArray) document.get(KEY_JSON_STATUS);

        if (status.isEmpty()) {
            LOGGER.log(ERROR, "Empty status object received in response from {0}", name);
            result.addError(Error.withArgs(Error.EMPTY_STATUS, name, result.getUrl()));
        }

        if (parts != null) {
            for (Object entry : status) {
                JSONObject statusEntry = (JSONObject) entry;
                if (statusEntry != null) {
                    validateStatusEntry(statusEntry, result, parts, name);
                } else {
                    // ignoring 
                }
            }
        } else {
            // if there are no parts to monitor for the resource, validating that 
            // there is a reply, has json and contains a status item is enough*/
        }
    }

    private void validateStatusEntry(final JSONObject statusEntry, final MonitoredData result, final Map<String, MonitoredPart> parts, final String name) {
        if (statusEntry.has(KEY_JSON_NAME)) {
            String partName = statusEntry.getString(KEY_JSON_NAME);
            if (parts.containsKey(partName)) {
                MonitoredPart part = parts.get(partName);
                if (statusEntry.has(KEY_JSON_ITEMS)) {
                    Map<String, String> validationItems = part.getItems();
                    JSONObject items = statusEntry.getJSONObject(KEY_JSON_ITEMS);
                    Iterator<String> keys = items.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        validateItem(key, items, validationItems, partName, result);
                    }
                } else {
                    result.addWarning(Warning.withArgs(Warning.STATUS_MISSING_FIELD, name, KEY_JSON_ITEMS));
                }

                if (statusEntry.has(KEY_JSON_DATETIME)) {
                    validateDatetimeCondition(part, statusEntry, partName, result);
                } else {
                    result.addWarning(Warning.withArgs(Warning.STATUS_MISSING_FIELD, name, KEY_JSON_DATETIME));
                }
            } else {
                // Not something the resource is interested in monitoring, skipping
            }
        } else {
            result.addWarning(Warning.withArgs(Warning.STATUS_MISSING_FIELD, name, KEY_JSON_NAME));
        }
    }

    private void validateItem(final String key, final JSONObject items, final Map<String, String> validationItems, final String partName, final MonitoredData result) {
        if (validationItems.containsKey(key)) {
            String value = items.getString(key);
            String condition = validationItems.get(key);
            if (conditionValidator.validateMeetsCriteria(value, condition)) {
                handlePotentialError(
                        Error.withArgs(Error.POTENTIAL_VALUE_ERROR, value, condition, partName),
                        condition, result);
            }
        } else {
            // Not something the resource is interested in monitoring, skipping
        }
    }

    private void validateDatetimeCondition(final MonitoredPart part, final JSONObject statusEntry, final String partName, final MonitoredData result) {
        String datetimeCondition = part.getDatetimeCondition();
        if (datetimeCondition != null && !datetimeCondition.isEmpty()) {
            String datetimeLastUpdated = statusEntry.getString(KEY_JSON_DATETIME);
            if (conditionValidator.validateMeetsCriteria(datetimeLastUpdated, datetimeCondition)) {
                handlePotentialError(
                        Error.withArgs(Error.EXCEEDS_TIME_CONSTRAINTS, partName),
                        datetimeCondition, result);
            } else {
                // the update is timely
            }
        } else {
            // for this monitored part, datetime of the update is not required to be within boundaries
        }
    }

    private void handlePotentialError(final String message, final String condition, final MonitoredData result) {
        char endingChar = condition.charAt((condition.length() - 1));
        if (WARNING_INDICATION == endingChar) {
            result.addWarning(message);
        } else {
            result.addError(message);
        }
    }

}

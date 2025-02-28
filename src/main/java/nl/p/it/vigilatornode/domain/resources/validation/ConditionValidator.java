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

import java.time.Instant;
import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.WARNING;
import static java.time.temporal.ChronoUnit.MINUTES;
import static nl.p.it.vigilatornode.domain.resources.validation.ConditionType.*;

/**
 * Validator for conditions
 *
 * @author Patrick
 */
public class ConditionValidator {

    private static final char IS = '=';
    private static final char EXCLAMATION_MARK = '!';
    private static final char BIGGER_THEN = '>';
    private static final char SMALLER_THEN = '<';
    private static final int NPOS = -1;
    private static final String MIN = "min";
    private static final char WARNING_INDICATION = 'W';
    private static final char PERCENT = '%';

    private static final System.Logger LOGGER = System.getLogger(ConditionValidator.class.getName());

    /**
     * @param value the value to validate
     * @param condition the condition to validate the value against
     * @return whether the value matches the condition
     */
    public boolean validateMeetsCriteria(final String value, String condition) {
        if (condition != null && !condition.isEmpty()) {
            condition = trimWarningIfExists(condition);
            int valueSize = (value == null ? 0 : value.length());
            int conditionSize = condition.length();
            int conditionStart = Character.isSpaceChar(condition.charAt(0)) ? 1 : 0;
            char firstChar = condition.charAt(conditionStart);

            switch (firstChar) {
                case EXCLAMATION_MARK -> {
                    return matchesIsNotCondition(valueSize, conditionSize, conditionStart, value, condition);
                }
                case BIGGER_THEN -> {
                    return matchesValueCondition(valueSize, conditionSize, value, condition, BIGGER);
                }
                case SMALLER_THEN -> {
                    return matchesValueCondition(valueSize, conditionSize, value, condition, SMALLER);
                }
                case IS -> {
                    if (IS == condition.charAt(1)) {
                        return matchesIsEqualCondition(valueSize, conditionSize, conditionStart, value, condition);
                    }
                }
                default -> {
                    int exclMarkPos = condition.indexOf(EXCLAMATION_MARK);
                    if (NPOS != exclMarkPos) {
                        return matchesIsNotCondition(valueSize, conditionSize, exclMarkPos, value, condition);
                    }
                }
            }
        }

        return false;
    }

    private boolean matchesIsNotCondition(final int valueSize, final int conditionSize, final int positionInCondition, final String value, final String condition) {
        if (valueSize == 0) {
            return true;
        }

        String valueToMatch = value.substring(positionInCondition, valueSize);
        String conditionToMatch = condition.substring((positionInCondition + 1), conditionSize);

        return !valueToMatch.equals(conditionToMatch);
    }

    private boolean matchesIsEqualCondition(final int valueSize, final int conditionSize, final int positionInCondition, final String value, final String condition) {
        if (valueSize == 0) {
            return false;
        }

        String valueToMatch = value.substring(positionInCondition, valueSize);
        String conditionToMatch = condition.substring((positionInCondition + 2), conditionSize);
        conditionToMatch = trimFirstSpaceIfExists(conditionToMatch);

        return valueToMatch.equals(conditionToMatch);
    }

    /**
     * FUTURE_WORK: extract and use temportal type -> String temporalType =
     * condition.substring(startPositionTemporalIndicator, conditionSize);
     *
     * @param valueSize the value size
     * @param conditionSize the condition size
     * @param value the value
     * @param condition the condition
     * @param type the condition type
     * @return whether matches the condition
     */
    private boolean matchesValueCondition(final int valueSize, final int conditionSize, final String value, final String condition, final ConditionType type) {
        if (valueSize == 0) {
            return false;
        }

        String valueToMatch = value.substring(0, valueSize);
        String conditionToMatch = condition.substring(1, conditionSize);
        conditionToMatch = trimFirstSpaceIfExists(conditionToMatch);
        try {
            int startPositionTemporalIndicator = conditionToMatch.indexOf(MIN);
            if (NPOS != startPositionTemporalIndicator) {
                String temporalAmountString = conditionToMatch.substring(0, startPositionTemporalIndicator);
                temporalAmountString = temporalAmountString.substring(1);
                temporalAmountString = trimFirstSpaceIfExists(temporalAmountString);
                int temporalAmount = Integer.parseInt(temporalAmountString.trim());
                if (BIGGER == type) {
                    return matchesDateCondition(value, temporalAmount, AFTER);
                } else {
                    return matchesDateCondition(value, temporalAmount, BEFORE);
                }
            } else {
                int parsedValue = Integer.parseInt(removeCharacter(valueToMatch, PERCENT));
                int parsedCondition = Integer.parseInt(removeCharacter(conditionToMatch, PERCENT));
                if (BIGGER == type) {
                    return parsedValue > parsedCondition;
                } else {
                    return parsedValue < parsedCondition;
                }
            }
        } catch (NumberFormatException ex) {
            LOGGER.log(WARNING, "String could not be parsed to integer, exception: ", ex);
            return false;
        }
    }

    /**
     * FUTURE_WORK: add more temporal types add: @param temporalType the
     * temporal type (currently supports: `min`)
     *
     * @param value the value to match
     * @param temporalAmount the amount to match against
     * @param type the condition type (BEFORE / AFTER)
     * @return whether value matches
     */
    private boolean matchesDateCondition(final String value, final int temporalAmount, final ConditionType type) {
        try {
            long datetimeValue = Long.parseLong(value);
            if (datetimeValue > 1000000000) {
                long conditionLimit = Instant.now().plus(temporalAmount, MINUTES).getEpochSecond();
                if (BEFORE == type) {
                    return datetimeValue < conditionLimit;
                } else {
                    return datetimeValue > conditionLimit;
                }
            } else {
                LOGGER.log(DEBUG, "Not a valid timestamp format: {0}", datetimeValue);
                return false;
            }
        } catch (NumberFormatException ex) {
            LOGGER.log(WARNING, "String could not be parsed to long, exception: {0}", ex);
            return false;
        }
    }

    private String trimFirstSpaceIfExists(final String value) {
        if (Character.isSpaceChar(value.charAt(0))) {
            return value.substring(1, value.length());
        } else {
            return value;
        }
    }

    private String trimWarningIfExists(final String condition) {
        int end = condition.length() - 1;
        if (condition.charAt(end) == WARNING_INDICATION) {
            return condition.substring(0, (end - 1));
        }
        return condition;
    }

    /**
     * Remove character from a string Why? because String.replaceAll uses regex
     * under the hood and is unreasonably slow for something this simple
     *
     * FUTURE_WORK: move to a util class?
     *
     * @param value to replace in
     * @param charToRemove the char to remove
     * @return the value without the char of unchanged if the char was not found
     */
    private String removeCharacter(final String value, final char charToRemove) {
        int index = value.indexOf(charToRemove);
        if (NPOS != index) {
            return value.substring(0, index)
                    + value.substring(index + 1, value.length());
        }
        return value;
    }
}

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for condition validator
 *
 * @author Patrick
 */
@SuppressWarnings("java:S1192")// really valuable in this class to have literals for readability
public class ConditionValidatorTest {

    private ConditionValidator classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new ConditionValidator();
    }

    @Test
    public void noConditionResultingInFalse() {
        String value = "some value";
        String condition = null;

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForValueIsNotWithoutAValue() {
        String value = null;
        String condition = "!value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsNotWithAnEmptyValue() {
        String value = "";
        String condition = "!value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsNotWithValueBeingEqualToCondition() {
        String value = "value";
        String condition = "!value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForValueIsNotWithValueBeingUnequalToCondition() {
        String value = "mockvalue";
        String condition = "!value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsNotWithValueBeingUnequalToCondition_withWarningIndicationInCondition() {
        String value = "mockvalue";
        String condition = "!value W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsNotWithValueBeingUnequalToConditionExpectingNoCapsAndReceivingCaps() {
        String value = "Value";
        String condition = "!value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsEqualToWithoutAValue() {
        String value = null;
        String condition = "==value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsEqualToWithEmtpyValue() {
        String value = "";
        String condition = "==value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsEqualToWithUnequalValue() {
        String value = "somethingelse";
        String condition = "==value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsEqualToWithEqualValue() {
        String value = "value";
        String condition = "==value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsEqualToWithEqualValueExpectingNumbers() {
        String value = "8";
        String condition = "==8";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsEqualToWithEqualValueWithConditionStartingWithASpace() {
        String value = "value";
        String condition = "== value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsEqualToWithEqualValueWithConditionStartingWithASpace_withWarningIndicationInCondition() {
        String value = "value";
        String condition = "== value W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsEqualToWithUnequalValueStartingWithASpaceAndWithConditionStartingWithASpace() {
        String value = " value";
        String condition = "== value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsEqualToWithEqualValueExpectingANumber() {
        String value = "0";
        String condition = "==0";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsPartialNotWithoutValue() {
        String value = null;
        String condition = "value !value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsPartialNotWithoutHavingIsNotConditionInCondition() {
        String value = "mock";
        String condition = "value value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsPartialNotWithEmptyValue() {
        String value = "";
        String condition = "value !value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsPartialNotWithValueEqual() {
        String value = "value value";
        String condition = "value !value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsPartialNotWithValueNotEqual() {
        String value = "value mock";
        String condition = "value !value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsPartialNotWithValueHavingDifferentStringSize() {
        String value = "valuevalue mock mock";
        String condition = "value !value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsPartialNotWithValueContinueingAfterValue() {
        String value = "value mock mock";
        String condition = "value !value";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsPartialNotWithValueContinueingAfterValue_withWarningIndicationInCondition() {
        String value = "value mock mock";
        String condition = "value !value W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithoutAValue() {
        String value = "";
        String condition = "> 80";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAValueLessThenCondition() {
        String value = "10";
        String condition = "> 80";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAValueEqualToCondition() {
        String value = "80";
        String condition = "> 80";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAValueBiggerThenCondition() {
        String value = "88";
        String condition = "> 80";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAValueBiggerThenCondition_withWarningIndicationInCondition() {
        String value = "88";
        String condition = "> 80 W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAPercentValueBiggerThenPercentCondition() {
        String value = "88%";
        String condition = "> 80%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAPercentValueBiggerThenPercentCondition_withWarningIndicationInCondition() {
        String value = "88%";
        String condition = "> 80% W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithANumberValueBiggerThenPercentCondition() {
        String value = "88";
        String condition = "> 80%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAPercentValueSmallerThenPercentCondition() {
        String value = "70%";
        String condition = "> 80%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAPercentValueEqualToPercentCondition() {
        String value = "80%";
        String condition = "> 80%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForValueIsBiggerThenWithAValueNotANumber() {
        String value = "mock";
        String condition = "> 80";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsLessThenWithoutAValue() {
        String value = null;
        String condition = "< 40";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsLessThenWithEmptyValue() {
        String value = "";
        String condition = "< 40";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsLessThenWithValueBeingBiggerThenCondition() {
        String value = "44";
        String condition = "< 40";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsLessThenWithValueBeingEqualToCondition() {
        String value = "40";
        String condition = "< 40";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsLessThenWithValueBeingLessThenCondition() {
        String value = "20";
        String condition = "< 40";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsLessThenWithValueBeingLessThenCondition_withWarningIndicationInCondition() {
        String value = "20";
        String condition = "< 40 W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsLessThenWithPercentValueBeingLessThenPercentCondition() {
        String value = "20%";
        String condition = "< 40%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsLessThenWithPercentValueBeingLessThenPercentCondition_withWarningIndicationInCondition() {
        String value = "20%";
        String condition = "< 40% W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }
    
    @Test
    public void conditionForIsLessThenWithNumberValueBeingLessThenPercentCondition() {
        String value = "20";
        String condition = "< 40%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsLessThenWithPercentValueBeingEqualToPercentCondition() {
        String value = "40%";
        String condition = "< 40%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsLessThenWithPercentValueBeingBiggerThenPercentCondition() {
        String value = "60%";
        String condition = "< 40%";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsNotMoreThenGivenMinutesAgoWithoutValue() {
        String value = null;
        String condition = "< 4min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsNotMoreThenGivenMinutesAgoWithEmptyValue() {
        String value = "";
        String condition = "< 4min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsNotMoreThenGivenMinutesAgoWithValueNotBeingATimestamp() {
        String value = "mock";
        String condition = "< 4min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsNotMoreThenGivenMinutesAgoWithValueANumberButNotATimestamp() {
        String value = "4";
        String condition = "< 4min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsNotMoreThenGivenMinutesAgoWithTimestampValueBeingAfterCondition() {
        String value = "" + Instant.now().plus(8, MINUTES).getEpochSecond();
        String condition = "< 4 min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsNotMoreThenGivenMinutesAgoWithTimestampValueBeingEqualToCondition() {
        String value = "" + Instant.now().plus(4, MINUTES).getEpochSecond();
        String condition = "< 4 min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsNotMoreThenGivenMinutesAgoWithTimestampValueBeingBeforeCondition() {
        String value = "" + Instant.now().plus(3, MINUTES).getEpochSecond();
        String condition = "< 4 min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithoutValue() {
        String value = null;
        String condition = "> 8min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithEmptyValue() {
        String value = "";
        String condition = "> 8min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithValueNotBeingATimestamp() {
        String value = "mock";
        String condition = "> 8min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithValueANumberButNotATimestamp() {
        String value = "4";
        String condition = "> 8min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithTimestampValueBeingAfterCondition() {
        String value = "" + Instant.now().plus(16, MINUTES).getEpochSecond();
        String condition = "> 8min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithTimestampValueBeingAfterCondition_withWarningIndicationInCondition() {
        String value = "" + Instant.now().plus(16, MINUTES).getEpochSecond();
        String condition = "> 8min W";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertTrue(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithTimestampValueBeingEqualToCondition() {
        String value = "" + Instant.now().plus(8, MINUTES).getEpochSecond();
        String condition = "> 8min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }

    @Test
    public void conditionForIsMoreThenGivenMinutesAgoWithTimestampValueBeingBeforeCondition() {
        String value = "" + Instant.now().plus(4, MINUTES).getEpochSecond();
        String condition = "> 8min";

        boolean result = classUnderTest.validateMeetsCriteria(value, condition);

        assertFalse(result);
    }
}

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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for warning
 *
 * @author Patrick
 */
public class WarningTest {

    private static final String ARG_1 = "arg1";
    private static final String ARG_2 = "arg2";

    @Test
    public void baseWarning() {
        String warning = Warning.WARNING;

        assertNotNull(warning);
    }

    @Test
    public void warningWithOnePlaceholderAndWithOneArgument() {
        String argument = ARG_1;

        String warning = Warning.withArgs(Warning.WARNING, argument);

        assertNotNull(warning);
        assertTrue(warning.contains(argument));
    }

    @Test
    public void warningWithOnePlaceholderAndWithoutArgument() {
        String argument = null;
        String expected = "null";

        String warning = Warning.withArgs(Warning.WARNING, argument);

        assertNotNull(warning);
        assertTrue(warning.contains(expected));
    }

    @Test
    public void warningWithOnePlaceholderAndWithEmptyArgument() {
        String argument = "";
        String original = Warning.WARNING;

        String warning = Warning.withArgs(Warning.WARNING, argument);

        assertNotNull(warning);
        assertTrue((warning.length()) == (original.length() - 2));
    }

    @Test
    public void warningWithOnePlaceholderAndWithTwoArguments() {
        String argument = ARG_1;
        String argumentTwo = ARG_2;

        String warning = Warning.withArgs(Warning.WARNING, argumentTwo, argument);

        assertNotNull(warning);
        assertTrue(warning.contains(argumentTwo));
    }
}

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
 * Tests for error
 *
 * @author Patrick
 */
public class ErrorTest {

    private static final String ARG_1 = "arg1";
    private static final String ARG_2 = "arg2";

    @Test
    public void baseError() {
        String error = Error.GENERAL_ERROR;

        assertNotNull(error);
    }

    @Test
    public void errorWithOnePlaceholderAndWithOneArgument() {
        String argument = ARG_1;

        String error = Error.withArgs(Error.GENERAL_ERROR, argument);

        assertNotNull(error);
        assertTrue(error.contains(argument));
    }

    @Test
    public void errorWithOnePlaceholderAndWithoutArgument() {
        String argument = null;
        String expected = "null";

        String error = Error.withArgs(Error.GENERAL_ERROR, argument);

        assertNotNull(error);
        assertTrue(error.contains(expected));
    }

    @Test
    public void errorWithOnePlaceholderAndWithEmptyArgument() {
        String argument = "";
        String original = Error.GENERAL_ERROR;

        String error = Error.withArgs(Error.GENERAL_ERROR, argument);

        assertNotNull(error);
        assertTrue((error.length()) == (original.length() - 2));
    }

    @Test
    public void errorWithOnePlaceholderAndWithTwoArguments() {
        String argument = ARG_1;
        String argumentTwo = ARG_2;

        String error = Error.withArgs(Error.GENERAL_ERROR, argumentTwo, argument);

        assertNotNull(error);
        assertTrue(error.contains(argumentTwo));
    }
}

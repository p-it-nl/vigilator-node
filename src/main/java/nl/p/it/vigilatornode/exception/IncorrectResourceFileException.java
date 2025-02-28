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
package nl.p.it.vigilatornode.exception;

/**
 * Exception for when the application reads an incorrect resource file
 *
 * @author Patrick
 */
public class IncorrectResourceFileException extends VigilatorNodeException {

    private final int line;

    public IncorrectResourceFileException(final CustomException exception) {
        super(exception);
        this.line = 0;
    }

    public IncorrectResourceFileException(final CustomException exception, final int line) {
        super(exception);
        this.line = line;
    }

    public IncorrectResourceFileException(final CustomException exception, final Object... args) {
        super(exception, args);
        this.line = 0;
    }

    public IncorrectResourceFileException(final int line, final CustomException exception, final Object... args) {
        super(exception, args);
        this.line = line;
    }

    /**
     * @return the line the exception occurred at or 0
     */
    public int getLine() {
        return line;
    }
}

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
 * Exception when a entity is received that the application cannot process
 *
 * @author Patrick
 */
public final class UnprocessableEntityException extends RequestException {

    public UnprocessableEntityException(final CustomException exception) {
        super(exception);
    }

    public UnprocessableEntityException(final CustomException exception, final Object... args) {
        super(exception, args);
    }

    @Override
    public int getStatusCode() {
        return 422;
    }
}

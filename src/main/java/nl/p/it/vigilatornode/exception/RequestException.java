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
 * Exception that can occur while processing requests
 *
 * @author Patrick
 */
public abstract sealed class RequestException extends VigilatorNodeException permits
        NotFoundException, BadRequestException, MethodNotAllowedException, 
        UnprocessableEntityException, ServiceUnavailableException {

    protected RequestException(final CustomException exception) {
        super(exception);
    }

    protected RequestException(final CustomException exception, final Object... args) {
        super(exception, args);
    }

    public abstract int getStatusCode();
}

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
 * Custom exceptions including some nice messages that are user friendly
 *
 * @author Patrick
 */
public enum CustomException {

    MISSING_APP_PROPERTIES("Not able to read app.properties file in the classpath, "
            + "resolve this issue in order to use the application"),
    INVALID_APP_PROPERTIES("""
        Not able to read app.properties file in the classpath, the file might be 
        in incorrect format, not found, or the application has no permission to 
        read it. Validate the file has {enviroment}.app.properties as name and that 
        the contents are valid. See the exception for more details"""
    ),
    SERVER_FAILED_TO_BOOT("The application was unable to boot httpserver, see exception for more details"),
    NO_ENVIRONMENT_SPECIFIED("The application requires an `environment` argument with value of either: `local`, `prod`, e.g.: environment=local"),
    UNEXPECTED_REQUEST_METHOD("The provided request method \'%s\' is not expected for this endpoint"),
    THE_REQUEST_GOT_INTERUPPTED("Sending the request failed due to interuption, trying agian might work"),
    UNEXPECTED_EXCEPTION_DURING_REQUEST("Sending the request resulted in an unexpected exception"),
    THE_REQUEST_WAS_NOT_AUTHORIZED("Sending the request resulted in authorization problems, validate any authorization requirements are met"),
    INVALID_URL("Requesting the request from the http builder failed due to the url for the request not being a valid url, url being: %s"),
    REQUIRED_VALUES_NOT_PROVIDED("Request requires a http request, acceptor and http client. Missing is: %s"),
    NO_REPONSE_RECEIVED("The request did not result in a response, this is a fatal error indicating connection issues"),
    THE_REQUEST_FAILED("The request to upstream server failed due to IO issues, probably the request timed out"),
    THE_REQUEST_RESULTED_IN_IO_ISSUES("The request had issues while reading or writing to the request, this might be due to connectivity issues resulting in incomplete datastreams"),
    COULD_NOT_CONFIGURE_LOGGER("Logger could not be configured, please validate path to log as configured in the properties is accessable"),
    COULD_NOT_READ_RESOURCE_FILES("""
            The specified location for reading monitored resources files could 
            not be accessed, validate the location is correct and the application 
            is permitted to access the location"""),
    DIRECTORY_DOES_NOT_EXIST("The specified directory: %s does not exist"),
    INVALID_RESOURCE_FILE("Incorrect resource file: %s, line %s, error: %s"),
    OUT_OF_CONTEXT_RESOURCE_NAME("Resource name given outside the context of a resource"),
    TO_DEEP_TABBING("Unexpected character detected, tab depth deeper then expected"),
    UNEXPECTED_RESOURCE("Unexpected resource type: %s, expected is either: ExposedResource, OnboardResource or InternalResource");

    private final String message;

    CustomException(String message) {
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}

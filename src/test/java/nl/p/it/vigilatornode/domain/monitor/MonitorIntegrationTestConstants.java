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
package nl.p.it.vigilatornode.domain.monitor;

/**
 * Constants for monitor integration tests
 *
 * @author Patrick
 */
public class MonitorIntegrationTestConstants {

    static final String RESOURCE_ONE = "ResourceOne";
    static final String RESOURCE_TWO = "ResourceTwo";
    static final String RESOURCE_THREE = "ResourceThree";
    static final String TRUE = "true";
    static final String FALSE = "false";
    static final String KEY_ACTIVE = "active";
    static final String KEY_URL = "url";
    static final String CONDITION_EQUALS_FALSE = "== false";
    static final String CONDITION_EQUALS_TRUE = "== true";
    static final String CONDITION_NOT_RUNNING = "!running";
    static final String CONDITION_BIGGER_THEN_TEN_WARNING = "> 10 W";
    static final String CONDITION_BIGGER_THEN_ZERO = "> 0";
    static final String KEY_IS_PROCESSING = "isProcessing";
    static final String KEY_HAS_EXCEPTIONS = "hasExceptions";
    static final String KEY_DATABASE = "database";
    static final String KEY_THREADS_QUEUED = "threads queued";
    static final String KEY_THREADS_BROKEN = "threads broken";

    static final String RESPONSE_RESOURCE_ONE_OK = """
        {
            "environment": "mock",
            "status": [
                {
                    "name": "ResourceOne",
                    "items": {
                        "isProcessing": "true",
                        "hasExceptions": "false"
                    },
                    "datetime": "1740587609"
                }
            ]
        }
        """;
    static final String RESPONSE_RESOURCE_ONE_NOK = """
        {
            "environment": "mock",
            "status": [
                {
                    "name": "ResourceOne",
                    "items": {
                        "isProcessing": "false",
                        "hasExceptions": "true"
                    },
                    "datetime": "1740587609"
                }
            ]
        }
        """;
    static final String RESPONSE_RESOURCE_THREE_NOK = """
        {
            "environment": "mock",
            "status": [
                {
                    "name": "ResourceThree",
                    "items": {
                        "database": "broken",
                        "threads queued": "1987",
                        "threads broken": "99",
                    }
                }
            ]
        }
    """;
    
    private MonitorIntegrationTestConstants() {
    }
}

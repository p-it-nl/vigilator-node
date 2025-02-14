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
package nl.p.it.vigilatornode.configuration;

import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for node config
 *
 * @author Patrick
 */
public class NodeConfigTest {

    private static final String LOCAL = "local";

    @Test
    public void load_expectingInstance() throws VigilatorNodeException {
        NodeConfig result = NodeConfig.load(LOCAL);

        assertNotNull(result);
        assertTrue(result.getPort() > 0);
        assertTrue(result.getAllowedOrigins() != null);
    }
}

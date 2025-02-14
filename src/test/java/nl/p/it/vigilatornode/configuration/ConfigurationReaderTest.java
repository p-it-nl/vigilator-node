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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Tests for configuration reader
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class ConfigurationReaderTest {

    @Mock
    private NodeConfig mockedConfig;
    
    private static final String LOCAL = "local";

    @Test
    public void load_withoutConfig() {
        NodeConfig config = null;

        assertDoesNotThrow(() -> new ConfigurationReader(LOCAL).load(config));

        assertNull(config);
    }

    @Test
    public void load_withConfig() {
        NodeConfig config = mockedConfig;

        assertDoesNotThrow(() -> new ConfigurationReader(LOCAL).load(config));

        verify(config, atLeastOnce()).setPort(any(Integer.class));
        verify(config, atLeastOnce()).setAllowedHeaders(any(String.class));
    }
}

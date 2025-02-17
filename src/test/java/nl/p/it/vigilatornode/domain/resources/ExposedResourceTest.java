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

import nl.p.it.vigilatornode.domain.out.OutgoingClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for exposed resource
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class ExposedResourceTest {
    
    @Mock
    private OutgoingClient client;
    
    private ExposedResource classUnderTest;
    
    @BeforeEach
    public void setUp() {
        classUnderTest = new ExposedResource();
    }
    
    @Test
    public void connectWithoutClient() {
        assertDoesNotThrow(() -> classUnderTest.connect(null));
    }
    
    @Test
    public void connectWithClient() {
        assertDoesNotThrow(() -> classUnderTest.connect(client));
    }
}

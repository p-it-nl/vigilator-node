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
package nl.p.it.vigilatornode.domain.out;

import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.HttpClientException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for outgoing client
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class OutgoingClientTest {

    @Mock
    private NodeConfig config;

    private OutgoingClient classUnderTest;

    private static final String URL = "https://localhost";

    @BeforeEach
    public void setUp() {
        classUnderTest = OutgoingClient.getInstance(config);
    }

    @Test
    public void scheduleRequestWithoutAnyValues() throws HttpClientException {
        CustomException expectedException = CustomException.INVALID_URL;
        String url = null;
        Acceptor acceptor = null;

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> classUnderTest.scheduleRequest(url, acceptor));

        assertEquals(String.format(expectedException.getMessage(), url), exception.getMessage());
    }

    @Test
    public void scheduleRequestWithEmptyUrlAndHavingAcceptor() throws HttpClientException {
        CustomException expectedException = CustomException.INVALID_URL;
        String url = null;
        Acceptor<MonitoredData> acceptor = (MonitoredData data) -> {
        };

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> classUnderTest.scheduleRequest(url, acceptor));

        assertEquals(String.format(expectedException.getMessage(), url), exception.getMessage());
    }

    @Test
    public void scheduleRequestWithUrlAndAcceptor() throws HttpClientException {
        CustomException expectedException = CustomException.INVALID_URL;
        String url = URL;
        Acceptor<MonitoredData> acceptor = (MonitoredData data) -> {
        };

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> classUnderTest.scheduleRequest(url, acceptor));

        assertEquals(String.format(expectedException.getMessage(), url), exception.getMessage());
    }
}

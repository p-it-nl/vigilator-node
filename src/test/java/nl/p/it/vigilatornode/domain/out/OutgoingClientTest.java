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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import nl.p.it.vigilatornode.configuration.NodeConfig;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import nl.p.it.vigilatornode.domain.monitor.Acceptor;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.HttpClientException;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for outgoing client
 *
 * @author Patrick
 */
@ExtendWith(MockitoExtension.class)
public class OutgoingClientTest {

    private static OutgoingClient classUnderTest;//NOSONAR: required state for test

    private static final String INVALID_URL = "localhost";
    private static final String URL = "https://localhost.com/somewhere";

    @BeforeAll
    public static void setUp() {
        NodeConfig config = mock(NodeConfig.class);
        when(config.getPoolExecutor()).thenReturn(
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10)));
        classUnderTest = OutgoingClient.getInstance(config);
    }

    @Test
    public void scheduleRequestWithoutAnyValues() throws HttpClientException {
        CustomException expectedException = CustomException.INVALID_URL;
        String url = null;
        Acceptor<MonitoredData> acceptor = null;

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
    public void scheduleRequestWithInvalidUrlAndAcceptor() throws HttpClientException {
        CustomException expectedException = CustomException.INVALID_URL;
        String url = INVALID_URL;
        Acceptor<MonitoredData> acceptor = (MonitoredData data) -> {
        };

        VigilatorNodeException exception = assertThrows(HttpClientException.class,
                () -> classUnderTest.scheduleRequest(url, acceptor));

        assertEquals(String.format(expectedException.getMessage(), url), exception.getMessage());
    }

    @Test
    public void scheduleRequestWithValidUrlAndAcceptor() throws HttpClientException {
        String url = URL;
        Acceptor<MonitoredData> acceptor = (MonitoredData data) -> {
        };

        assertDoesNotThrow(() -> classUnderTest.scheduleRequest(url, acceptor));
    }
}

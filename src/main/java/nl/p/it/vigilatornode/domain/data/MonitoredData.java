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
package nl.p.it.vigilatornode.domain.data;

import java.io.Serializable;
import java.lang.ref.Cleaner;
import java.time.Instant;

/**
 * Data that has been observed, meaning it has been received, understood and is
 * ready to be stored somewhere persisting (or not).
 *
 * @author Patrick
 * @param <T> type of data
 */
public class MonitoredData<T> implements AutoCloseable, Serializable {

    private final State<T> state;
    private final Cleaner.Cleanable cleanable;

    private static final Cleaner cleaner = Cleaner.create();

    /**
     * Static nested class avoids accidentally retaining the references to
     * memory data, preventing memory exhaustion and helps with graceful clean
     * up after breaking exceptions or exhausted number of attempts to recover
     * from a bad situation. By storing carefully, the application can get away
     * with attempting to collect vital monitoring data more rigoriously
     */
    static class State<T> implements Runnable {

        private byte[] data;
        private int take;
        private String url;
        private final Instant timestamp;

        State(final byte[] data) {
            this.data = data;
            this.timestamp = Instant.now();
        }

        @Override
        public void run() {
            this.data = null;
        }
    }

    public MonitoredData(final byte[] data) {
        this.state = new State<>(data);
        this.cleanable = cleaner.register(this, state);
    }

    public byte[] getData() {
        return this.state.data;
    }

    public void label(final int take) {
        this.state.take = take;
    }

    public void url(final String url) {
        this.state.url = url;
    }

    public int getTake() {
        return this.state.take;
    }

    public String getUrl() {
        return this.state.url;
    }

    public Instant getSince() {
        return this.state.timestamp;
    }

    public boolean hasData() {
        return this.state.data != null && this.state.data.length > 0;
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}

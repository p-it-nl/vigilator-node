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
import java.util.List;

/**
 * Data that has been observed, meaning it has been received, understood and is
 * ready to be stored in the database.This is temporary saved data in the state
 * that makes sure that if either saving to the database or retrieving data
 * fails, the data remains. This also allows for concurrency to not be bound to
 * either task, meaning saving to database does not block retrieving data and
 * vice versa
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
     * articles, preventing memory exhaustion and helps with graceful clean up
     * after breaking exceptions or exhausted number of attempts to recover from
     * a bad situation
     */
    static class State<T> implements Runnable {

        private byte[] data;
        private final Instant timestamp;

        State(final List<T> data) {
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

    public List<T> getData() {
        return this.state.data;
    }

    public Instant getSince() {
        return this.state.timestamp;
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}

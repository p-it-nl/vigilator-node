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

import static java.lang.System.Logger.Level.ERROR;

/**
 * Task to wait and notify when wait completed
 *
 * @author Patrick
 */
public class WaitTask implements Runnable {

    private final int millisToWait;
    private final Notifier notifier;

    private static final System.Logger LOGGER = System.getLogger(WaitTask.class.getName());

    public WaitTask(final int millisToWait, final Notifier notifier) {
        this.millisToWait = millisToWait;
        this.notifier = notifier;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(millisToWait);

            if (notifier != null) {
                notifier.doNotify();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOGGER.log(ERROR, "Error while waiting before resending update "
                    + "request, exception: {0}", ex);
        }
    }
}

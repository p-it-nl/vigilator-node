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
package nl.p.it.vigilatornode.domain.status;

import com.sun.net.httpserver.HttpExchange;
import java.util.Map;
import nl.p.it.vigilatornode.domain.Service;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;

/**
 * Service for processing status updates 
 * 
 * @author Patrick
 */
public class StatusService implements Service {

    @Override
    public void processRequest(
            final byte[] bytes, 
            final Map<String, String> params,
            final HttpExchange exchange) throws VigilatorNodeException {
        
        System.out.println("hoi");
        System.out.println();
        
        
    }

}

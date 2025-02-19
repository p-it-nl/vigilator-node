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

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;
import java.util.Map;
import nl.p.it.vigilatornode.domain.data.MonitoredData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Validator to validate monitored data
 *
 * @author Patrick
 */
public class MonitorValidator {

    private static final String KEY_JSON_STATUS = "status";
    private static final String KEY_JSON_NAME = "name";
    private static final String KEY_JSON_ITEMS = "items";
    private static final String KEY_JSON_DATETIME = "datetime";

    private static final System.Logger LOGGER = System.getLogger(MonitorValidator.class.getName());

    public MonitorValidator() {
        // new ConditionValidator
    }

    /**
     * FUTURE_WORK: replace json.org later, it throws runtime exceptions and
     * does not provide line numbers or other relevant information on exception
     * and what we need is pretty simple. Will be done later for now its fine.
     * Not using Jackson since object deserialization is costly and we have not
     * fully settled on a response data structure
     *
     * TODO: Move to its own class and unit test
     *
     * @param result the result to validate
     * @param parts the parts to validate against
     * @param name the name of the resource being validated (this is used for
     * logging and error information)
     */
    public void validate(final MonitoredData result, final Map<String, MonitoredPart> parts, final String name) {
        if (result != null) {
            try {
                if (result.hasData()) {
                    JSONObject document = new JSONObject(new String(result.getData()));
                    JSONArray statusItems = (JSONArray) document.get(KEY_JSON_STATUS);

                } else {
                    LOGGER.log(ERROR, "Empty response received in response from {0}", name);
                    result.addError(Error.withArgs(Error.EMPTY_RESPONSE, name, result.getUrl()));
                }
            } catch (JSONException ex) {
                LOGGER.log(ERROR, "Invalid JSON received in response from {0} with "
                        + "exception being: {1}", name, ex);
                result.addError(Error.withArgs(Error.NOT_VALID_JSON, name, ex.getMessage()));
            }
        } else {
            LOGGER.log(WARNING, "validate called without monitored data");
        }
        /*
        QJsonObject json = document.object();
        QJsonArray status = json.value(KEY_JSON_STATUS).toArray();

        for (const QJsonValue & v : status
        
        
            ){
                QJsonObject statusEntry = v.toObject();
            std::string name = statusEntry.value(KEY_JSON_NAME).toString().toStdString();
            if (parts.find(name) != parts.end()) {
                MonitoredPart * part = parts[name];
                std::map < std::string
                , std::string > validationItems = part -> getItems();
                QJsonObject items = statusEntry.value(KEY_JSON_ITEMS).toObject();
                foreach(
                const QString key, items
                .keys()
                
                
                    ){
                        validateItem(key, items, validationItems, name);
                }

                validateDatetimeCondition(part, statusEntry);
            } else {
                // Not something the resource is interested in monitoring, skipping
            }
        }
         */
    }

    void validateWebReply(MonitoredData result, String name) {

        /*
         * std::string data = repliedData->getData();
         * MonitoredPart* webPart = parts[CONFIG_WEB];
         * 
         * std::string title = webPart->getItems()[KEY_TITLE];
         *  if(!title.empty()) {
         *     std::string needle = HTML_TITLE + title;
         *      std::size_t found = data.find(needle);
         *      if(found == std::string::npos) {
         *          errors.push_back("Web reply for url " + webPart->getItems()[KEY_URL] + " failed to validate title");
         *          healthy = false;
         *      }
         * } else if(data.length() < 3) {
         *      errors.push_back("Web reply for url " + webPart->getItems()[KEY_URL] + " resulted in empty response");
         *      healthy = false;
         * }
         */
    }

}

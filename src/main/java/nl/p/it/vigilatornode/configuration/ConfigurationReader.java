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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import nl.p.it.vigilatornode.exception.CustomException;
import nl.p.it.vigilatornode.exception.UnstartableException;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.WARNING;

/**
 * Reader to read configuration file using reflection
 * <p>
 * Reflection has limited performance but it is only used once at start up.
 * </p>
 * <p>
 * Will find the corresponding setter based on `get` + `key capitalised`
 * </p>
 * @author Patrick
 */
public class ConfigurationReader implements Reader {

    private final StringBuilder sb;
    private final String environment;

    private static final String SET = "set";
    private static final String APP_PROPERTIES = "/%s.app.properties";

    private static final System.Logger LOGGER = System.getLogger(ConfigurationReader.class.getName());

    public ConfigurationReader(final String environment) {
        this.environment = environment;
        sb = new StringBuilder();
    }

    @Override
    public void load(final NodeConfig config) throws UnstartableException {
        if (config != null) {
            try (InputStream configurationFile = getClass().getResourceAsStream(String.format(APP_PROPERTIES, environment))) {
                read(configurationFile, config);
            } catch (IOException ex) {
                LOGGER.log(ERROR, """
                    Not able to read app.properties file in the classpath, 
                    resolve this issue in order to use the application, 
                    exception: {0}""", ex);
                throw new UnstartableException(CustomException.MISSING_APP_PROPERTIES);
            }
        } else {
            LOGGER.log(WARNING, "ConfigurationReader.load() was called without an instance of NodeConfig");
        }
    }

    private void read(final InputStream configurationFile, final NodeConfig config) throws UnstartableException {
        try (InputStream is = new BufferedInputStream(configurationFile)) {
            Properties properties = new Properties();
            properties.load(is);

            setProperties(properties, config);
        } catch (IOException ex) {
            LOGGER.log(ERROR, """
                Error while reading app.properties file in the classpath, resolve 
                this issue in order to use the application, exception: {0}""", ex);
            throw new UnstartableException(CustomException.INVALID_APP_PROPERTIES);
        }
    }

    private void setProperties(final Properties properties, final NodeConfig config) throws UnstartableException {
        Map<String, Method> methods = mapMethods(config.getClass().getMethods());
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String property = entry.getKey().toString();
            readPropertyValue(property, entry.getValue(), methods, config);
        }
    }

    private void readPropertyValue(
            final String property,
            final Object value,
            final Map<String, Method> methods,
            final NodeConfig config) throws UnstartableException {
        String setter = getSetter(property);
        if (methods.containsKey(setter)) {
            Method method = methods.get(setter);
            try {
                Type type = method.getParameterTypes()[0];
                if (type.equals(int.class)) {
                    method.invoke(config, Integer.valueOf(value.toString()));
                } else if(type.equals(boolean.class)){
                    method.invoke(config, Boolean.valueOf(value.toString()));                    
                } else {
                    method.invoke(config, value);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOGGER.log(ERROR, """
                            Error occurred while attempting to set the value 
                            of property: {0}, exception: {1}""", property, ex);
                throw new UnstartableException(CustomException.INVALID_APP_PROPERTIES);
            }
        } else {
            LOGGER.log(WARNING, "No setter found for property: {0}", property);
        }
    }

    private String getSetter(final String property) {
        sb.setLength(0);
        sb.append(SET);
        sb.append(property.substring(0, 1).toUpperCase());
        sb.append(property.substring(1));

        return sb.toString();
    }

    private Map<String, Method> mapMethods(final Method[] methods) {
        Map<String, Method> mappedMethods = new HashMap<>();
        for (Method method : methods) {
            mappedMethods.put(method.getName(), method);
        }

        return mappedMethods;
    }
}

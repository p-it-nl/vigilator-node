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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import nl.p.it.vigilatornode.exception.VigilatorNodeException;
import nl.p.it.vigilatornode.exception.UnstartableException;

/**
 * Configuration for the application
 * <br>
 * the values in this configuration are read by the ConfigurationReader
 *
 * @see ConfigurationReader
 * @author Patrick
 */
public class NodeConfig {

    private final String environment;
    private int port;
    private int corePoolSize;
    private int maxThreads;
    private int maxQueuedTasks;
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private String resourceFilesLocation;
    private String logfile;
    private int defaultUpdateFrequency;

    private static NodeConfig instance;

    private NodeConfig(final String environment) throws UnstartableException {
        this.environment = environment;
        read(environment);
    }

    /**
     * Will load from properties file
     * <br>
     * This method throws UnstartableException when configuration loading fails,
     * this makes sure the application does not start without successfully
     * loading the configuration
     *
     * @param environment the environment to run the application for,
     * corresponding to the {enviroment}.app.properties file in de src/resources
     * folder.
     * @return the constructed instance
     * @throws VigilatorNodeException when reading the configuration fails
     */
    public static synchronized NodeConfig load(final String environment) throws VigilatorNodeException {
        instance = new NodeConfig(environment);

        return instance;
    }

    /**
     * Returns instance of application configuration
     *
     * @see NodeConfig.load()
     * @return the application configuration, or null when not loaded
     */
    public static synchronized NodeConfig getInstance() {
        return instance;
    }

    /**
     * @return the environment value this instance in running for
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * @return the server type
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * @param corePoolSize the amount of initial cores
     */
    public void setCorePoolSize(final int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * @return the amount of initial cores
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * @return the maximum amount of threads
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * @param maxThreads the maximum amount of threads
     */
    public void setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * @return the maximum amount of queued tasks
     */
    public int getMaxQueuedTasks() {
        return maxQueuedTasks;
    }

    /**
     * @param maxQueuedTasks the maximum amount of threads
     */
    public void setMaxQueuedTasks(final int maxQueuedTasks) {
        this.maxQueuedTasks = maxQueuedTasks;
    }

    /**
     * @return allowed origins
     */
    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * @param allowedOrigins the allowed origins
     */
    public void setAllowedOrigins(final String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * @return allowed methods
     */
    public String getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * @param allowedMethods the allowed methods
     */
    public void setAllowedMethods(final String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    /**
     * @return allowed headers
     */
    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    /**
     * @param allowedHeaders the allowed headers
     */
    public void setAllowedHeaders(final String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    /**
     * @param resourceFilesLocation the location from where to read resource
     * files
     */
    public void setResourceFilesLocation(final String resourceFilesLocation) {
        this.resourceFilesLocation = resourceFilesLocation;
    }

    /**
     * @return the location from where to read resource files
     */
    public String getResourceFilesLocation() {
        return resourceFilesLocation;
    }

    /**
     * @param logfile the file to write logs to, to set
     */
    public void setLogfile(final String logfile) {
        this.logfile = logfile;
    }

    /**
     * @return the file to write logs to
     */
    public String getLogfile() {
        return logfile;
    }

    /**
     * @param defaultUpdateFrequency the default frequency to set for the
     * monitor to be sending monitoring requests
     */
    public void setDefaultUpdateFrequency(final int defaultUpdateFrequency) {
        this.defaultUpdateFrequency = defaultUpdateFrequency;
    }

    /**
     * @return defaultUpdateFrequency the default frequency of the monitor
     * sending monitoring requests
     */
    public int getDefaultUpdateFrequency() {
        return defaultUpdateFrequency;
    }

    /**
     * Get a thread pool executor for the http server
     * <p>
     * starting with {corePoolSize} core pool size, having 1 to
     * {config.maxthreads} worker threads where each thread is killed after 60
     * seconds idle time having a fixed queue of maximum
     * {config.maxqueuedtasks}, where excess tasks are rejected by
     * CallerRunsPolicy
     * </p>
     * <p>
     * FUTURE_WORK: currently using same thread pool settings for both incoming
     * and outgoing requests. This is fine but when requirements change this
     * might be worthwhile to reevaluate
     * </p>
     *
     * @see java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy</p>
     * @return the thread pool executor
     */
    public ThreadPoolExecutor getPoolExecutor() {
        return new ThreadPoolExecutor(corePoolSize, maxThreads, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(maxQueuedTasks),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * Get a thread pool executor for internal processes
     * <p>
     * having 1 worker thread where each thread is never killed having a fixed
     * queue of maximum 100000, where excess tasks are rejected by
     * CallerRunsPolicy @see
     * java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy</p>
     * <p>
     * Tasks are guaranteed to execute sequentially, and no more than one task
     * will be active at any given time</p>
     *
     * @return the thread pool executor
     */
    public ThreadPoolExecutor getSingleThreadExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public String toString() {
        return "NodeConfig{"
                + "port=" + port
                + ", corePoolSize=" + corePoolSize
                + ", maxThreads=" + maxThreads
                + ", maxQueuedTasks=" + maxQueuedTasks
                + ", allowedOrigins=" + allowedOrigins
                + ", allowedMethods=" + allowedMethods
                + ", allowedHeaders=" + allowedHeaders
                + ", resourceFilesLocation=" + resourceFilesLocation
                + ", logfile=" + logfile
                + ", defaultUpdateFrequency=" + defaultUpdateFrequency + '}';
    }

    private void read(final String environment) throws UnstartableException {
        new ConfigurationReader(environment).load(this);
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.query.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.skywalking.oap.query.graphql.type.TimeInfo;
import org.apache.skywalking.oap.server.core.CoreModule;
import org.apache.skywalking.oap.server.core.query.MetadataQueryService;
import org.apache.skywalking.oap.server.core.query.input.Duration;
import org.apache.skywalking.oap.server.core.query.type.*;
import org.apache.skywalking.oap.server.library.module.ModuleManager;

public class MetadataQuery implements GraphQLQueryResolver {

    private final ModuleManager moduleManager;
    private MetadataQueryService metadataQueryService;

    public MetadataQuery(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    private MetadataQueryService getMetadataQueryService() {
        if (metadataQueryService == null) {
            this.metadataQueryService = moduleManager.find(CoreModule.NAME)
                                                     .provider()
                                                     .getService(MetadataQueryService.class);
        }
        return metadataQueryService;
    }

    /**
     * @return all 0 of metadata.
     */
    @Deprecated
    public ClusterBrief getGlobalBrief(final Duration duration, final String projectId) throws IOException, ParseException {
        return new ClusterBrief();
    }

    public List<Project> getProjects(final Duration duration, final List<String> projectNames) throws IOException, ParseException {
        return getMetadataQueryService().getProjects(projectNames);
    }

    public List<Service> getAllServices(final Duration duration, final String projectId) throws IOException, ParseException {
        return getMetadataQueryService().getAllServices(duration.getStartTimestamp(), duration.getEndTimestamp(), projectId);
    }

    public List<Service> getAllBrowserServices(final Duration duration, final String projectId) throws IOException, ParseException {
        return getMetadataQueryService().getAllBrowserServices(duration.getStartTimestamp(), duration.getEndTimestamp());
    }

    public List<Service> searchServices(final Duration duration, final String keyword, final String projectId) throws IOException, ParseException {
        return getMetadataQueryService().searchServices(duration.getStartTimestamp(), duration.getEndTimestamp(), keyword, projectId);
    }

    public Service searchService(final String serviceCode) throws IOException {
        return getMetadataQueryService().searchService(serviceCode);
    }

    public List<ServiceInstance> getServiceInstances(final Duration duration, final String serviceId) throws IOException, ParseException {
        return getMetadataQueryService().getServiceInstances(duration.getStartTimestamp(), duration.getEndTimestamp(), serviceId);
    }

    public List<Endpoint> searchEndpoint(final String keyword, final String serviceId, final int limit) throws IOException {
        return getMetadataQueryService().searchEndpoint(keyword, serviceId, limit);
    }

    public EndpointInfo getEndpointInfo(final String endpointId) throws IOException {
        return getMetadataQueryService().getEndpointInfo(endpointId);
    }

    public List<Database> getAllDatabases(final Duration duration, final String projectId) throws IOException {
        return getMetadataQueryService().getAllDatabases();
    }

    public TimeInfo getTimeInfo() {
        TimeInfo timeInfo = new TimeInfo();
        SimpleDateFormat timezoneFormat = new SimpleDateFormat("ZZZZZZ");
        Date date = new Date();
        timeInfo.setCurrentTimestamp(date.getTime());
        timeInfo.setTimezone(timezoneFormat.format(date));
        return timeInfo;
    }
}

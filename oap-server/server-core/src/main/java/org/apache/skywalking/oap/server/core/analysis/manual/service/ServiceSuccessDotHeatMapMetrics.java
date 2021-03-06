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

package org.apache.skywalking.oap.server.core.analysis.manual.service;

import org.apache.skywalking.oap.server.core.Const;
import org.apache.skywalking.oap.server.core.analysis.Stream;
import org.apache.skywalking.oap.server.core.analysis.metrics.HistogramMetrics;
import org.apache.skywalking.oap.server.core.analysis.metrics.Metrics;
import org.apache.skywalking.oap.server.core.analysis.metrics.MetricsMetaInfo;
import org.apache.skywalking.oap.server.core.analysis.metrics.WithMetadata;
import org.apache.skywalking.oap.server.core.analysis.metrics.DataTable;
import org.apache.skywalking.oap.server.core.analysis.worker.SecondMetricsStreamProcessor;
import org.apache.skywalking.oap.server.core.remote.grpc.proto.RemoteData;
import org.apache.skywalking.oap.server.core.source.DefaultScopeDefine;
import org.apache.skywalking.oap.server.core.storage.StorageBuilder;
import org.apache.skywalking.oap.server.core.storage.annotation.Column;

import java.util.HashMap;
import java.util.Map;

@Stream(name = ServiceSuccessDotHeatMapMetrics.INDEX_NAME, scopeId = DefaultScopeDefine.SERVICE, builder = ServiceSuccessDotHeatMapMetrics.Builder.class, processor = SecondMetricsStreamProcessor.class)
public class ServiceSuccessDotHeatMapMetrics extends HistogramMetrics implements WithMetadata {

    public static final String INDEX_NAME = "service_success_dot_heatmap";

    public static final String ENTITY_ID = "entity_id";
    public static final String PROJECT_ID = "project_id";

    @Column(columnName = ENTITY_ID, length = 512)
    private String entityId;
    @Column(columnName = PROJECT_ID, length = 256)
    private String projectId;

    public ServiceSuccessDotHeatMapMetrics() {
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String id() {
        return getTimeBucket() + Const.ID_CONNECTOR + entityId;
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + this.entityId.hashCode();
        result = 31 * result + (int) this.getTimeBucket();
        return result;
    }

    public int remoteHashCode() {
        int result = 17;
        result = 31 * result + this.entityId.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            ServiceSuccessDotHeatMapMetrics var2 = (ServiceSuccessDotHeatMapMetrics) obj;
            if (!this.entityId.equals(var2.entityId)) {
                return false;
            } else {
                return this.getTimeBucket() == var2.getTimeBucket();
            }
        }
    }

    public RemoteData.Builder serialize() {
        RemoteData.Builder remoteBuilder = RemoteData.newBuilder();
        remoteBuilder.addDataStrings(this.getEntityId());
        remoteBuilder.addDataStrings(this.getProjectId());
        remoteBuilder.addDataLongs(this.getTimeBucket());
        remoteBuilder.addDataObjectStrings(this.getDataset().toStorageData());
        return remoteBuilder;
    }

    public void deserialize(RemoteData remoteBuilder) {
        this.setEntityId(remoteBuilder.getDataStrings(0));
        this.setProjectId(remoteBuilder.getDataStrings(1));
        this.setTimeBucket(remoteBuilder.getDataLongs(0));
        this.setDataset(new DataTable(remoteBuilder.getDataObjectStrings(0)));
    }

    public MetricsMetaInfo getMeta() {
        return new MetricsMetaInfo(INDEX_NAME, DefaultScopeDefine.SERVICE, this.entityId);
    }

    public Metrics toHour() {
        throw new UnsupportedOperationException("Just supported second metrics.");
    }

    public Metrics toDay() {
        throw new UnsupportedOperationException("Just supported second metrics.");
    }

    public static class Builder implements StorageBuilder<ServiceSuccessDotHeatMapMetrics> {

        public Map<String, Object> data2Map(ServiceSuccessDotHeatMapMetrics storageData) {
            Map<String, Object> map = new HashMap<>();
            map.put(ENTITY_ID, storageData.getEntityId());
            map.put(PROJECT_ID, storageData.getProjectId());
            map.put(DATASET, storageData.getDataset());
            map.put(TIME_BUCKET, storageData.getTimeBucket());
            return map;
        }

        public ServiceSuccessDotHeatMapMetrics map2Data(Map<String, Object> dbMap) {
            ServiceSuccessDotHeatMapMetrics metrics = new ServiceSuccessDotHeatMapMetrics();
            metrics.setEntityId((String) dbMap.get(ENTITY_ID));
            metrics.setProjectId((String) dbMap.get(PROJECT_ID));
            metrics.setDataset(new DataTable((String) dbMap.get(DATASET)));
            metrics.setTimeBucket(((Number) dbMap.get(TIME_BUCKET)).longValue());
            return metrics;
        }
    }
}
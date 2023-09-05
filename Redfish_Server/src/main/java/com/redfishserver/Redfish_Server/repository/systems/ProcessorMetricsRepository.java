package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ProcessorMetrics_ProcessorMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessorMetricsRepository extends MongoRepository<ProcessorMetrics_ProcessorMetrics, Object> {
}

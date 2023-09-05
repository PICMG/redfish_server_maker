package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ProcessorCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessorsCollectionRepository extends MongoRepository<ProcessorCollection, Object> {
}

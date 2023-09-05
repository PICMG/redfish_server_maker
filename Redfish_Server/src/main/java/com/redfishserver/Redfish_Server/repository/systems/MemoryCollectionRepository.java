package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.MemoryCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemoryCollectionRepository extends MongoRepository<MemoryCollection, Object> {
}

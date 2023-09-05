package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.SimpleStorageCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SimpleStorageCollectionRepository extends MongoRepository<SimpleStorageCollection, Object> {
}

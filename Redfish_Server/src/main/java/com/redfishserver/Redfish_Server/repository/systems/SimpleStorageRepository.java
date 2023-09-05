package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.SimpleStorage_SimpleStorage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SimpleStorageRepository extends MongoRepository<SimpleStorage_SimpleStorage, Object> {
    SimpleStorage_SimpleStorage getById(String Id);
}

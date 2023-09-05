package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.USBControllerCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface USBControllerCollectionRepository extends MongoRepository<USBControllerCollection, Object> {
}

package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.GraphicsControllerCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GraphicsControllerCollectionRepository extends MongoRepository<GraphicsControllerCollection, Object> {
}

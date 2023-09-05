package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.GraphicsController_GraphicsController;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GraphicsControllerRepository extends MongoRepository<GraphicsController_GraphicsController, Object> {
    GraphicsController_GraphicsController getById(String Id);
}

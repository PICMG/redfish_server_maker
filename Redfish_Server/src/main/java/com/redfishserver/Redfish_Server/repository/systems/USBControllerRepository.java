package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.USBController_USBController;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface USBControllerRepository extends MongoRepository<USBController_USBController, Object> {
    USBController_USBController getById(String Id);
}

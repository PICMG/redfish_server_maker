package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ComputerSystem_ComputerSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComputerSystemRepository extends MongoRepository<ComputerSystem_ComputerSystem, Object> {

    ComputerSystem_ComputerSystem findById(String Id);
}

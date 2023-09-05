package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ComputerSystemCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComputerSystemsCollectionRepository extends MongoRepository<ComputerSystemCollection, Object> {
}

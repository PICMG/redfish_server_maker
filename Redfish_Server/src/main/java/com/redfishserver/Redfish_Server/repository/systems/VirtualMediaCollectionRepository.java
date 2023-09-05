package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.VirtualMediaCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VirtualMediaCollectionRepository extends MongoRepository<VirtualMediaCollection, Object> {
}

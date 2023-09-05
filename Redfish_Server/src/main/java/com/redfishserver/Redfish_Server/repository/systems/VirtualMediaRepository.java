package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.VirtualMedia_VirtualMedia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VirtualMediaRepository extends MongoRepository<VirtualMedia_VirtualMedia, Object> {
    VirtualMedia_VirtualMedia getById(String Id);
}

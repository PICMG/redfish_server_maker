package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.SecureBoot_SecureBoot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecureBootRespository extends MongoRepository<SecureBoot_SecureBoot, Object> {
}

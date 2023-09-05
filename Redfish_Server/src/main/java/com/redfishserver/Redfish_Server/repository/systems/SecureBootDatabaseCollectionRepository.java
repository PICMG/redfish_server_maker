package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.SecureBootDatabaseCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecureBootDatabaseCollectionRepository extends MongoRepository<SecureBootDatabaseCollection, Object> {

}

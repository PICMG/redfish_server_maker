package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.SecureBootDatabase_SecureBootDatabase;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecureBootDatabaseRepository extends MongoRepository<SecureBootDatabase_SecureBootDatabase, Object> {
    SecureBootDatabase_SecureBootDatabase getById(String Id);
}

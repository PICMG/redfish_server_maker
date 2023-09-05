package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.Bios_Bios;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BiosRespository extends MongoRepository<Bios_Bios, Object> {
    Bios_Bios getById(String Id);
}

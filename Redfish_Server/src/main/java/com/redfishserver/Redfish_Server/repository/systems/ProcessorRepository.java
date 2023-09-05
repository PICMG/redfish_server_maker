package com.redfishserver.Redfish_Server.repository.systems;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.Processor_Processor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessorRepository extends MongoRepository<Processor_Processor, Object> {
    Processor_Processor getById(String Id);
}

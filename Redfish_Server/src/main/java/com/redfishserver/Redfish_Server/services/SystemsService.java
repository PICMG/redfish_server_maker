//******************************************************************************************************
// SystemService.java
//
// System service according to redfish specification.
//
//Copyright (C) 2022, PICMG.
//
//        This program is free software: you can redistribute it and/or modify
//        it under the terms of the GNU General Public License as published by
//        the Free Software Foundation, either version 3 of the License, or
//        (at your option) any later version.
//
//        This program is distributed in the hope that it will be useful,
//        but WITHOUT ANY WARRANTY; without even the implied warranty of
//        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//        GNU General Public License for more details.
//
//        You should have received a copy of the GNU General Public License
//        along with this program.  If not, see <https://www.gnu.org/licenses/>.
//*******************************************************************************************************


package com.redfishserver.Redfish_Server.services;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.*;
import com.redfishserver.Redfish_Server.repository.chassis.CertificateCollectionRepository;
import com.redfishserver.Redfish_Server.repository.chassis.CertificateRepository;
import com.redfishserver.Redfish_Server.repository.chassis.EnvironmentMetricsRepository;
import com.redfishserver.Redfish_Server.repository.managers.*;
import com.redfishserver.Redfish_Server.repository.systems.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class SystemsService {

    @Value("${async.task.wait-time}")
    long taskWaitTime;

    @Autowired
    TaskService taskService;
    @Autowired
    ComputerSystemsCollectionRepository computerSystemsCollectionRepository;

    @Autowired
    ComputerSystemRepository computerSystemRepository;

    @Autowired
    BiosRespository biosRespository;

    @Autowired
    SecureBootRespository secureBootRespository;

    @Autowired
    SecureBootDatabaseCollectionRepository secureBootDatabaseCollectionRepository;

    @Autowired
    SecureBootDatabaseRepository secureBootDatabaseRepository;

    @Autowired
    ProcessorsCollectionRepository processorsCollectionRepository;

    @Autowired
    ProcessorRepository processorRepository;

    @Autowired
    EnvironmentMetricsRepository environmentMetricsRepository;

    @Autowired
    ProcessorMetricsRepository processorMetricsRepository;

    @Autowired
    MemoryCollectionRepository memoryCollectionRepository;

    @Autowired
    MemoryRepository memoryRepository;

    @Autowired
    EthernetInterfaceCollectionRepository ethernetInterfaceCollectionRepository;

    @Autowired
    EthernetInterfaceRepository ethernetInterfaceRepository;

    @Autowired
    SimpleStorageCollectionRepository simpleStorageCollectionRepository;

    @Autowired
    SimpleStorageRepository simpleStorageRepository;

    @Autowired
    LogServiceCollectionRepository logServiceCollectionRepository;

    @Autowired
    LogServiceRepository logServiceRepository;

    @Autowired
    GraphicsControllerCollectionRepository graphicsControllerCollectionRepository;

    @Autowired
    GraphicsControllerRepository graphicsControllerRepository;

    @Autowired
    USBControllerCollectionRepository usbControllerCollectionRepository;

    @Autowired
    USBControllerRepository usbControllerRepository;

    @Autowired
    CertificateCollectionRepository certificateCollectionRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    VirtualMediaCollectionRepository virtualMediaCollectionRepository;

    @Autowired
    VirtualMediaRepository virtualMediaRepository;

    @Autowired
    SecurityPolicyRepository securityPolicyRepository;

    public List<SecurityPolicy_SecurityPolicy> getSecurityPolicy(){ return securityPolicyRepository.findAll();}

    @Async
    public Future<VirtualMedia_VirtualMedia> getVirtualMedia(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String virtualMediaObjectId){
        List<VirtualMedia_VirtualMedia> all = virtualMediaRepository.findAll();
        VirtualMedia_VirtualMedia virtualMed = null;
        for(VirtualMedia_VirtualMedia virtualMedia : all){
            if(virtualMedia.getAtOdataId().contains(systemCollectionId) && virtualMedia.getAtOdataId().contains(virtualMediaObjectId))
                virtualMed = virtualMedia;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, virtualMed);
        }
        return new AsyncResult<VirtualMedia_VirtualMedia>(virtualMed);
    }

    @Async
    public Future<Certificate_Certificate> getVirtualMediaCertificate(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String virtualMediaObjectId, String certificateObjectId){
        List<Certificate_Certificate> all = certificateRepository.findAll();
        Certificate_Certificate cert = null;
        for(Certificate_Certificate certificate : all){
            if(certificate.getAtOdataId() != null) {
                if (certificate.getAtOdataId().contains(systemCollectionId) && certificate.getAtOdataId().contains(virtualMediaObjectId) && certificate.getAtOdataId().contains(certificateObjectId))
                    cert = certificate;
            }
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, cert);
        }
        return new AsyncResult<Certificate_Certificate>(cert);
    }

    @Async
    public Future<Certificate_Certificate> getCertificate(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String certificateObjectId){
        List<Certificate_Certificate> all = certificateRepository.findAll();
        Certificate_Certificate cert = null;
        for(Certificate_Certificate certificate : all){
            if(certificate.getAtOdataId() == null) {
                if(certificate.getId().contains(certificateObjectId))
                    cert = certificate;
            }
            else{
                if(certificate.getAtOdataId().contains(systemCollectionId) && certificate.getAtOdataId().contains(certificateObjectId))
                    cert = certificate;
            }
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, cert);
        }
        return new AsyncResult<Certificate_Certificate>(cert);
    }

    @Async
    public Future<List<CertificateCollection>> getVirtualMediaCertificateCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String virtualMediaObjectId){
        List<CertificateCollection> all = certificateCollectionRepository.findAll();
        List<CertificateCollection> certificateCollectionList = new ArrayList<>();
        for(CertificateCollection certificateCollection : all){
            if(certificateCollection.getAtOdataId().contains(systemCollectionId) && certificateCollection.getAtOdataId().contains(virtualMediaObjectId))
                certificateCollectionList.add(certificateCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, certificateCollectionList);
        }
        return new AsyncResult<List<CertificateCollection>>(certificateCollectionList);
    }

    @Async
    public Future<List<CertificateCollection>> getCertificateCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<CertificateCollection> all = certificateCollectionRepository.findAll();
        List<CertificateCollection> certificateCollectionList = new ArrayList<>();
        for(CertificateCollection certificateCollection : all){
            if(certificateCollection.getAtOdataId().contains(systemCollectionId))
                certificateCollectionList.add(certificateCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, certificateCollectionList);
        }
        return new AsyncResult<List<CertificateCollection>>(certificateCollectionList);
    }

    @Async
    public Future<USBController_USBController> getUSBController(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String uSBControllerObjectId){
        List<USBController_USBController> all = usbControllerRepository.findAll();
        USBController_USBController usbController = null;
        for(USBController_USBController usbController_usbController : all){
            if(usbController_usbController.getAtOdataId().contains(systemCollectionId) && usbController_usbController.getAtOdataId().contains(uSBControllerObjectId))
                usbController = usbController_usbController;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, usbController);
        }
        return new AsyncResult<USBController_USBController>(usbController);
    }

    @Async
    public Future<List<USBControllerCollection>> getUSBControllerCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<USBControllerCollection> all = usbControllerCollectionRepository.findAll();
        List<USBControllerCollection> usbControllerCollectionList = new ArrayList<>();
        for(USBControllerCollection usbControllerCollection : all){
            if(usbControllerCollection.getAtOdataId().contains(systemCollectionId))
                usbControllerCollectionList.add(usbControllerCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, usbControllerCollectionList);
        }
        return new AsyncResult<List<USBControllerCollection>>(usbControllerCollectionList);
    }

    @Async
    public Future<GraphicsController_GraphicsController> getGraphicsController(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String graphicsControllerObjectId){
        List<GraphicsController_GraphicsController> all = graphicsControllerRepository.findAll();
        GraphicsController_GraphicsController graphicsController = null;
        for(GraphicsController_GraphicsController graphicsController_graphicsController : all){
            if(graphicsController_graphicsController.getAtOdataId().contains(systemCollectionId) && graphicsController_graphicsController.getAtOdataId().contains(graphicsControllerObjectId))
                graphicsController = graphicsController_graphicsController;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, graphicsController);
        }
        return new AsyncResult<GraphicsController_GraphicsController>(graphicsController);
    }

    @Async
    public Future<List<GraphicsControllerCollection>> getGraphicsControllerCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<GraphicsControllerCollection> all = graphicsControllerCollectionRepository.findAll();
        List<GraphicsControllerCollection> graphicsControllerCollectionList = new ArrayList<>();
        for(GraphicsControllerCollection graphicsControllerCollection : all){
            if(graphicsControllerCollection.getAtOdataId().contains(systemCollectionId))
                graphicsControllerCollectionList.add(graphicsControllerCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, graphicsControllerCollectionList);
        }
        return new AsyncResult<List<GraphicsControllerCollection>>(graphicsControllerCollectionList);
    }

    @Async
    public Future<LogService_LogService> getLogService(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String logServiceObjectId){
        List<LogService_LogService> all = logServiceRepository.findAll();
        LogService_LogService lService = null;
        for(LogService_LogService logService : all){
            if(logService.getAtOdataId().contains(systemCollectionId) && logService.getAtOdataId().contains(logServiceObjectId))
                lService = logService;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, lService);
        }
        return new AsyncResult<LogService_LogService>(lService);
    }

    @Async
    public Future<List<LogServiceCollection>> getLogServiceCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<LogServiceCollection> all = logServiceCollectionRepository.findAll();
        List<LogServiceCollection> logServiceCollectionList = new ArrayList<>();
        for(LogServiceCollection logServiceCollection : all){
            if(logServiceCollection.getAtOdataId().contains(systemCollectionId))
                logServiceCollectionList.add(logServiceCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, logServiceCollectionList);
        }
        return new AsyncResult<List<LogServiceCollection>>(logServiceCollectionList);
    }

    @Async
    public Future<SimpleStorage_SimpleStorage> getSimpleStorage(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String simpleStorageObjectId){
        List<SimpleStorage_SimpleStorage> all = simpleStorageRepository.findAll();
        SimpleStorage_SimpleStorage simpleStorage = null;
        for(SimpleStorage_SimpleStorage simpleStorage_simpleStorage : all){
            if(simpleStorage_simpleStorage.getAtOdataId().contains(systemCollectionId) && simpleStorage_simpleStorage.getAtOdataId().contains(simpleStorageObjectId))
                simpleStorage = simpleStorage_simpleStorage;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, simpleStorage);
        }
        return new AsyncResult<SimpleStorage_SimpleStorage>(simpleStorage);
    }

    @Async
    public Future<List<SimpleStorageCollection>> getSimpleStorageCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<SimpleStorageCollection> all = simpleStorageCollectionRepository.findAll();
        List<SimpleStorageCollection> simpleStorageCollectionList = new ArrayList<>();
        for(SimpleStorageCollection simpleStorageCollection : all){
            if(simpleStorageCollection.getAtOdataId().contains(systemCollectionId))
                simpleStorageCollectionList.add(simpleStorageCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, simpleStorageCollectionList);
        }
        return new AsyncResult<List<SimpleStorageCollection>>(simpleStorageCollectionList);
    }

    @Async
    public Future<EthernetInterface_EthernetInterface> getEthernetInterface(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String ethernetInterfaceObjectId){
        List<EthernetInterface_EthernetInterface> all = ethernetInterfaceRepository.findAll();
        EthernetInterface_EthernetInterface eInterface = null;
        for(EthernetInterface_EthernetInterface ethernetInterface : all){
            if(ethernetInterface.getAtOdataId().contains(systemCollectionId) && ethernetInterface.getAtOdataId().contains(ethernetInterfaceObjectId))
                eInterface = ethernetInterface;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, eInterface);
        }
        return new AsyncResult<EthernetInterface_EthernetInterface>(eInterface);
    }

    @Async
    public Future<List<EthernetInterfaceCollection>> getEthernetInterfaceCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<EthernetInterfaceCollection> all = ethernetInterfaceCollectionRepository.findAll();
        List<EthernetInterfaceCollection> ethernetInterfaceCollectionList = new ArrayList<>();
        for(EthernetInterfaceCollection ethernetInterfaceCollection : all){
            if(ethernetInterfaceCollection.getAtOdataId().contains(systemCollectionId))
                ethernetInterfaceCollectionList.add(ethernetInterfaceCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, ethernetInterfaceCollectionList);
        }
        return new AsyncResult<List<EthernetInterfaceCollection>>(ethernetInterfaceCollectionList);
    }

    @Async
    public Future<Memory_Memory> getMemory(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String memoryObjectId){
        List<Memory_Memory> memory_memoryList = memoryRepository.findAll();
        Memory_Memory memory = null;
        for(Memory_Memory memory_memory : memory_memoryList){
            if(memory_memory.getAtOdataId().contains(systemCollectionId) && memory_memory.getAtOdataId().contains(memoryObjectId))
                memory = memory_memory;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, memory);
        }
        return new AsyncResult<Memory_Memory>(memory);
    }

    @Async
    public Future<List<MemoryCollection>> getMemoryCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<MemoryCollection> all = memoryCollectionRepository.findAll();
        List<MemoryCollection> memoryCollectionList = new ArrayList<>();
        for(MemoryCollection memoryCollection : all){
            if(memoryCollection.getAtOdataId().contains(systemCollectionId))
                memoryCollectionList.add(memoryCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, memoryCollectionList);
        }
        return new AsyncResult<List<MemoryCollection>>(memoryCollectionList);
    }

    @Async
    public Future<List<ProcessorMetrics_ProcessorMetrics>> getProcessorMetrics(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String processorObjectId){
        List<ProcessorMetrics_ProcessorMetrics> all = processorMetricsRepository.findAll();
        List<ProcessorMetrics_ProcessorMetrics> processorMetricsList = new ArrayList<>();
        for(ProcessorMetrics_ProcessorMetrics processorMetrics : all){
            if(processorMetrics.getAtOdataId().contains(systemCollectionId) && processorMetrics.getAtOdataId().contains(processorObjectId))
                processorMetricsList.add(processorMetrics);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, processorMetricsList);
        }
        return new AsyncResult<List<ProcessorMetrics_ProcessorMetrics>>(processorMetricsList);
    }

    @Async
    public Future<List<EnvironmentMetrics_EnvironmentMetrics>> getEnvironmentMetrics(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String processorObjectId){
        List<EnvironmentMetrics_EnvironmentMetrics> all = environmentMetricsRepository.findAll();
        List<EnvironmentMetrics_EnvironmentMetrics> environmentMetrics_environmentMetricsList = new ArrayList<>();
        for(EnvironmentMetrics_EnvironmentMetrics environmentMetrics_environmentMetrics : all){
            if(environmentMetrics_environmentMetrics.getAtOdataId().contains(systemCollectionId) && environmentMetrics_environmentMetrics.getAtOdataId().contains(processorObjectId))
                environmentMetrics_environmentMetricsList.add(environmentMetrics_environmentMetrics);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, environmentMetrics_environmentMetricsList);
        }
        return new AsyncResult<List<EnvironmentMetrics_EnvironmentMetrics>>(environmentMetrics_environmentMetricsList);
    }

    @Async
    public Future<Processor_Processor> getProcessor(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String processorObjectId){
        List<Processor_Processor> processor_processors = processorRepository.findAll();
        Processor_Processor processor = null;
        for(Processor_Processor processor_processor : processor_processors){
            if(processor_processor.getAtOdataId().contains(processorObjectId) && processor_processor.getAtOdataId().contains(systemCollectionId))
                processor = processor_processor;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, processor);
        }
        return new AsyncResult<Processor_Processor>(processor);
    }

    @Async
    public Future<List<ProcessorCollection>> getProcessorCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<ProcessorCollection> all = processorsCollectionRepository.findAll();
        List<ProcessorCollection> processorCollectionList = new ArrayList<>();
        for(ProcessorCollection processorCollection : all){
            if(processorCollection.getAtOdataId().contains(systemCollectionId))
                processorCollectionList.add(processorCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, processorCollectionList);
        }
        return new AsyncResult<List<ProcessorCollection>>(processorCollectionList);
    }

    @Async
    public Future<SecureBootDatabase_SecureBootDatabase> getSecureBootDatabase(OffsetDateTime startTime, Integer taskId, String systemCollectionId, String secureBootDatabaseObjectId){
        List<SecureBootDatabase_SecureBootDatabase> secureBootDatabase_secureBootDatabaseList = secureBootDatabaseRepository.findAll();
        SecureBootDatabase_SecureBootDatabase secureBootDatabase = null;
        for(SecureBootDatabase_SecureBootDatabase secureBootDatabase_secureBootDatabase : secureBootDatabase_secureBootDatabaseList){
            if(secureBootDatabase_secureBootDatabase.getAtOdataId().contains(systemCollectionId) && secureBootDatabase_secureBootDatabase.getAtOdataId().contains(secureBootDatabaseObjectId))
                secureBootDatabase = secureBootDatabase_secureBootDatabase;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, secureBootDatabase);
        }
        return new AsyncResult<SecureBootDatabase_SecureBootDatabase>(secureBootDatabase);
    }

    @Async
    public Future<List<SecureBootDatabaseCollection>> getSecureBootDatabaseCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<SecureBootDatabaseCollection> all = secureBootDatabaseCollectionRepository.findAll();
        List<SecureBootDatabaseCollection> secureBootDatabaseCollectionList = new ArrayList<>();
        for(SecureBootDatabaseCollection secureBootDatabaseCollection : all){
            if(secureBootDatabaseCollection.getAtOdataId().contains(systemCollectionId))
                secureBootDatabaseCollectionList.add(secureBootDatabaseCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, secureBootDatabaseCollectionList);
        }
        return new AsyncResult<List<SecureBootDatabaseCollection>>(secureBootDatabaseCollectionList);
    }

    @Async
    public Future<List<SecureBoot_SecureBoot>> getSecureBoot(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<SecureBoot_SecureBoot> all = secureBootRespository.findAll();
        List<SecureBoot_SecureBoot> secureBoot_secureBootList = new ArrayList<>();
        for(SecureBoot_SecureBoot secureBoot_secureBoot : all){
            if(secureBoot_secureBoot.getAtOdataId().contains(systemCollectionId))
                secureBoot_secureBootList.add(secureBoot_secureBoot);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, secureBoot_secureBootList);
        }
        return new AsyncResult<List<SecureBoot_SecureBoot>>(secureBoot_secureBootList);
    }

    @Async
    public Future<Bios_Bios> getSettings(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<Bios_Bios> bios_biosList =  biosRespository.findAll();
        Bios_Bios bios = null;
        for(Bios_Bios bios_bios : bios_biosList){
            if(bios_bios.getAtOdataId().contains(systemCollectionId) && bios_bios.getAtOdataId().contains("Settings"))
                bios = bios_bios;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, bios_biosList);
        }
        return new AsyncResult<Bios_Bios>(bios);
    }

    @Async
    public Future<List<Bios_Bios>> getBios(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<Bios_Bios> allBiosList = biosRespository.findAll();
        List<Bios_Bios> bios_biosList = new ArrayList<>();
        for(Bios_Bios bios_bios : allBiosList){
            if(bios_bios.getAtOdataId().contains(systemCollectionId))
                bios_biosList.add(bios_bios);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, bios_biosList);
        }
        return new AsyncResult<List<Bios_Bios>>(bios_biosList);
    }

    @Async
    public Future<ComputerSystem_ComputerSystem> getComputerSystem(OffsetDateTime startTime, Integer taskId, String systemObjectId){
        ComputerSystem_ComputerSystem computerSystem_computerSystem = computerSystemRepository.findById(systemObjectId);
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, computerSystem_computerSystem);
        }
        return new AsyncResult<ComputerSystem_ComputerSystem>(computerSystem_computerSystem);
    }

    @Async
    public Future<List<ComputerSystemCollection>> getComputerSystemCollection(OffsetDateTime startTime, Integer taskId){
        List<ComputerSystemCollection> computerSystemCollectionList = computerSystemsCollectionRepository.findAll();
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, computerSystemCollectionList);
        }
        return new AsyncResult<List<ComputerSystemCollection>>(computerSystemCollectionList);
    }

    @Async
    public Future<List<VirtualMediaCollection>> getVirtualMediaCollection(OffsetDateTime startTime, Integer taskId, String systemCollectionId){
        List<VirtualMediaCollection> all = virtualMediaCollectionRepository.findAll();
        List<VirtualMediaCollection> virtualMediaCollectionList = new ArrayList<>();
        for(VirtualMediaCollection virtualMediaCollection : all){
            if(virtualMediaCollection.getAtOdataId().contains(systemCollectionId))
                virtualMediaCollectionList.add(virtualMediaCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, virtualMediaCollectionList);
        }
        return new AsyncResult<List<VirtualMediaCollection>>(virtualMediaCollectionList);
    }

    public Integer getTaskId() {
        return taskService.getMaxTaskCount();
    }

    public String getTaskServiceURI(String newTaskId) {
        return taskService.getTaskServiceURI() + newTaskId + "/monitor";
    }

    public void createTaskForOperation(OffsetDateTime startTime, Integer newTaskId, String uri) {
        taskService.createTaskForAsyncOperation(startTime, newTaskId, uri);
    }

    public Task_Task getTaskResource(String Id) {
        return taskService.getTask(Id);
    }

}

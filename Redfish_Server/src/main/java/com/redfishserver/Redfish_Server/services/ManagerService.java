//******************************************************************************************************
// ManagerService.java
//
// Manager service according to redfish specification.
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
import com.redfishserver.Redfish_Server.repository.managers.*;
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
public class ManagerService {

    @Value("${async.task.wait-time}")
    long taskWaitTime;

    @Autowired
    TaskService taskService;
    @Autowired
    ManagerCollectionRepository managerCollectionRepository;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    ManagerNetworkProtocolRepository managerNetworkProtocolRepository;

    @Autowired
    CertificateCollectionRepository certificateCollectionRepository;

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    EthernetInterfaceCollectionRepository ethernetInterfaceCollectionRepository;

    @Autowired
    EthernetInterfaceRepository ethernetInterfaceRepository;

    @Autowired
    PortCollectionRepository portCollectionRepository;

    @Autowired
    PortRepository portRepository;

    @Autowired
    HostInterfaceCollectionRepository hostInterfaceCollectionRepository;

    @Autowired
    HostInterfaceRepository hostInterfaceRepository;

    @Autowired
    SerialInterfaceCollectionRepository serialInterfaceCollectionRepository;

    @Autowired
    SerialInterfaceRepository serialInterfaceRepository;

    @Autowired
    LogServiceCollectionRepository logServiceCollectionRepository;

    @Autowired
    LogServiceRepository logServiceRepository;

    @Autowired
    SecurityPolicyRepository securityPolicyRepository;

    @Async
    public Future<List<SecurityPolicy_SecurityPolicy>> getSecurityPolicy(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<SecurityPolicy_SecurityPolicy> all = securityPolicyRepository.findAll();
        List<SecurityPolicy_SecurityPolicy> securityPolicyList = new ArrayList<>();
        for(SecurityPolicy_SecurityPolicy securityPolicy : all){
            if(securityPolicy.getAtOdataId().contains(managerObjectId))
                securityPolicyList.add(securityPolicy);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, securityPolicyList);
        }
        return new AsyncResult<List<SecurityPolicy_SecurityPolicy>>(securityPolicyList);
    }

    @Async
    public Future<Certificate_Certificate> getManagerCertificate2(OffsetDateTime startTime, Integer taskId, String managerObjectId, String obj1, String certType, String cert){
        List<Certificate_Certificate> certificateList = certificateRepository.findAll();
        Certificate_Certificate certificate1 = null;
        for(Certificate_Certificate certificate : certificateList){
            if(certificate.getAtOdataId().contains(managerObjectId) && certificate.getAtOdataId().contains(obj1)
                    && certificate.getAtOdataId().contains(certType) && certificate.getAtOdataId().contains(cert))
                certificate1 = certificate;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, certificate1);
        }
        return new AsyncResult<Certificate_Certificate>(certificate1);
    }

    @Async
    public Future<List<CertificateCollection>> getManagerCertificateCollection(OffsetDateTime startTime, Integer taskId, String managerObjectId, String certType){
        List<CertificateCollection> all = certificateCollectionRepository.findAll();
        List<CertificateCollection> certificateCollectionList = new ArrayList<>();
        for(CertificateCollection certificateCollection : all){
            if(certificateCollection.getAtOdataId().contains(managerObjectId) && certificateCollection.getAtOdataId().contains(certType))
                certificateCollectionList.add(certificateCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, certificateCollectionList);
        }
        return new AsyncResult<List<CertificateCollection>>(certificateCollectionList);
    }

    @Async
    public Future<List<CertificateCollection>> getManagerCertificateCollection2(OffsetDateTime startTime, Integer taskId, String managerObjectId, String obj1, String certType){
        List<CertificateCollection> all = certificateCollectionRepository.findAll();
        List<CertificateCollection> certificateCollectionList = new ArrayList<>();
        for(CertificateCollection certificateCollection : all){
            if(certificateCollection.getAtOdataId().contains(managerObjectId) && certificateCollection.getAtOdataId().contains(obj1) && certificateCollection.getAtOdataId().contains(certType))
                certificateCollectionList.add(certificateCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, certificateCollectionList);
        }
        return new AsyncResult<List<CertificateCollection>>(certificateCollectionList);
    }

    @Async
    public Future<Certificate_Certificate> getManagerCertificate(OffsetDateTime startTime, Integer taskId, String managerObjectId, String certType, String cert){
        List<Certificate_Certificate> certificateList = certificateRepository.findAll();
        Certificate_Certificate certificate1 = null;
        for(Certificate_Certificate certificate : certificateList){
            if(certificate.getAtOdataId().contains(managerObjectId)
                    && certificate.getAtOdataId().contains(certType) && certificate.getAtOdataId().contains(cert))
                certificate1 = certificate;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, certificate1);
        }
        return new AsyncResult<Certificate_Certificate>(certificate1);
    }

    @Async
    public Future<LogService_LogService> getLogService(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<LogService_LogService> logServiceList = logServiceRepository.findAll();
        LogService_LogService logService = null;
        for(LogService_LogService lService : logServiceList){
            if(lService.getAtOdataId().contains(managerObjectId))
                logService = lService;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, logService);
        }
        return new AsyncResult<LogService_LogService>(logService);
    }

    @Async
    public Future<LogServiceCollection> getLogServiceCollection(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<LogServiceCollection> logServiceCollectionList = logServiceCollectionRepository.findAll();
        LogServiceCollection logServiceCollection = null;
        for(LogServiceCollection lServiceCollection : logServiceCollectionList){
            if(lServiceCollection.getAtOdataId().contains(managerObjectId))
                logServiceCollection = lServiceCollection;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, logServiceCollection);
        }
        return new AsyncResult<LogServiceCollection>(logServiceCollection);
    }

    @Async
    public Future<SerialInterface_SerialInterface> getSerialInterface(OffsetDateTime startTime, Integer taskId, String managerObjectId, String serialInterfaceObjectId){
        List<SerialInterface_SerialInterface> serialInterfaceList = serialInterfaceRepository.findAll();
        SerialInterface_SerialInterface serialInterface = null;
        for(SerialInterface_SerialInterface serialInterface_serialInterface : serialInterfaceList){
            if(serialInterface_serialInterface.getAtOdataId().contains(managerObjectId) && serialInterface_serialInterface.getAtOdataId().contains(serialInterfaceObjectId))
                serialInterface = serialInterface_serialInterface;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, serialInterface);
        }
        return new AsyncResult<SerialInterface_SerialInterface>(serialInterface);
    }

    @Async
    public Future<SerialInterfaceCollection> getSerialInterfaceCollection(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<SerialInterfaceCollection> serialInterfaceCollectionList = serialInterfaceCollectionRepository.findAll();
        SerialInterfaceCollection serialIntCollection = null;
        for(SerialInterfaceCollection serialInterfaceCollection : serialInterfaceCollectionList){
            if(serialInterfaceCollection.getAtOdataId().contains(managerObjectId))
                serialIntCollection = serialInterfaceCollection;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, serialIntCollection);
        }
        return new AsyncResult<SerialInterfaceCollection>(serialIntCollection);
    }

    @Async
    public Future<HostInterface_HostInterface> getHostInterface(OffsetDateTime startTime, Integer taskId, String managerObjectId, String hostInterfaceObjectId){
        List<HostInterface_HostInterface> hostInterfaceList = hostInterfaceRepository.findAll();
        HostInterface_HostInterface hostInterface = null;
        for(HostInterface_HostInterface hInt : hostInterfaceList){
            if(hInt.getAtOdataId().contains(managerObjectId) && hInt.getAtOdataId().contains(hostInterfaceObjectId))
                hostInterface = hInt;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, hostInterface);
        }
        return new AsyncResult<HostInterface_HostInterface>(hostInterface);
    }

    @Async
    public Future<HostInterfaceCollection> getHostInterfaceCollection(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<HostInterfaceCollection> hostInterfaceCollectionList = hostInterfaceCollectionRepository.findAll();
        HostInterfaceCollection hostIntCollection = null;
        for(HostInterfaceCollection hostInterfaceCollection : hostInterfaceCollectionList){
            if(hostInterfaceCollection.getAtOdataId().contains(managerObjectId))
                hostIntCollection = hostInterfaceCollection;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, hostIntCollection);
        }
        return new AsyncResult<HostInterfaceCollection>(hostIntCollection);
    }

    @Async
    public Future<Port_Port> getPort(OffsetDateTime startTime, Integer taskId, String managerObjectId, String portObjectId){
        List<Port_Port> portList = portRepository.findAll();
        Port_Port p = null;
        for(Port_Port port : portList){
            if(port.getAtOdataId().contains(managerObjectId) && port.getAtOdataId().contains(portObjectId))
                p = port;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, p);
        }
        return new AsyncResult<Port_Port>(p);
    }

    @Async
    public Future<PortCollection> getPortCollection(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<PortCollection> portCollectionList = portCollectionRepository.findAll();
        PortCollection pCollection = null;
        for(PortCollection portCollection : portCollectionList){
            if(portCollection.getAtOdataId().contains(managerObjectId))
                pCollection = portCollection;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, pCollection);
        }
        return new AsyncResult<PortCollection>(pCollection);
    }

    @Async
    public Future<EthernetInterface_EthernetInterface> getEthernetInterface(OffsetDateTime startTime, Integer taskId, String managerObjectId, String ethernetInterfaceId){
        List<EthernetInterface_EthernetInterface> ethernetInterfaceList = ethernetInterfaceRepository.findAll();
        EthernetInterface_EthernetInterface ethernetInterface = null;
        for(EthernetInterface_EthernetInterface ethernetInterface2 : ethernetInterfaceList){
            if(ethernetInterface2.getAtOdataId().contains(managerObjectId) && ethernetInterface2.getAtOdataId().contains(ethernetInterfaceId))
                ethernetInterface = ethernetInterface2;
        }

        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, ethernetInterface);
        }
        return new AsyncResult<EthernetInterface_EthernetInterface>(ethernetInterface);
    }

    @Async
    public Future<EthernetInterfaceCollection> getEthernetInterfaceCollection(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<EthernetInterfaceCollection> ethernetInterfaceCollectionList = ethernetInterfaceCollectionRepository.findAll();
        EthernetInterfaceCollection ethernetIntCollection = null;
        for(EthernetInterfaceCollection ethernetInterfaceCollection : ethernetInterfaceCollectionList){
            if(ethernetInterfaceCollection.getAtOdataId().contains(managerObjectId) && ethernetInterfaceCollection.getAtOdataId().contains("/Ethernet"))
                ethernetIntCollection = ethernetInterfaceCollection;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, ethernetIntCollection);
        }
        return new AsyncResult<EthernetInterfaceCollection>(ethernetIntCollection);
    }

    @Async
    public Future<Certificate_Certificate> getCertificate(OffsetDateTime startTime, Integer taskId, String managerObjectId, String certificateControlId){
        List<Certificate_Certificate> certificateList = certificateRepository.findAll();
        Certificate_Certificate cert = null;
        for(Certificate_Certificate certificate : certificateList){
            if(certificate.getAtOdataId().contains(managerObjectId) && certificate.getAtOdataId().contains(certificateControlId))
                cert = certificate;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, cert);
        }
        return new AsyncResult<Certificate_Certificate>(cert);
    }

    @Async
    public Future<List<CertificateCollection>> getCertificateCollection(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<CertificateCollection> certificateCollectionList = certificateCollectionRepository.findAll();
        List<CertificateCollection> answer = new ArrayList<>();
        for(CertificateCollection certificateCollection : certificateCollectionList){
            if(certificateCollection.getAtOdataId().contains(managerObjectId) && certificateCollection.getAtOdataId().contains("/HTTPS"))
                answer.add(certificateCollection);
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, answer);
        }
        return new AsyncResult<List<CertificateCollection>>(answer);
    }

    @Async
    public Future<ManagerNetworkProtocol_ManagerNetworkProtocol> getManagerNetworkProtocol(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<ManagerNetworkProtocol_ManagerNetworkProtocol> managerNetworkProtocolList = managerNetworkProtocolRepository.findAll();
        ManagerNetworkProtocol_ManagerNetworkProtocol managerNetworkProtocol1 = null;
        for(ManagerNetworkProtocol_ManagerNetworkProtocol managerNetworkProtocol : managerNetworkProtocolList){
            if(managerNetworkProtocol.getAtOdataId().contains(managerObjectId))
                managerNetworkProtocol1 = managerNetworkProtocol;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, managerNetworkProtocol1);
        }
        return new AsyncResult<ManagerNetworkProtocol_ManagerNetworkProtocol>(managerNetworkProtocol1);
    }

    @Async
    public Future<Manager_Manager> getManager(OffsetDateTime startTime, Integer taskId, String managerObjectId){
        List<Manager_Manager> managerList = managerRepository.findAll();
        Manager_Manager manager_manager = null;
        for(Manager_Manager manager : managerList){
            if(manager.getAtOdataId().contains(managerObjectId))
                manager_manager = manager;
        }
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, manager_manager);
        }
        return new AsyncResult<Manager_Manager>(manager_manager);
    }

    @Async
    public Future<List<ManagerCollection>> getManagerCollection(OffsetDateTime startTime, Integer taskId){
        List<ManagerCollection> managerCollectionList = managerCollectionRepository.findAll();
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, managerCollectionList);
        }
        return new AsyncResult<List<ManagerCollection>>(managerCollectionList);
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

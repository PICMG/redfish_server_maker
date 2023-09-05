//******************************************************************************************************
// SystemController.java
//
// Controller for systems file.
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

package com.redfishserver.Redfish_Server.controllers;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.*;
import com.redfishserver.Redfish_Server.services.SystemsService;
import com.redfishserver.Redfish_Server.services.apiAuth.APIAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("")
public class SystemsController {

    @Value("${async.task.retry-time}")
    Integer taskRetryTime;

    @Value("${async.task.wait-time}")
    long taskWaitTime;
    @Autowired
    SystemsService systemsService;

    @Autowired
    APIAuthService apiAuthService;

    @GetMapping("/redfish/v1/Systems")
    public ResponseEntity<?> getComputerSystemCollection(){
        String uri = "/redfish/v1/Systems";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<ComputerSystemCollection> computerSystemCollectionList = null;

        try {
            Future<List<ComputerSystemCollection>> resp = systemsService.getComputerSystemCollection(startTime, newTaskId);
            computerSystemCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(computerSystemCollectionList);

    }

    @GetMapping("/redfish/v1/Systems/{systemObjectId}")
    public ResponseEntity<?> getComputerSystem(@PathVariable String systemObjectId, @RequestHeader String authorization) throws Exception {
        String uri = "/redfish/v1/Systems/"+systemObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        ComputerSystem_ComputerSystem computerSystem_computerSystem = null;
        try {
            Future<ComputerSystem_ComputerSystem> resp = systemsService.getComputerSystem(startTime, newTaskId, systemObjectId);
            computerSystem_computerSystem = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(computerSystem_computerSystem);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Bios")
    public ResponseEntity<?> getBios(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }

        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Bios";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<Bios_Bios> bios_biosList = null;
        try {
            Future<List<Bios_Bios>> resp = systemsService.getBios(startTime, newTaskId, systemCollectionId);
            bios_biosList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(bios_biosList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Bios/Settings")
    public ResponseEntity<?> getBiosSettings(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }

        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Bios/Settings";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Bios_Bios bios_bios = null;
        try {
            Future<Bios_Bios> resp = systemsService.getSettings(startTime, newTaskId, systemCollectionId);
            bios_bios = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(bios_bios);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/SecureBoot")
    public ResponseEntity<?> getSecureBoot(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/SecureBoot";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<SecureBoot_SecureBoot> secureBoot_secureBootList = null;
        try {
            Future<List<SecureBoot_SecureBoot>> resp = systemsService.getSecureBoot(startTime, newTaskId, systemCollectionId);
            secureBoot_secureBootList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(secureBoot_secureBootList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/SecureBoot/SecureBootDatabases")
    public ResponseEntity<?> getSecureBootDataBaseCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/SecureBoot/SecureBootDatabases";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<SecureBootDatabaseCollection> secureBootDatabaseCollectionList = null;
        try {
            Future<List<SecureBootDatabaseCollection>> resp = systemsService.getSecureBootDatabaseCollection(startTime, newTaskId, systemCollectionId);
            secureBootDatabaseCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(secureBootDatabaseCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/SecureBoot/SecureBootDatabases/{secureBootDatabaseObjectId}")
    public ResponseEntity<?> getSecureBootDatabase(@PathVariable String systemCollectionId, @PathVariable String secureBootDatabaseObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }

        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/SecureBoot/SecureBootDatabases/"+secureBootDatabaseObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        SecureBootDatabase_SecureBootDatabase secureBootDatabase = null;
        try {
            Future<SecureBootDatabase_SecureBootDatabase> resp = systemsService.getSecureBootDatabase(startTime, newTaskId, systemCollectionId, secureBootDatabaseObjectId);
            secureBootDatabase = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(secureBootDatabase);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Processors")
    public ResponseEntity<?> getProcessorCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Processors";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<ProcessorCollection> processorCollectionList = null;
        try {
            Future<List<ProcessorCollection>> resp = systemsService.getProcessorCollection(startTime, newTaskId, systemCollectionId);
            processorCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(processorCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Processors/{processorObjectId}")
    public ResponseEntity<?> getProcessor(@PathVariable String systemCollectionId, @PathVariable String processorObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Processors/"+processorObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Processor_Processor processor = null;
        try {
            Future<Processor_Processor> resp = systemsService.getProcessor(startTime, newTaskId, systemCollectionId, processorObjectId);
            processor = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(processor);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Processors/{processorObjectId}/EnvironmentMetrics")
    public ResponseEntity<?> getEnvironmentMetrics(@PathVariable String systemCollectionId, @PathVariable String processorObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Processors/"+processorObjectId+"/EnvironmentMetrics";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<EnvironmentMetrics_EnvironmentMetrics> environmentMetricsList = null;
        try {
            Future<List<EnvironmentMetrics_EnvironmentMetrics>> resp = systemsService.getEnvironmentMetrics(startTime, newTaskId, systemCollectionId, processorObjectId);
            environmentMetricsList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(environmentMetricsList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Processors/{processorObjectId}/ProcessorMetrics")
    public ResponseEntity<?> getProcessorMetrics(@PathVariable String systemCollectionId, @PathVariable String processorObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Processors/"+processorObjectId+"/ProcessorMetrics";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<ProcessorMetrics_ProcessorMetrics> processorMetricsList = null;
        try {
            Future<List<ProcessorMetrics_ProcessorMetrics>> resp = systemsService.getProcessorMetrics(startTime, newTaskId, systemCollectionId, processorObjectId);
            processorMetricsList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(processorMetricsList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Memory")
    public ResponseEntity<?> getMemoryCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Memory";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<MemoryCollection> memoryCollectionList = null;
        try {
            Future<List<MemoryCollection>> resp = systemsService.getMemoryCollection(startTime, newTaskId, systemCollectionId);
            memoryCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(memoryCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Memory/{memoryObjectId}")
    public ResponseEntity<?> getMemory(@PathVariable String systemCollectionId, @PathVariable String memoryObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Memory/"+memoryObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Memory_Memory memory = null;
        try {
            Future<Memory_Memory> resp = systemsService.getMemory(startTime, newTaskId, systemCollectionId, memoryObjectId);
            memory = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(memory);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/EthernetInterfaces")
    public ResponseEntity<?> getEthernetInterfaceCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/EthernetInterfaces";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<EthernetInterfaceCollection> ethernetInterfaceCollectionList = null;
        try {
            Future<List<EthernetInterfaceCollection>> resp = systemsService.getEthernetInterfaceCollection(startTime, newTaskId, systemCollectionId);
            ethernetInterfaceCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(ethernetInterfaceCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/EthernetInterfaces/{ethernetInterfaceObjectId}")
    public ResponseEntity<?> getEthernetInterface(@PathVariable String systemCollectionId, @PathVariable String ethernetInterfaceObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/EthernetInterfaces/"+ethernetInterfaceObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        EthernetInterface_EthernetInterface ethernetInterface = null;
        try {
            Future<EthernetInterface_EthernetInterface> resp = systemsService.getEthernetInterface(startTime, newTaskId, systemCollectionId, ethernetInterfaceObjectId);
            ethernetInterface = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(ethernetInterface);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/SimpleStorage")
    public ResponseEntity<?> getSimpleStorageCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/SimpleStorage";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<SimpleStorageCollection> simpleStorageCollectionList = null;
        try {
            Future<List<SimpleStorageCollection>> resp = systemsService.getSimpleStorageCollection(startTime, newTaskId, systemCollectionId);
            simpleStorageCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(simpleStorageCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/SimpleStorage/{simpleStorageObjectId}")
    public ResponseEntity<?> getSimpleStorage(@PathVariable String systemCollectionId, @PathVariable String simpleStorageObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/SimpleStorage/"+simpleStorageObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        SimpleStorage_SimpleStorage simpleStorage = null;
        try {
            Future<SimpleStorage_SimpleStorage> resp = systemsService.getSimpleStorage(startTime, newTaskId, systemCollectionId, simpleStorageObjectId);
            simpleStorage = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(simpleStorage);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/LogServices")
    public ResponseEntity<?> getLogServiceCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/LogServices";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<LogServiceCollection> logServiceCollectionList = null;
        try {
            Future<List<LogServiceCollection>> resp = systemsService.getLogServiceCollection(startTime, newTaskId, systemCollectionId);
            logServiceCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(logServiceCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/LogServices/{logServiceObjectId}")
    public ResponseEntity<?> getLogServices(@PathVariable String systemCollectionId, @PathVariable String logServiceObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/LogServices/"+logServiceObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        LogService_LogService logService = null;
        try {
            Future<LogService_LogService> resp = systemsService.getLogService(startTime, newTaskId, systemCollectionId, logServiceObjectId);
            logService = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(logService);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/GraphicsControllers")
    public ResponseEntity<?> getGraphicsControllerCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/GraphicsControllers";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<GraphicsControllerCollection> graphicsControllerCollectionList = null;
        try {
            Future<List<GraphicsControllerCollection>> resp = systemsService.getGraphicsControllerCollection(startTime, newTaskId, systemCollectionId);
            graphicsControllerCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(graphicsControllerCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/GraphicsControllers/{graphicsControllerObjectId}")
    public ResponseEntity<?> getGraphicsController(@PathVariable String systemCollectionId, @PathVariable String graphicsControllerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/GraphicsControllers/"+graphicsControllerObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        GraphicsController_GraphicsController graphicsController = null;
        try {
            Future<GraphicsController_GraphicsController> resp = systemsService.getGraphicsController(startTime, newTaskId, systemCollectionId, graphicsControllerObjectId);
            graphicsController = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(graphicsController);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/USBControllers")
    public ResponseEntity<?> getUSBControllerCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/USBControllers";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<USBControllerCollection> usbControllerCollectionList = null;
        try {
            Future<List<USBControllerCollection>> resp = systemsService.getUSBControllerCollection(startTime, newTaskId, systemCollectionId);
            usbControllerCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(usbControllerCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/USBControllers/{uSBControllerObjectId}")
    public ResponseEntity<?> getUSBController(@PathVariable String systemCollectionId, @PathVariable String uSBControllerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/USBControllers/"+uSBControllerObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        USBController_USBController usbController = null;
        try {
            Future<USBController_USBController> resp = systemsService.getUSBController(startTime, newTaskId, systemCollectionId, uSBControllerObjectId);
            usbController = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(usbController);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Certificates")
    public ResponseEntity<?> getCertificateCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Certificates";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<CertificateCollection> certificateCollectionList = null;
        try {
            Future<List<CertificateCollection>> resp = systemsService.getCertificateCollection(startTime, newTaskId, systemCollectionId);
            certificateCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificateCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/Certificates/{certificateObjectId}")
    public ResponseEntity<?> getCertificate(@PathVariable String systemCollectionId, @PathVariable String certificateObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/Certificates/"+certificateObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Certificate_Certificate certificate = null;
        try {
            Future<Certificate_Certificate> resp = systemsService.getCertificate(startTime, newTaskId, systemCollectionId, certificateObjectId);
            certificate = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificate);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/VirtualMedia")
    public ResponseEntity<?> getVirtualMediaCollection(@PathVariable String systemCollectionId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/VirtualMedia";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<VirtualMediaCollection> virtualMediaCollectionList = null;
        try {
            Future<List<VirtualMediaCollection>> resp = systemsService.getVirtualMediaCollection(startTime, newTaskId, systemCollectionId);
            virtualMediaCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(virtualMediaCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/VirtualMedia/{virtualMediaObjectId}")
    public ResponseEntity<?> getVirtualMedia(@PathVariable String systemCollectionId, @PathVariable String virtualMediaObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/VirtualMedia/"+virtualMediaObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        VirtualMedia_VirtualMedia virtualMedia = null;
        try {
            Future<VirtualMedia_VirtualMedia> resp = systemsService.getVirtualMedia(startTime, newTaskId, systemCollectionId, virtualMediaObjectId);
            virtualMedia = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(virtualMedia);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/VirtualMedia/{virtualMediaObjectId}/Certificates")
    public ResponseEntity<?> getVirtualMediaCertificateCollection(@PathVariable String systemCollectionId, @PathVariable String virtualMediaObjectId, @RequestHeader String authorization) throws Exception{
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/VirtualMedia/"+virtualMediaObjectId+"/Certificates";
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<CertificateCollection> certificateCollectionList = null;
        try {
            Future<List<CertificateCollection>> resp = systemsService.getVirtualMediaCertificateCollection(startTime, newTaskId, systemCollectionId, virtualMediaObjectId);
            certificateCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificateCollectionList);
    }

    @GetMapping("/redfish/v1/Systems/{systemCollectionId}/VirtualMedia/{virtualMediaObjectId}/Certificates/{certificateObjectId}")
    public ResponseEntity<?> getVirtualMediaCertificate(@PathVariable String systemCollectionId, @PathVariable String virtualMediaObjectId, @PathVariable String certificateObjectId, @RequestHeader String authorization) throws Exception{
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Systems/"+systemCollectionId+"/VirtualMedia/"+virtualMediaObjectId+"/Certificates/"+certificateObjectId;
        Integer newTaskId = systemsService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Certificate_Certificate certificate = null;
        try {
            Future<Certificate_Certificate> resp = systemsService.getVirtualMediaCertificate(startTime, newTaskId, systemCollectionId, virtualMediaObjectId, certificateObjectId);
            certificate = resp.get(taskWaitTime, TimeUnit.SECONDS);
        }catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", systemsService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            systemsService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(systemsService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificate);
    }

}

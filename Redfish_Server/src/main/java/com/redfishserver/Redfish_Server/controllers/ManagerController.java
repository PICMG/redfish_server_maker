//******************************************************************************************************
// ManagerController.java
//
// Controller for manager.
//
//Copyright (C) 2022.
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
import com.redfishserver.Redfish_Server.services.ManagerService;
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
public class ManagerController {

    @Value("${async.task.retry-time}")
    Integer taskRetryTime;

    @Value("${async.task.wait-time}")
    long taskWaitTime;
    @Autowired
    ManagerService managerService;

    @Autowired
    APIAuthService apiAuthService;

    @GetMapping("/redfish/v1/Managers")
    public ResponseEntity<?> getManagerCollection(){
        String uri = "/redfish/v1/Managers";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<ManagerCollection> managerCollectionList = null;
        try {
            Future<List<ManagerCollection>> resp = managerService.getManagerCollection(startTime, newTaskId);
            managerCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(managerCollectionList);

    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}")
    public ResponseEntity<?> getManager(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Manager_Manager manager = null;
        try {
            Future<Manager_Manager> resp = managerService.getManager(startTime, newTaskId, managerObjectId);
            manager = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(manager);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/NetworkProtocol")
    public ResponseEntity<?> getManagerNetworkProtocol(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/NetworkProtocol";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        ManagerNetworkProtocol_ManagerNetworkProtocol managerNetworkProtocol = null;
        try {
            Future<ManagerNetworkProtocol_ManagerNetworkProtocol> resp = managerService.getManagerNetworkProtocol(startTime, newTaskId, managerObjectId);
            managerNetworkProtocol = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(managerNetworkProtocol);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/NetworkProtocol/HTTPS/Certificates")
    public ResponseEntity<?> getCertificateCollection(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/NetworkProtocol/HTTPS/Certificates";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<CertificateCollection> certificateCollectionList = null;
        try {
            Future<List<CertificateCollection>> resp = managerService.getCertificateCollection(startTime, newTaskId, managerObjectId);
            certificateCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificateCollectionList);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/NetworkProtocol/HTTPS/Certificates/{certificateObjectId}")
    public ResponseEntity<?> getCertificate(@PathVariable String managerObjectId, @PathVariable String certificateObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/NetworkProtocol/HTTPS/Certificates/"+certificateObjectId;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Certificate_Certificate certificate = null;
        try {
            Future<Certificate_Certificate> resp = managerService.getCertificate(startTime, newTaskId, managerObjectId, certificateObjectId);
            certificate = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificate);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/EthernetInterfaces")
    public ResponseEntity<?> getEthernetInterfaceCollection(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/EthernetInterfaces";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        EthernetInterfaceCollection ethernetInterfaceCollection = null;
        try {
            Future<EthernetInterfaceCollection> resp = managerService.getEthernetInterfaceCollection(startTime, newTaskId, managerObjectId);
            ethernetInterfaceCollection = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(ethernetInterfaceCollection);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/EthernetInterfaces/{ethernetInterfaceObjectId}")
    public ResponseEntity<?> getEthernetInterface(@PathVariable String managerObjectId, @PathVariable String ethernetInterfaceObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/EthernetInterfaces/"+ethernetInterfaceObjectId;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        EthernetInterface_EthernetInterface ethernetInterface = null;
        try {
            Future<EthernetInterface_EthernetInterface> resp = managerService.getEthernetInterface(startTime, newTaskId, managerObjectId, ethernetInterfaceObjectId);
            ethernetInterface = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(ethernetInterface);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/DedicatedNetworkPorts")
    public ResponseEntity<?> getPortCollection(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/DedicatedNetworkPorts";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        PortCollection portCollection = null;
        try {
            Future<PortCollection> resp = managerService.getPortCollection(startTime, newTaskId, managerObjectId);
            portCollection = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(portCollection);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/DedicatedNetworkPorts/{portObjectId}")
    public ResponseEntity<?> getPort(@PathVariable String managerObjectId, @PathVariable String portObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/DedicatedNetworkPorts/"+portObjectId;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Port_Port port = null;
        try {
            Future<Port_Port> resp = managerService.getPort(startTime, newTaskId, managerObjectId, portObjectId);
            port = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(port);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/HostInterfaces")
    public ResponseEntity<?> getHostInterfaceCollection(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/HostInterfaces";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        HostInterfaceCollection hostInterfaceCollection = null;
        try {
            Future<HostInterfaceCollection> resp = managerService.getHostInterfaceCollection(startTime, newTaskId, managerObjectId);
            hostInterfaceCollection = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(hostInterfaceCollection);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/HostInterfaces/{hostInterfaceObjectId}")
    public ResponseEntity<?> getHostInterface(@PathVariable String managerObjectId, @PathVariable String hostInterfaceObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/HostInterfaces/"+hostInterfaceObjectId;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        HostInterface_HostInterface hostInterface = null;
        try {
            Future<HostInterface_HostInterface> resp = managerService.getHostInterface(startTime, newTaskId, managerObjectId, hostInterfaceObjectId);
            hostInterface = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(hostInterface);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/SerialInterfaces")
    public ResponseEntity<?> getSerialInterfaceCollection(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/SerialInterfaces";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        SerialInterfaceCollection serialInterfaceCollection = null;
        try {
            Future<SerialInterfaceCollection> resp = managerService.getSerialInterfaceCollection(startTime, newTaskId, managerObjectId);
            serialInterfaceCollection = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(serialInterfaceCollection);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/SerialInterfaces/{serialInterfaceObjectId}")
    public ResponseEntity<?> getSerialInterface(@PathVariable String managerObjectId, @PathVariable String serialInterfaceObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/SerialInterfaces/"+serialInterfaceObjectId;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        SerialInterface_SerialInterface serialInterface = null;
        try {
            Future<SerialInterface_SerialInterface> resp = managerService.getSerialInterface(startTime, newTaskId, managerObjectId, serialInterfaceObjectId);
            serialInterface = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(serialInterface);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/LogServices")
    public ResponseEntity<?> getLogServicesCollection(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/LogServices";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        LogServiceCollection logServiceCollection = null;
        try {
            Future<LogServiceCollection> resp = managerService.getLogServiceCollection(startTime, newTaskId, managerObjectId);
            logServiceCollection = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(logServiceCollection);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/LogServices/Log")
    public ResponseEntity<?> getLogService(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/LogServices/Log";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        LogService_LogService logService = null;
        try {
            Future<LogService_LogService> resp = managerService.getLogService(startTime, newTaskId, managerObjectId);
            logService = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(logService);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/SecurityPolicy")
    public ResponseEntity<?> getSecurityPolicy(@PathVariable String managerObjectId, @RequestHeader String authorization) throws Exception{
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/SecurityPolicy";
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<SecurityPolicy_SecurityPolicy> securityPolicyList = null;
        try {
            Future<List<SecurityPolicy_SecurityPolicy>> resp = managerService.getSecurityPolicy(startTime, newTaskId, managerObjectId);
            securityPolicyList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(securityPolicyList);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/SecurityPolicy/SPDM/{certType}")
    public ResponseEntity<?> getManagerCertificateCollection(@PathVariable String managerObjectId, @PathVariable String certType, @RequestHeader String authorization) throws Exception{
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/SecurityPolicy/SPDM/"+certType;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<CertificateCollection> certificateCollectionList = null;
        try {
            Future<List<CertificateCollection>> resp = managerService.getManagerCertificateCollection(startTime, newTaskId, managerObjectId, certType);
            certificateCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificateCollectionList);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/SecurityPolicy/SPDM/{certType}/{cert}")
    public ResponseEntity<?> getManagerCertificate(@PathVariable String managerObjectId, @PathVariable String certType, @PathVariable String cert, @RequestHeader String authorization) throws Exception{
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/SecurityPolicy/SPDM/"+certType+cert;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Certificate_Certificate certificate = null;
        try {
            Future<Certificate_Certificate> resp = managerService.getManagerCertificate(startTime, newTaskId, managerObjectId, certType, cert);
            certificate = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificate);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/SecurityPolicy/TLS/{obj1}/{certType}")
    public ResponseEntity<?> getManagerCertificateCollection(@PathVariable String managerObjectId, @PathVariable String obj1, @PathVariable String certType, @RequestHeader String authorization) throws Exception{
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/SecurityPolicy/TLS/"+obj1+certType;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<CertificateCollection> certificateCollectionList = null;
        try {
            Future<List<CertificateCollection>> resp = managerService.getManagerCertificateCollection2(startTime, newTaskId, managerObjectId, obj1, certType);
            certificateCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificateCollectionList);
    }

    @GetMapping("/redfish/v1/Managers/{managerObjectId}/SecurityPolicy/TLS/{obj1}/{certType}/{cert}")
    public ResponseEntity<?> getManagerCertificate(@PathVariable String managerObjectId, @PathVariable String obj1, @PathVariable String certType, @PathVariable String cert, @RequestHeader String authorization) throws Exception{
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "ServiceRoot", "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/Managers/"+managerObjectId+"/SecurityPolicy/TLS/"+obj1+certType+cert;
        Integer newTaskId = managerService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Certificate_Certificate certificate = null;
        try {
            Future<Certificate_Certificate> resp = managerService.getManagerCertificate2(startTime, newTaskId, managerObjectId, obj1, certType,cert);
            certificate = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", managerService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            managerService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(managerService.getTaskResource(newTaskId.toString()));
        } catch (Exception e)  {

        }
        return ResponseEntity.ok(certificate);
    }

}

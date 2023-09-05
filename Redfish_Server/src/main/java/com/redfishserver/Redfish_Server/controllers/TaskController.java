//******************************************************************************************************
// TasksController.java
//
// Controller for Task service.
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

import com.redfishserver.Redfish_Server.RFmodels.AllModels.Task_Task;
import com.redfishserver.Redfish_Server.RFmodels.custom.TaskMonitor;
import com.redfishserver.Redfish_Server.services.QueryParameterService;
import com.redfishserver.Redfish_Server.services.TaskService;
import com.redfishserver.Redfish_Server.services.apiAuth.APIAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityExistsException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/redfish/v1/TaskService")
public class TaskController {

    String controllerEntityName = "TaskService";
    @Autowired
    TaskService taskService;

    @Autowired
    APIAuthService apiAuthService;

    @Autowired
    QueryParameterService queryParameterService;

    @GetMapping("/Tasks")
    public ResponseEntity<?> getAll(@RequestHeader String authorization, @RequestParam Map<String, String> params) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, controllerEntityName, "GET");
        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }

        if(params.isEmpty())
            return ResponseEntity.ok().body(taskService.getAllTaskDetails());
        List<Task_Task> objectList = taskService.getAllTaskDetails();
        if(params.containsKey("$top"))
            return ResponseEntity.ok().body(queryParameterService.getTopQueryParameterResult(Collections.singletonList(objectList), Integer.parseInt(params.get("$top"))));
        if(params.containsKey("$skip"))
            return ResponseEntity.ok().body(queryParameterService.getSkippedQueryParameterResult(Collections.singletonList(objectList), Integer.parseInt(params.get("$skip"))));
        if(params.containsKey("only")) {
            if(objectList.size()==1)
                return ResponseEntity.ok().body(objectList.get(0));
            return ResponseEntity.ok().body(objectList);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Querying is not supported on the requested resource.");
    }

    @GetMapping("/Task/{Id}")
    public ResponseEntity<?> getById(@RequestHeader String authorization, @PathVariable String Id) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)) {
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, controllerEntityName, "GET")) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        Task_Task task = taskService.getTask(Id);
        if(task == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request succeeded, but no content is being returned in the body of the response.");
        }
        return ResponseEntity.ok().body(task);
    }


    @GetMapping("/Task/{Id}/monitor")
    public ResponseEntity<?> getMonitorById(@RequestHeader String authorization, @PathVariable String Id) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, controllerEntityName, "GET");
        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }
        TaskMonitor taskMonitor = null;
        try {
            taskMonitor = taskService.getTaskMonitor(Id);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
        if(taskMonitor.getTaskResponse()==null)
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskMonitor);
        return ResponseEntity.status(HttpStatus.OK).body(taskMonitor);
    }

    @RequestMapping(value = "/Task", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addTask(@RequestBody Task_Task task, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, controllerEntityName, "POST");
        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess\n" +
                    "authorization to complete this request.");
        }

        try {
            task = taskService.addTask(task);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", task.getAtOdataId());
            return ResponseEntity.status(HttpStatus.CREATED).headers(responseHeaders).body(task);
        } catch (EntityExistsException e) {
            ResponseEntity.status(HttpStatus.CONFLICT).body("Creation or update request could not be completed because it would cause a conflict in the");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request could not be processed because it contains invalid information");
    }

    @RequestMapping(value="/Task", method=RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteTask(@RequestBody Task_Task task, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, controllerEntityName, "DELETE");
        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess" +
                    "authorization to complete this request.");
        }

        if(taskService.deleteTask(task))
            return ResponseEntity.ok(task);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("The requested resource of type '%1' named %2 was not found.", "Session", task.getName()));
    }

    @RequestMapping(value="/Task", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateTask(@RequestBody Task_Task task, @RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body("There is no valid session established with the implementation.");
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, controllerEntityName, "PUT");
        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body("Service recognized the credentials in the request but those credentials do not possess" +
                    "authorization to complete this request.");
        }

        if(taskService.updateTask(task, null))
            return ResponseEntity.ok("The request completed successfully");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request could not be processed because it contains invalid information");
    }

}

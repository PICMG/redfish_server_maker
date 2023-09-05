//******************************************************************************************************
// RootController.java
//
// Controller for Root service.
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

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ActionInfo_ActionInfo;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.ActionInfo_Parameters;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.RedfishError;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.ServiceRoot_ServiceRoot;
import com.redfishserver.Redfish_Server.services.ActionInfoService;
import com.redfishserver.Redfish_Server.services.RedfishErrorResponseService;
import com.redfishserver.Redfish_Server.services.RootService;
import com.redfishserver.Redfish_Server.services.actions.ActionHandler;
import com.redfishserver.Redfish_Server.services.actions.ActionHandlerFactory;
import com.redfishserver.Redfish_Server.services.apiAuth.APIAuthService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/redfish/v1")
public class RootController {

    @Value("${async.task.retry-time}")
    Integer taskRetryTime;

    @Value("${async.task.wait-time}")
    long taskWaitTime;

    @Autowired
    ActionHandler actionHandler;

    @Autowired
    APIAuthService apiAuthService;


    @Autowired
    RootService rootService;

    @Autowired
    ActionInfoService actionInfoService;

    @Autowired
    RedfishErrorResponseService errorResponseService;


    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        String uri = "/redfish/v1/";
        Integer newTaskId = rootService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<ServiceRoot_ServiceRoot> rootList = new ArrayList<>();
        try {
            Future<List<ServiceRoot_ServiceRoot>> resp = rootService.getRootData(startTime, newTaskId);
            rootList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", rootService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            rootService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(rootService.getTaskResource(newTaskId.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Request could not be processed because it contains invalid information"));
        }
        if(rootList.size() ==0)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Request succeeded, but no content is being returned in the body of the response.");
        return ResponseEntity.ok().body(rootList.get(0));
    }


    @RequestMapping(value = { "/*/Actions/{resourceType}.{actionName}", "/*/*/Actions/{resourceType}.{actionName}","/*/*/*/Actions/{resourceType}.{actionName}", "/*/*/*/*/Actions/{resourceType}.{actionName}", "/*/*/*/*/*/Actions/{resourceType}.{actionName}"}, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postActions(@RequestHeader String authorization, @RequestBody String requestBody, @PathVariable String resourceType, @PathVariable String actionName, HttpServletRequest request) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        String uri = request.getRequestURI();
        Integer newTaskId = rootService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Boolean res = false;
        try {
            ActionHandlerFactory actionHandlerFactory = new ActionHandlerFactory();
            actionHandler = actionHandlerFactory.getActionHandler(resourceType, actionName);

            if (actionHandler == null){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorResponseService.getActionNotSupportedErrorResponse(actionName));
            }
            List<ActionInfo_Parameters> actionInfoParameters = null;
            ActionInfo_ActionInfo actionInfo = actionInfoService.getActionInfoById(resourceType + actionName + "ActionInfo");
            if (actionInfo != null) {
                actionInfoParameters = actionInfo.getParameters();
            }
            List<String> validateResponse = actionHandler.validateRequestBody(requestBody, actionInfoParameters);
            if (validateResponse == null){
                return ResponseEntity.badRequest().body("The action " + actionName + " does not require a request body.");
            }
            List<RedfishError> errors = new ArrayList<>();
            for (String resp:validateResponse){
                if (resp.contains("InvalidParameterValue")){
                    errors.add(errorResponseService.getActionParameterValueErrorResponse(resp.split("_")[1], actionName));
                }
                if (resp.contains("ActionParameterMissing")){
                    errors.add(errorResponseService.getActionParameterMissingErrorResponse(resp.split("_")[1], actionName));
                }
                if (resp.contains("IncorrectParameterType")){
                    errors.add(errorResponseService.getActionParameterValueTypeErrorResponse(resp.split("_")[2], resp.split("_")[1], actionName));
                }
                if (resp.contains("IncorrectParameterValue")){
                    errors.add(errorResponseService.getActionParameterValueNotInListErrorResponse(resp.split("_")[2], resp.split("_")[1], actionName));
                }

            }
            if(errors.size()>0){
                return ResponseEntity.badRequest().body(errors);
            }
            actionHandler.setRequestBody(requestBody);
            try {
                Future<Boolean> response = actionHandler.execute(startTime, newTaskId);
                res = response.get(taskWaitTime, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Location", rootService.getTaskServiceURI(newTaskId.toString()));
                responseHeaders.set("Retry-After", taskRetryTime + " seconds");
                rootService.createTaskForOperation(startTime, newTaskId, uri);
                return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(rootService.getTaskResource(newTaskId.toString()));
            }
            if (res.equals(true)) {
                return ResponseEntity.ok(errorResponseService.getSuccessResponse());
            }
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().body(errorResponseService.getActionParameterUnknownErrorResponse(actionName,e.getMessage().split("\"")[1]));
        }

        catch (JSONException e) {
            return ResponseEntity.badRequest().body(errorResponseService.getActionParameterDuplicateErrorResponse(actionName,e.getMessage().split("\"")[1]));
        }
        catch (ExecutionException e) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }
        catch (InterruptedException e) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }
    }

    @RequestMapping(value = { "/*/Actions/{resourceType}.{actionName}", "/*/*/Actions/{resourceType}.{actionName}","/*/*/*/Actions/{resourceType}.{actionName}", "/*/*/*/*/Actions/{resourceType}.{actionName}", "/*/*/*/*/*/Actions/{resourceType}.{actionName}"}, method = RequestMethod.POST)
    public ResponseEntity<?> postActions(@RequestHeader String authorization, @PathVariable String resourceType, @PathVariable String actionName,HttpServletRequest request) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        String uri = request.getRequestURI();
        Integer newTaskId = rootService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Boolean res = null;
        try {
            String requestBody = "";
            ActionHandlerFactory actionHandlerFactory = new ActionHandlerFactory();
            actionHandler = actionHandlerFactory.getActionHandler(resourceType, actionName);

            if (actionHandler == null){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorResponseService.getActionNotSupportedErrorResponse(actionName));
            }
            actionHandler.setRequestBody(requestBody);
            Future<Boolean> response = actionHandler.execute(startTime, newTaskId);
            res = response.get(taskWaitTime, TimeUnit.SECONDS);
            if (res.equals(true)) {
                return ResponseEntity.ok(errorResponseService.getSuccessResponse());
            }
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().body("The action " + actionName + " requires one or more parameters to be present in the request body.");
        }
        catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", rootService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", taskRetryTime + " seconds");
            rootService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(rootService.getTaskResource(newTaskId.toString()));
        }
        catch (ExecutionException e) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }
        catch (InterruptedException e) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(errorResponseService.getInternalErrorErrorResponse());
        }

    }

}

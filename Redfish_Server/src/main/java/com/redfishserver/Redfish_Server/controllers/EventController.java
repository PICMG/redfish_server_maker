//******************************************************************************************************
// EventController.java
//
// Controller for Event service.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.MetricReport_MetricReport;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.EventDestinationCollection;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.EventDestination_EventDestination;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.EventService_EventService;
import com.redfishserver.Redfish_Server.RFmodels.custom.Events;
import com.redfishserver.Redfish_Server.services.EventService;
import com.redfishserver.Redfish_Server.services.QueryParameterService;
import com.redfishserver.Redfish_Server.services.RedfishErrorResponseService;
import com.redfishserver.Redfish_Server.services.apiAuth.APIAuthService;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.naming.LimitExceededException;
import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Configuration
@EnableWebMvc
@RestController
@RequestMapping("/redfish/v1/EventService")
public class EventController {

    @Value("${async.task.retry-time}")
    Integer taskRetryTime;

    @Value("${async.task.wait-time}")
    long taskWaitTime;

    String controllerEntityName = "EventService";

    @Autowired
    EventService eventService;

    @Autowired
    APIAuthService apiAuthService;

    @Autowired
    QueryParameterService queryParameterService;

    @Autowired
    RedfishErrorResponseService redfishErrorResponseService;

    private MetricReport_MetricReport metricReport;

    @GetMapping("")
    public List<EventService_EventService> getEventServices() {
        return eventService.getEventServices();
    }

    @GetMapping("/Subscriptions")
    public ResponseEntity<?> getAllSubscriptions(@RequestHeader String authorization) {
        if(!apiAuthService.isUserAuthorizedForOperationType(authorization.substring(7), controllerEntityName, "GET")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Service recognized the credentials in the request but those credentials do not possess" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/EventService/Subscriptions";
        Integer newTaskId = eventService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        List<EventDestinationCollection> eventDestinationCollectionList = null;
        try {
            Future<List<EventDestinationCollection>> resp = eventService.getAllEventDetails(startTime, newTaskId);
            eventDestinationCollectionList = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", eventService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", eventService + " seconds");
            eventService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(eventService.getTaskResource(newTaskId.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Request could not be processed because it contains invalid information"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(eventDestinationCollectionList);
    }
    @GetMapping("/Subscriptions/{ID}")
    @ResponseBody
    public ResponseEntity<?> getSubscriptionById(@PathVariable String ID, @RequestHeader String authorization) {
        if(!apiAuthService.isUserAuthorizedForOperationType(authorization.substring(7), controllerEntityName, "GET")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Service recognized the credentials in the request but those credentials do not possess" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/EventService/Subscriptions/"+ID;
        Integer newTaskId = eventService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        EventDestination_EventDestination event = null;
        try {
            Future<EventDestination_EventDestination> resp = eventService.getSubscriptionById(startTime, newTaskId, ID);;
            event = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", eventService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", eventService + " seconds");
            eventService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(eventService.getTaskResource(newTaskId.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Request could not be processed because it contains invalid information"));
        }
        if(event == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Subscription with ID "+ID+" does not exist.");
        return ResponseEntity.ok(event);
    }

    @RequestMapping(value = "/Subscriptions", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSubscription(@RequestBody EventDestination_EventDestination events, @RequestHeader String authorization){
        if(!apiAuthService.isUserAuthorizedForOperationType(authorization.substring(7), controllerEntityName, "GET")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Service recognized the credentials in the request but those credentials do not possess" +
                    "authorization to complete this request.");
        }
        String uri = "/redfish/v1/EventService/Subscriptions";
        Integer newTaskId = eventService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        HttpHeaders headers = new HttpHeaders();
        EventDestination_EventDestination addEvent = null;
        try {
            Future<EventDestination_EventDestination> resp = eventService.addSubscription(true, startTime, newTaskId, events);
            addEvent = resp.get(taskWaitTime, TimeUnit.SECONDS);
            headers.add(HttpHeaders.LOCATION, addEvent.getAtOdataId());

        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", eventService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", eventService + " seconds");
            eventService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(eventService.getTaskResource(newTaskId.toString()));
        } catch (EntityExistsException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Subscription already exists or Syntax Error");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request could not be processed because it contains invalid information");
        }
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(addEvent);
    }

    @RequestMapping(value="/Subscriptions/{ID}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteSubscription(@PathVariable String ID, @RequestHeader String authorization) {
        if(!apiAuthService.isUserAuthorizedForOperationType(authorization.substring(7), controllerEntityName, "DELETE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
        }
        String uri = "/redfish/v1/EventService/Subscriptions/"+ID;
        Integer newTaskId = eventService.getTaskId();
        OffsetDateTime startTime = OffsetDateTime.now();
        Boolean deleteSubscriptionResponse = null;
        try {
            Future<Boolean> resp = eventService.deleteSubscriptionAsync(false, startTime, newTaskId, ID);
            deleteSubscriptionResponse = resp.get(taskWaitTime, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", eventService.getTaskServiceURI(newTaskId.toString()));
            responseHeaders.set("Retry-After", eventService + " seconds");
            eventService.createTaskForOperation(startTime, newTaskId, uri);
            return ResponseEntity.status(HttpStatus.ACCEPTED).headers(responseHeaders).body(eventService.getTaskResource(newTaskId.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request could not be processed because it contains invalid information");
        }
        if(deleteSubscriptionResponse!=null && deleteSubscriptionResponse)
            return ResponseEntity.ok(redfishErrorResponseService.getSubscriptionTerminationResponse(ID));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
    }

    @CrossOrigin
    @RequestMapping(value = "/SSE", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public SseEmitter subscribe() {
        try {
            return eventService.setEmitterList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @CrossOrigin
    @PostMapping(value = "/dispatch/Event")
    public void dispatchEventToClients(@RequestBody Events events) {
        eventService.disPatchEvents(events);
    }

    @CrossOrigin
    @PostMapping(value = "/SSE/close")
    public void closeSSEConnection() {
        eventService.completeSseEmitters();
    }

    @RequestMapping(value = "/PushEvents", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addEventAlert(@RequestBody Events events, @RequestHeader String authorization) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/redfish/v1/EventService/");
        try {
            if(eventService.getAllEventAlertDetails(events, authorization.substring(7)))
                return ResponseEntity.ok().headers(headers).body("");
        } catch (LimitExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Request payload, or a part in a multipart request, is larger than the maximum size the service supports.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to Push New Events");
    }

    @RequestMapping(value = "/PushMetricReport", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addEventAlert(@RequestBody String metricReportString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.registerModule(new JsonNullableModule());
            this.metricReport = mapper.readValue(metricReportString, MetricReport_MetricReport.class);
            eventService.disPatchMetricReport(this.metricReport);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}

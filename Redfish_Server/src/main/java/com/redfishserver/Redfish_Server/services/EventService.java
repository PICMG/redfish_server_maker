//******************************************************************************************************
// EventService.java
//
// Event service according to redfish specification.
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
import com.redfishserver.Redfish_Server.RFmodels.custom.EventMessage;
import com.redfishserver.Redfish_Server.RFmodels.custom.Events;
import com.redfishserver.Redfish_Server.repository.EventService.EventDestinationCollectionRepository;
import com.redfishserver.Redfish_Server.repository.EventService.EventDestinationRepository;
import com.redfishserver.Redfish_Server.repository.EventService.EventRepository;
import com.redfishserver.Redfish_Server.repository.EventService.EventServiceRepository;
import com.redfishserver.Redfish_Server.utils.Utils;
import org.json.JSONObject;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.LimitExceededException;
import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class EventService {

    @Value("${smtp-service.username}")
    String smtpUsername;

    @Value("${smtp-service.password}")
    String smtpPassword;

    @Value("${async.task.wait-time}")
    long taskWaitTime;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    EventServiceRepository eventServiceRepository;

    @Autowired
    EventDestinationRepository eventDestinationRepository;

    @Autowired
    EventDestinationCollectionRepository eventDestinationCollectionRepository;

    @Autowired
    APIServices apiServices;

    @Autowired
    TaskService taskService;

    public List<SseEmitter> emitterList = new CopyOnWriteArrayList<>();

    @Async
    public Future<List<EventDestinationCollection>> getAllEventDetails(OffsetDateTime startTime, Integer taskId) {
        List<EventDestinationCollection> eventDestinationCollectionList = eventDestinationCollectionRepository.findAll();
        if(startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, eventDestinationCollectionList);
        }
        return new AsyncResult<List<EventDestinationCollection>>(eventDestinationCollectionList);
    }

    @Async
    public Future<EventDestination_EventDestination> getSubscriptionById(OffsetDateTime startTime, Integer taskId, String Id) {
        EventDestination_EventDestination eventDestination = eventRepository.getById(Id);
        if(startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, eventDestination);
        }
        return new AsyncResult<EventDestination_EventDestination>(eventDestination);
    }

    @Async
    public Future<EventDestination_EventDestination> addSubscription(Boolean isaAync, OffsetDateTime startTime, Integer taskId, EventDestination_EventDestination event) {
        EventDestination_EventDestination newSubscription = eventRepository.getEventByName(event.getName());
        if(newSubscription != null)
            throw new EntityExistsException();
        List<EventDestination_EventDestination> allSubscriptions = eventRepository.findAll();
        long maxID = 0;
        for(EventDestination_EventDestination evnt : allSubscriptions) {
            maxID = Math.max(maxID, Long.valueOf(evnt.getId()));
        }
        maxID++;
        event.setId(String.valueOf(maxID));
        event.setAtOdataType("#EventDestination.v1_12_0.EventDestination");
        String oDataID = "/redfish/v1/EventService/Subscriptions/"+event.getId();
        event.setAtOdataId(oDataID);
        event.setContext(JsonNullable.of(event.getContext().toString()));
        EventDestinationCollection eventCollection  = eventDestinationCollectionRepository.findAll().get(0);
        List<Odata_IdRef> members = eventCollection.getMembers();
        members.add(new Odata_IdRef().atOdataId("/redfish/v1/EventService/Subscriptions/"+event.getId()));
        eventCollection.setMembers(members);
        eventCollection.setMembersAtOdataCount((long) members.size());
        eventDestinationCollectionRepository.save(eventCollection);
        event = eventRepository.save(event);
        if(isaAync && startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, event);
        }
        return new AsyncResult<EventDestination_EventDestination>(event);
    }

    public Boolean updateSubscription(EventDestination_EventDestination event) throws ChangeSetPersister.NotFoundException, UnsupportedOperationException, Exception {
        EventDestination_EventDestination eventSubscription = eventRepository.getById(event.getId());
        try{
            if(eventSubscription == null)
                return false;
            if(event.getName()!=null)
                eventSubscription.setName(event.getName());
            if(event.getId()!=null)
                eventSubscription.setId(event.getId());
            if(event.getDestination()!=null)
                eventSubscription.setDestination(event.getDestination());
            if(event.getContext()!=null)
                eventSubscription.setContext(event.getContext());
            if(event.getProtocol()!=null)
                eventSubscription.setProtocol(event.getProtocol());
            if(event.getAtOdataId()!=null)
                eventSubscription.setAtOdataId(event.getAtOdataId());
            eventRepository.save(eventSubscription);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error While Updating Subscription");
        }
        return true;
    }

    @Async
    public Future<Boolean> deleteSubscriptionAsync(Boolean isaAync, OffsetDateTime startTime, Integer taskId, String Id) {
        EventDestination_EventDestination existingSubscription = eventRepository.getById(Id);
        if(existingSubscription == null)
            return new AsyncResult<Boolean>(false);
        EventDestinationCollection eventDestinationCollection = eventDestinationCollectionRepository.findAll().get(0);
        List<Odata_IdRef> members = eventDestinationCollection.getMembers();
        for(int i=0;i<members.size();i++) {
            if(members.get(i).getAtOdataId().equals("/redfish/v1/EventService/Subscriptions/"+Id)) {
                members.remove(i);
                eventDestinationCollection.setMembersAtOdataCount(eventDestinationCollection.getMembersAtOdataCount()-1);
                break;
            }
        }
        eventDestinationCollection.setMembers(members);
        eventDestinationCollectionRepository.save(eventDestinationCollection);
        eventRepository.deleteSubscriptionById(Id);

        if(existingSubscription.getName().equalsIgnoreCase(Utils.SSE_CONNECTION_NAME)) {
            completeSseEmitters();
        }
        if(isaAync && startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, true);
        }
        return new AsyncResult<Boolean>(true);
    }

    public Boolean deleteSubscription(String Id) {
        EventDestination_EventDestination existingSubscription = eventRepository.getById(Id);
        if(existingSubscription == null)
            return false;
        EventDestinationCollection eventDestinationCollection = eventDestinationCollectionRepository.findAll().get(0);
        List<Odata_IdRef> members = eventDestinationCollection.getMembers();
        for(int i=0;i<members.size();i++) {
            if(members.get(i).getAtOdataId().equals("/redfish/v1/EventService/Subscriptions/"+Id)) {
                members.remove(i);
                eventDestinationCollection.setMembersAtOdataCount(eventDestinationCollection.getMembersAtOdataCount()-1);
                break;
            }
        }
        eventDestinationCollection.setMembers(members);
        eventDestinationCollectionRepository.save(eventDestinationCollection);
        eventRepository.deleteSubscriptionById(Id);

        if(existingSubscription.getName().equalsIgnoreCase(Utils.SSE_CONNECTION_NAME)) {
            completeSseEmitters();
        }
        return true;
    }

    public List<EventService_EventService> getEventServices() {
        return eventServiceRepository.findAll();
    }

    public EventDestination_EventDestination getByEventName(String eventName) {
        return eventRepository.getEventByName(eventName);
    }

    public SseEmitter setEmitterList() throws Exception {
        Future<EventDestination_EventDestination> resp = addSubscription(false, null, null, createSubscription());
        EventDestination_EventDestination sseEvent = resp.get();
        SseEmitter sseEmitter = new SseEmitter(Long.valueOf(60000*60));
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sseEmitter.onCompletion(() -> {
            System.out.println("Emitter is Closing...");
            emitterList.remove(sseEmitter);
            deleteSubscription(sseEvent.getId());
        });
        emitterList.add(sseEmitter);
        return sseEmitter;
    }

    public EventDestination_EventDestination createSubscription() {
        EventDestination_EventDestination event = new EventDestination_EventDestination();
        event.setName(Utils.SSE_CONNECTION_NAME);
        event.setDestination("/redfish/v1/EventService/SSE");
        event.setContext(JsonNullable.of("SSE Connection"));
        event.setProtocol(EventDestination_EventDestinationProtocol.SYSLOGTCP);
        return event;
    }

    public void completeSseEmitters() {
        for(SseEmitter sseEmitter: emitterList) {
            sseEmitter.complete();
        }
        EventDestination_EventDestination event = getByEventName("SSE");
        if(event != null)
            deleteSubscription(event.getId());
    }

    public void disPatchEvents(Events events) {
        for ( SseEmitter emitter : emitterList){
            try {
                emitter.send(SseEmitter.event().name("Event").data(events));
                emitter.send(SseEmitter.event().name("-").data("-"));
            } catch (IOException e) {
                emitterList.remove(emitter);
            }
        }
    }

    public void disPatchMetricReport(MetricReport_MetricReport metricReport) {
        for ( SseEmitter emitter : emitterList){
            try {
                emitter.send(SseEmitter.event().name("Metric Report").data(metricReport));
            } catch (IOException e) {
                emitterList.remove(emitter);
            }
        }
    }

    public boolean getAllEventAlertDetails(Events events,  String authorization) throws IOException, LimitExceededException {
        JSONObject jsonObject = new JSONObject(events);
        if(!Utils.isSizeILimit(jsonObject, Utils.BYTES_IN_MB)) {
            throw new LimitExceededException();
        }
        List<EventDestination_EventDestination> eventDestination = eventDestinationRepository.findAll();
        List<EventMessage> eventMessages = events.getEventMessageObjects();
        for(EventDestination_EventDestination obj : eventDestination) {
            for(EventMessage eventMessage : eventMessages) {
                List<EventEventType> rawEventTypes = obj.getEventTypes();
                if(obj.getProtocol().toString().equalsIgnoreCase("Redfish")) {
                    String destination = obj.getDestination();
                    implementRedfishProtocol(destination, eventMessage, authorization);
                }
                if(obj.getProtocol().toString().equalsIgnoreCase("SMTP")) {
                    String destination = obj.getDestination().split(":")[1];
                    implementSMTP(destination, eventMessage);
                }
                if(obj.getName().equalsIgnoreCase("SSE")) {
                    disPatchEvents(events);
                } else if(rawEventTypes == null) {
                    // Allowing all events when Event Type is Null
                } else {
                    // From DB: eventTypes
                    List<String> eventTypes = new ArrayList<>();
                    for(EventEventType event: rawEventTypes) {
                        eventTypes.add(event.getValue());
                    }
                    if(eventTypes.contains(eventMessage.getEventType())) {

                    }
                }
            }
        }
        return true;
    }

    public void implementSMTP(String destination, EventMessage eventMessage) {
        String username = this.smtpUsername;
        String password = this.smtpPassword;

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject("This is the Subject Line!");
            JSONObject jsonObject = new JSONObject(eventMessage);
            message.setText(jsonObject.toString());

            Transport.send(message);
            System.out.println("Sent message successfully for Protocol: SMTP");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void implementRedfishProtocol(String destination, EventMessage message, String authorization) throws IOException {
        String respString = apiServices.callGETAPI(destination, authorization);
//        String respString = apiServices.callPOSTAPI(destination, message.toString(), authorization);
        System.out.println(respString);
    }

    public void createTaskForOperation(OffsetDateTime startTime, Integer newTaskId, String uri) {
        taskService.createTaskForAsyncOperation(startTime, newTaskId, uri);
    }

    public Integer getTaskId() {
        return taskService.getMaxTaskCount();
    }

    public String getTaskServiceURI(String newTaskId) {
        return taskService.getTaskServiceURI() + newTaskId + "/monitor";
    }

    public Task_Task getTaskResource(String Id) {
        return taskService.getTask(Id);
    }
}
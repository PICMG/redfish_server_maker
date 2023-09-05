//******************************************************************************************************
// RedfishErrorResponseService.java
//
// Redfish error response service according to redfish specification.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.*;
import com.redfishserver.Redfish_Server.repository.MessageRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RedfishErrorResponseService {

    @Autowired
    MessageRegistryRepository messageRegistryRepository;

    List<MessageRegistry_MessageRegistry> messageRegistryList = null;

    /***
     *
     * PasswordChangeRequired
     */
    public RedfishError getPasswordChangeRequiredResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("PasswordChangeRequired");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"PasswordChangeRequired");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getNOOperationErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("NoOperation");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                redfishErrorError.setMessage(messageRegistryMessage.get("Message"));
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"NoOperation");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getInternalErrorErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("InternalError");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                redfishErrorError.setMessage(messageRegistryMessage.get("Message"));
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"InternalError");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }



    public RedfishError getActionNotSupportedErrorResponse(String action) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionNotSupported");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1",action);
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionNotSupported");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }
    public RedfishError getActionParameterMissingErrorResponse(String parameter, String action) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionParameterMissing");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1",action);
                message = message.replace("%2",parameter);
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionParameterMissing");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getActionParameterDuplicateErrorResponse(String action, String parameter) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionParameterDuplicate");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1",action);
                message = message.replace("%2",parameter);
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionParameterDuplicate");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getActionParameterUnknownErrorResponse(String action, String parameter) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionParameterUnknown");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1",action);
                message = message.replace("%2",parameter);
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionParameterUnknown");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getActionParameterValueTypeErrorResponse(String value, String parameter, String action) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionParameterValueTypeError");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1",value);
                message = message.replace("%2",parameter);
                message = message.replace("%3",action);
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionParameterValueTypeError");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getActionParameterValueNotInListErrorResponse(String value, String parameter, String action) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionParameterValueNotInList");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1",value);
                message = message.replace("%2",parameter);
                message = message.replace("%3",action);
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionParameterValueNotInList");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getActionParameterValueErrorResponse(String parameter, String action) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionParameterValueError");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1",parameter);
                message = message.replace("%2",action);
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionParameterValueError");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getNoValidSessionErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String,String> messageRegistryMessage = (LinkedHashMap<String, String>) messageLinkedHashMap.get("NoValidSession");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"NoValidSession");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getInsufficientPrivilegeErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("InsufficientPrivilege");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                String message = messageRegistryMessage.get("Message");
                redfishErrorError.setMessage(message);
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"InsufficientPrivilege");
                messageV112Message.setMessage(message);
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public Message_Message getTaskStartedResponse(Integer id) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        Message_Message messageV112Message = new Message_Message();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Task Event Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String>  messageRegistryMessage = messageLinkedHashMap.get("TaskStarted");
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"TaskStarted");
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1", id.toString());
                messageV112Message.setMessage(message);
            }
        }
        return messageV112Message;
    }

    public Message_Message getSuccessResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        Message_Message messageV112Message = new Message_Message();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String>  messageRegistryMessage = messageLinkedHashMap.get("Success");
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"Success");
                String message = messageRegistryMessage.get("Message");
                messageV112Message.setMessage(message);
            }
        }
        return messageV112Message;
    }


    public Message_Message getTaskCompletedOKResponse(String id) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        Message_Message messageV112Message = new Message_Message();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Task Event Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String>  messageRegistryMessage = messageLinkedHashMap.get("TaskCompletedOK");
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"TaskCompletedOK");
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1", id.toString());
                messageV112Message.setMessage(message);
            }
        }
        return messageV112Message;
    }

    public Message_Message getSubscriptionTerminationResponse(String id) {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        Message_Message messageV112Message = new Message_Message();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String>  messageRegistryMessage = messageLinkedHashMap.get("SubscriptionTerminated");
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"SubscriptionTerminated");
                String message = messageRegistryMessage.get("Message");
                message = message.replace("%1", id.toString());
                messageV112Message.setMessage(message);
            }
        }
        return messageV112Message;
    }

    public RedfishError getPropertyDuplicateErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("PropertyDuplicate");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                redfishErrorError.setMessage(messageRegistryMessage.get("Message"));
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"PropertyDuplicate");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }

    public RedfishError getPropertyValueTypeErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("PropertyValueTypeError");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                redfishErrorError.setMessage(messageRegistryMessage.get("Message"));
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"PropertyValueTypeError");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }


    public RedfishError getPropertyValueNotInListErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("PropertyValueNotInList");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                redfishErrorError.setMessage(messageRegistryMessage.get("Message"));
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"PropertyValueNotInList");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }


    public RedfishError getPropertyNotWritableErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("PropertyNotWritable");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                redfishErrorError.setMessage(messageRegistryMessage.get("Message"));
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"PropertyNotWritable");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }


    public RedfishError getActionParameterDuplicateErrorResponse() {
        if(messageRegistryList==null) {
            messageRegistryList = messageRegistryRepository.findAll();
        }
        RedfishError redfishError = new RedfishError();
        for(MessageRegistry_MessageRegistry messageRegistry: messageRegistryList) {
            if(messageRegistry.getName().equalsIgnoreCase("Base Message Registry")) {
                LinkedHashMap<String,LinkedHashMap<String, String>> messageLinkedHashMap = (LinkedHashMap<String, LinkedHashMap<String, String>>) messageRegistry.getMessages();
                LinkedHashMap<String, String> messageRegistryMessage = messageLinkedHashMap.get("ActionParameterDuplicate");
                RedfishErrorError redfishErrorError = new RedfishErrorError();
                redfishErrorError.setCode(messageRegistry.getId());
                redfishErrorError.setMessage(messageRegistryMessage.get("Message"));
                List<Message_Message> messageV112MessageList = new ArrayList<>();
                Message_Message messageV112Message = new Message_Message();
                messageV112Message.setSeverity(messageRegistryMessage.get("Severity"));
                messageV112Message.setResolution(messageRegistryMessage.get("Resolution"));
//                messageV112Message.setMessageSeverity(ResourceHealth.valueOf(messageRegistryMessage.get("Severity").toUpperCase()));
                messageV112Message.setMessageId(messageRegistry.getName()+"."+"ActionParameterDuplicate");
                messageV112Message.setMessage(messageRegistryMessage.get("Message"));
                messageV112MessageList.add(messageV112Message);
                redfishErrorError.setAtMessageExtendedInfo(messageV112MessageList);
                redfishError.setError(redfishErrorError);
            }
        }
        return redfishError;
    }
}

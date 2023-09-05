//******************************************************************************************************
// EventMessage.java
//
// Mongo Offset DateTime Writer file.
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
package com.redfishserver.Redfish_Server.RFmodels.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class EventMessage {
    @JsonProperty("MemberId")
    @Field("MemberId")
    String MemberId;

    @JsonProperty("EventType")
    @Field("EventType")
    String EventType;

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getEventType() {
        return EventType;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getSeverity() {
        return Severity;
    }

    public void setSeverity(String severity) {
        Severity = severity;
    }

    public String getMessageSeverity() {
        return MessageSeverity;
    }

    public void setMessageSeverity(String messageSeverity) {
        MessageSeverity = messageSeverity;
    }

    public String getEventTimestamp() {
        return EventTimestamp;
    }

    public void setEventTimestamp(String eventTimestamp) {
        EventTimestamp = eventTimestamp;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @JsonProperty("EventId")
    @Field("EventId")
    String EventId;

    @JsonProperty("Severity")
    @Field("Severity")
    String Severity;

    @JsonProperty("MessageSeverity")
    @Field("MessageSeverity")
    String MessageSeverity;

    @JsonProperty("EventTimestamp")
    @Field("EventTimestamp")
    String EventTimestamp;

    @JsonProperty("Message")
    @Field("Message")
    String Message;

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    @JsonProperty("MessageId")
    @Field("MessageId")
    String MessageId;

    public String getContext() {
        return Context;
    }

    public void setContext(String context) {
        Context = context;
    }

    @JsonProperty("Context")
    @Field("Context")
    String Context;

    public List<String> getMessageArgs() {
        return MessageArgs;
    }

    public void setMessageArgs(List<String> messageArgs) {
        MessageArgs = messageArgs;
    }

    @JsonProperty("MessageArgs")
    @Field("MessageArgs")
    List<String> MessageArgs;
}

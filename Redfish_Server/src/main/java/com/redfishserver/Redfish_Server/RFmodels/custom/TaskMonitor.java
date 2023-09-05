//******************************************************************************************************
// TaskMonitor.java
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
import com.redfishserver.Redfish_Server.RFmodels.AllModels.Message_Message;
import org.springframework.data.mongodb.core.mapping.Field;

public class TaskMonitor {

    @JsonProperty("uri")
    @Field("uri")
    String uri;

    @JsonProperty("TaskState")
    @Field("TaskState")
    String taskState;

    @JsonProperty("Message")
    @Field("Message")
    Message_Message message;

    @JsonProperty("response")
    @Field("response")
    Object taskResponse;


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Object getTaskResponse() {
        return taskResponse;
    }

    public void setTaskResponse(Object taskResponse) {
        this.taskResponse = taskResponse;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public Message_Message getMessage() {
        return message;
    }

    public void setMessage(Message_Message message) {
        this.message = message;
    }
}

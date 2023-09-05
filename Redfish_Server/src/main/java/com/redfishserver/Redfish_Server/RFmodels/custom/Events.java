//******************************************************************************************************
// Events.java
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

public class Events {

    @JsonProperty("Id")
    @Field("Id")
    String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContext() {
        return Context;
    }

    public void setContext(String context) {
        Context = context;
    }

    @JsonProperty("Name")
    @Field("Name")
    String Name;

    @JsonProperty("Context")
    @Field("Context")
    String Context;


    public List<EventMessage> getEventMessageObjects() {
        return EventMessageObjects;
    }

    public void setEventMessageObjects(List<EventMessage> eventMessageObjects) {
        EventMessageObjects = eventMessageObjects;
    }

    @JsonProperty("Events")
    @Field("Events")
    List<EventMessage> EventMessageObjects;
}

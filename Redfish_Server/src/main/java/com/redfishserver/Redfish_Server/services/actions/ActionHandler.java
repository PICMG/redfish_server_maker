//******************************************************************************************************
// ActionHandler.java
//
// Interface for ActionHandler.
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


package com.redfishserver.Redfish_Server.services.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.ActionInfo_Parameters;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public interface ActionHandler {

    Future<Boolean> execute(OffsetDateTime startTime, Integer taskId) throws Exception;
    List<String> validateRequestBody(String requestBody, List<ActionInfo_Parameters> actionInfoParameters);
    void setRequestBody(String requestBody) throws JsonProcessingException;
}

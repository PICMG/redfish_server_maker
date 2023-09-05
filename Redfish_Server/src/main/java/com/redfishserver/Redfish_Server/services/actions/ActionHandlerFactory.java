//******************************************************************************************************
// ActionHandlerFactory.java
//
// Action handlers handled using factory design..
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class ActionHandlerFactory {

//    @Autowired
//    @Qualifier("Bios_ChangePassword")
//    private Bios_ChangePasswordActionHandler bios_changePasswordActionHandler;
//
//    @Autowired
//    @Qualifier("Bios_Reset")
//    private  Bios_ResetActionHandler bios_resetActionHandler;

    public ActionHandler getActionHandler(String resourceType, String actionName) throws IOException {
        if ((resourceType == null) || (actionName == null)){
            return null;
        }
        if (resourceType.equals("Bios")){
            if (actionName.equals("ChangePassword")){
                return new Bios_ChangePasswordActionHandler();
            }
            if (actionName.equals("Reset")){
                return new Bios_ResetActionHandler();
            }
        }
        return null;
    }
}

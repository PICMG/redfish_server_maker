//******************************************************************************************************
// APiAuthService.java
//
// API authentication service file.
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


package com.redfishserver.Redfish_Server.services.apiAuth;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.*;
import com.redfishserver.Redfish_Server.repository.AccountService.AccountRepository;
import com.redfishserver.Redfish_Server.repository.RolesRepository;
import com.redfishserver.Redfish_Server.services.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class APIAuthService {

    @Autowired
    JWTService jwtService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    PrivilegeRegistryService privilegeRegistryService;

    List<PrivilegeRegistry_PrivilegeRegistry> privilegeRegistryList = null;

    public APIAuthService() {

    }

    public Boolean isUserAuthorizedForOperationType(String token, String entity, String operationType) {
        Boolean isAuthorized = false;
        List<PrivilegesPrivilegeType > assignedPrivileges = getAssignedPrivileges(token);
        privilegeRegistryList = privilegeRegistryService.getAllPrivilegesList();
        List<PrivilegeRegistry_Mapping> privilegeMappingList = privilegeRegistryList.get(0).getMappings();
        for(PrivilegeRegistry_Mapping privilegeMapping: privilegeMappingList) {
            if(privilegeMapping.getEntity().equalsIgnoreCase(entity)) {
                PrivilegeRegistry_OperationMap operationMap = privilegeMapping.getOperationMap();
                List<PrivilegeRegistry_OperationPrivilege> privilegeOperationsList = null;
                if (operationType.equalsIgnoreCase("GET")){
                    privilegeOperationsList = operationMap.getGET();
                }
                else if (operationType.equalsIgnoreCase("POST")){
                    privilegeOperationsList = operationMap.getPOST();
                }
                else if (operationType.equalsIgnoreCase("PUT")){
                    privilegeOperationsList = operationMap.getPUT();
                }
                else if (operationType.equalsIgnoreCase("PATCH")){
                    privilegeOperationsList = operationMap.getPATCH();
                }
                else if (operationType.equalsIgnoreCase("DELETE")){
                    privilegeOperationsList = operationMap.getDELETE();
                }

                // validate Privilege
                for(PrivilegeRegistry_OperationPrivilege privilegeOperation: privilegeOperationsList) {
                    List<String > requiredPrivilegesList = privilegeOperation.getPrivilege();
                    for(String rp: requiredPrivilegesList) {
                        for (PrivilegesPrivilegeType ap:assignedPrivileges){
                            if(ap.getValue().equalsIgnoreCase(rp)) {
                                isAuthorized = true;
                                break;
                            }
                        }
                        if(isAuthorized)break;
                    }
                    if(isAuthorized)break;
                }
                break;
            }
        }
        return isAuthorized;
    }


    public List<PrivilegesPrivilegeType> getAssignedPrivileges(String token) {
        List<PrivilegesPrivilegeType> assignedPrivileges = new ArrayList<>();
        try {
            String username = jwtService.extractJWTUsername(token);
            ManagerAccount_ManagerAccount account = accountRepository.getByUserName(username);
            String roleId = account.getRoleId();
            Role_Role roles = rolesRepository.getById(roleId);
            assignedPrivileges = roles.getAssignedPrivileges();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return assignedPrivileges;
    }

    public Boolean isUserAuthenticated(String jwt) throws Exception {
        return jwtService.isTokenValid(jwt);
    }
}

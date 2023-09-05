//******************************************************************************************************
// SessionController.java
//
// Controller for Session service.
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

import com.redfishserver.Redfish_Server.RFmodels.AllModels.SessionService_SessionService;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.Session_Session;
import com.redfishserver.Redfish_Server.RFmodels.AllModels.ManagerAccount_ManagerAccount;
import com.redfishserver.Redfish_Server.dto.SessionLoginDTO;
import com.redfishserver.Redfish_Server.services.RedfishErrorResponseService;
import com.redfishserver.Redfish_Server.services.apiAuth.APIAuthService;
import com.redfishserver.Redfish_Server.services.jwt.JWTService;
import com.redfishserver.Redfish_Server.services.SessionService;
import org.hibernate.UnknownProfileException;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/redfish/v1/SessionService")
public class SessionController {
    @Autowired
    SessionService sessionService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JWTService jwtService;

    @Autowired
    APIAuthService apiAuthService;

    @Autowired
    RedfishErrorResponseService errorResponseService;

    @RequestMapping(value = "/Sessions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> Session(@RequestBody SessionLoginDTO account) throws IOException, NoSuchAlgorithmException {
        ManagerAccount_ManagerAccount user = sessionService.validateUser(account.getUserName());
        try {
            if (user == null){
                return ResponseEntity.badRequest().body("Username or Password is Incorrect");
            }
            if (user.getPassword().get().compareTo(account.getPassword()) != 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or Password is Incorrect");
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(account.getUserName(), account.getPassword())
            );
            Session_Session session = new Session_Session();
            session.id(UUID.randomUUID().toString());
            session.name("User Session");
            session.description("Manager User Session");
            session.userName(user.getUserName());
            session.atOdataId("/redfish/v1/SessionService/Sessions/" + session.getId());
            session.setCreatedTime(JsonNullable.of(OffsetDateTime.now()));
            sessionService.addSession(session);
            sessionService.addMemberToSessionCollection(session.getAtOdataId());
            String jwt = jwtService.generateToken(user, session.getId());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, session.getAtOdataId());
            headers.set("X-Auth-Token", jwt);
            return ResponseEntity.ok().headers(headers).body(session);

        } catch (UnknownProfileException unknownProfileException) {
            ResponseEntity.badRequest().body(unknownProfileException.getMessage());
        }
        catch (Exception exception){
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        return ResponseEntity.badRequest().body("");
    }

    @GetMapping (value="/Sessions/{id}")
    public ResponseEntity<?> Session(@RequestHeader String authorization, @PathVariable("id") String id) throws Exception {
        String token = authorization.substring(7);
        if (!apiAuthService.isUserAuthenticated(token)){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        if(!apiAuthService.isUserAuthorizedForOperationType(token, "Session", "GET")) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInsufficientPrivilegeErrorResponse());
        }
        //SessionV140Session session = sessionService.getSessionById(id);
        return ResponseEntity.ok().body(sessionService.getSessionById(id));
    }
    @RequestMapping(value="/Sessions", method=RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSession(@RequestBody Session_Session session, @RequestHeader String authorization) throws Exception {

        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, "SessionService", "PATCH");

        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInsufficientPrivilegeErrorResponse());
        }
        if(session.getId() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request could not be processed because it contains invalid information");


        if(sessionService.updateSession(session))
            return ResponseEntity.ok(errorResponseService.getSuccessResponse());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request could not be processed because it contains invalid information");
    }

    @RequestMapping(value="", method=RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSessionService(@RequestBody SessionService_SessionService sessionService1, @RequestHeader String authorization) throws Exception {

        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, "SessionService", "PATCH");

        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInsufficientPrivilegeErrorResponse());
        }
        if(sessionService1 == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request could not be processed because it contains invalid information");

        return ResponseEntity.ok(sessionService.updateSessionService(sessionService1));
    }

    @RequestMapping(value = "/Sessions/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSession(@RequestHeader String authorization, @PathVariable("id") String id) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, "Session", "DELETE");

        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInsufficientPrivilegeErrorResponse());
        }
        if(id == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID cannot be null");
        sessionService.deleteMemberFromSessionCollection(id);
        Session_Session deletedSession = sessionService.deleteSession(id);

        if(deletedSession != null)
            return ResponseEntity.ok(deletedSession);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session not found");
    }
    @RequestMapping(value="/Sessions", method=RequestMethod.GET)
    public ResponseEntity<?> getSessionCollection(@RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, "SessionCollection", "GET");

        if(!isAPIAuthorized) {
            return ResponseEntity.badRequest().body(errorResponseService.getInsufficientPrivilegeErrorResponse());
        }
        return ResponseEntity.ok().body(sessionService.getSessionCollection());
    }

    @RequestMapping(value="", method=RequestMethod.GET)
    public ResponseEntity<?> getSessionService(@RequestHeader String authorization) throws Exception {
        String token = authorization.substring(7);
        Boolean isUserAuthenticated = apiAuthService.isUserAuthenticated(token);
        if (!isUserAuthenticated){
            return ResponseEntity.badRequest().body(errorResponseService.getNoValidSessionErrorResponse());
        }
        Boolean isAPIAuthorized = apiAuthService.isUserAuthorizedForOperationType(token, "SessionService", "GET");

        if(!isAPIAuthorized) {
            return ResponseEntity.internalServerError().body(errorResponseService.getInsufficientPrivilegeErrorResponse());
        }

        return ResponseEntity.ok().body(sessionService.getSessionService());

    }
}
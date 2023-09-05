//******************************************************************************************************
// SessionService.java
//
// Session service according to redfish specification.
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
import com.redfishserver.Redfish_Server.repository.AccountService.AccountRepository;
import com.redfishserver.Redfish_Server.repository.SessionService.SessionRepository;
import com.redfishserver.Redfish_Server.repository.SessionService.SessionCollectionRepository;
import com.redfishserver.Redfish_Server.repository.SessionService.SessionServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SessionServiceRepository sessionServiceRepository;

    @Autowired
    SessionCollectionRepository sessionCollectionRepository;

    @Autowired
    SessionRepository sessionRepository;

    public ManagerAccount_ManagerAccount validateUser(String UserName) {
        return accountRepository.getByUserName(UserName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ManagerAccount_ManagerAccount userAccount = accountRepository.getByUserName(username);
        return new User(userAccount.getUserName(), userAccount.getPassword().get(), new ArrayList<>());
    }

    public Session_Session getSessionById(String id) {
        return sessionRepository.findByID(id);
    }

    public SessionService_SessionService updateSessionService(SessionService_SessionService sessionService) {
        SessionService_SessionService service = sessionServiceRepository.findAll().get(0);
        if (sessionService.getServiceEnabled() != null) {
            service.setServiceEnabled(sessionService.getServiceEnabled());
        }
        if (sessionService.getName() != null) {
            service.setName(sessionService.getName());
        }
        if (sessionService.getDescription() != null) {
            service.setDescription(sessionService.getDescription());
        }
        if (sessionService.getSessionTimeout() != null) {
            service.setSessionTimeout(sessionService.getSessionTimeout());
        }
        sessionServiceRepository.save(service);
        return sessionServiceRepository.findAll().get(0);
    }
    public Boolean updateSession(Session_Session session) {
        Session_Session userSession = sessionRepository.findByID(session.getId());
        if(userSession == null)
            return false;
        if(session.getAtOdataId()!=null)
            userSession.setAtOdataId(session.getAtOdataId());
        if(session.getAtOdataType()!=null)
            userSession.setAtOdataType(session.getAtOdataType());
        if(session.getDescription()!=null)
            userSession.setDescription(session.getDescription());
        if(session.getName()!=null)
            userSession.setName(session.getName());
        if(session.getUserName()!=null)
            userSession.setUserName(session.getUserName());
        if(session.getCreatedTime() != null){
            userSession.setCreatedTime(session.getCreatedTime());
        }
        sessionRepository.save(userSession);

        return true;
    }

    public Session_Session deleteSession(String id) {
        Session_Session session = sessionRepository.deleteByID(id);
        return session;
    }

    public boolean addSession(Session_Session session) {
        sessionRepository.insert(session);
        return true;
    }

    public List<SessionService_SessionService> getSessionService(){
        return sessionServiceRepository.findAll();
    }

    public List<SessionCollection> getSessionCollection(){
        return sessionCollectionRepository.findAll();
    }

    public boolean addMemberToSessionCollection(String member) {
        SessionCollection sessionCollection = sessionCollectionRepository.findByName("Session Collection");
        Odata_IdRef idRef = new Odata_IdRef();
        idRef.atOdataId(member);
        sessionCollection.addMembersItem(idRef);
        sessionCollection.setMembersAtOdataCount(sessionCollection.getMembersAtOdataCount() + 1);
        sessionCollectionRepository.save(sessionCollection);
        return true;
    }

    public boolean deleteMemberFromSessionCollection(String id) {
        Session_Session session = sessionRepository.findByID(id);
        String member = session.getAtOdataId();
        SessionCollection sessionCollection = sessionCollectionRepository.findByName("Session Collection");
        List<Odata_IdRef> members = sessionCollection.getMembers();
        Odata_IdRef idRef = new Odata_IdRef();
        idRef.atOdataId(member);
        members.remove(idRef);
        sessionCollection.setMembers(members);
        sessionCollection.setMembersAtOdataCount(sessionCollection.getMembersAtOdataCount() - 1);
        sessionCollectionRepository.save(sessionCollection);
        return true;
    }

}

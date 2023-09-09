//******************************************************************************************************
// AccountService.java
//
// Service for account according to redfish.
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
import com.redfishserver.Redfish_Server.repository.AccountService.AccountServiceRepository;
import com.redfishserver.Redfish_Server.repository.AccountService.ExternalAccountProviderCollectionRepository;
import com.redfishserver.Redfish_Server.repository.AccountService.ExternalAccountProviderRepository;
import com.redfishserver.Redfish_Server.repository.RolesRepository;
import org.hibernate.UnknownProfileException;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class AccountService {

    @Value("${async.task.wait-time}")
    long taskWaitTime;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountServiceRepository accountServiceRepository;

    @Autowired
    ExternalAccountProviderRepository accountServiceExternalAccountProviderRepository;

    @Autowired
    APIServices apiServices;


    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    ExternalAccountProviderRepository externalAccountProviderRepository;

    @Autowired
    ExternalAccountProviderCollectionRepository externalAccountProviderCollectionRepository;

    @Autowired
    TaskService taskService;


    @Autowired
    PasswordEncryptorService passwordEncryptorService;


    public List<ManagerAccount_ManagerAccount> getAllAccountDetails() {
        List<ManagerAccount_ManagerAccount> accountList = accountRepository.findAll();
        return accountList;
    }

    public List<AccountService_ExternalAccountProvider> getAllAccountServiceV1110ExternalAccountProvider() {
        List<AccountService_ExternalAccountProvider> accountServiceV1110ExternalAccountProviderArrayList =
                accountServiceExternalAccountProviderRepository.findAll();
        return accountServiceV1110ExternalAccountProviderArrayList;
    }


    public Boolean validateUser(ManagerAccount_ManagerAccount account) throws UnknownProfileException {
        ManagerAccount_ManagerAccount userAccount = accountRepository.getByUserName(account.getUserName());
        if(account.getPassword() != null && userAccount.getPassword() != null &&
                account.getPassword().toString().equalsIgnoreCase(userAccount.getPassword().toString()))return true;
        return false;
    }

    public Boolean isContainOnlyOdata(ManagerAccount_ManagerAccount account) {
        if(account.getAtOdataId()!=null && account.getId()==null &&
            account.getUserName()==null && account.getPassword()==null &&
            account.getRoleId()==null && account.getAtOdataType()==null &&
            account.getName()==null)
            return true;
        return false;
    }

    @Async
    public Future<Boolean> updateUser(OffsetDateTime startTime, Integer taskId, ManagerAccount_ManagerAccount account) throws  Exception {
        if(isContainOnlyOdata(account)) {
            throw new UnsupportedOperationException();
        }
        ManagerAccount_ManagerAccount userAccount = accountRepository.getByUserName(account.getUserName());
        try {
            if(userAccount == null)
                throw new ChangeSetPersister.NotFoundException();
            if(account.getName()!=null)
                userAccount.setName(account.getName());
            if(account.getDescription()!=null)
                userAccount.setDescription(account.getDescription());
            if(account.getPassword().isPresent()) {
                String encPassword = passwordEncryptorService.encryptPassword(account.getPassword().toString());
                userAccount.setPassword(JsonNullable.of(encPassword));
            }
            if(account.getEnabled()!=null)
                userAccount.setEnabled(account.getEnabled());
            if(account.getRoleId()!=null)
                userAccount.setRoleId(account.getRoleId());
            if(account.getLocked()!=null)
                userAccount.setRoleId(account.getRoleId());

            System.out.println("Async Account Service Complete");
            accountRepository.save(userAccount);
            if(startTime.getSecond() > taskWaitTime+1) {
                taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, userAccount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error While Updating Account");
        }
        return new AsyncResult<Boolean>(true);
    }

    public Boolean deleteAccount(ManagerAccount_ManagerAccount account) throws ChangeSetPersister.NotFoundException {
        ManagerAccount_ManagerAccount userAccount = accountRepository.getByUserName(account.getUserName());
        if(userAccount == null)
            throw new ChangeSetPersister.NotFoundException();
        accountRepository.deleteAccountByUserName(account.getUserName());
        return true;
    }

    @Async
    public Future<Boolean> addAccount(OffsetDateTime startTime, Integer taskId, ManagerAccount_ManagerAccount account) throws Exception {
        ManagerAccount_ManagerAccount userAccount = accountRepository.getByUserName(account.getUserName());
// debug
System.out.println("Add Account 1");
System.out.println(account.getName());
        if(userAccount != null)
            throw new Exception("UserName " + account.getUserName() + " Already Exist");
        List<ManagerAccount_ManagerAccount> listAccount = accountRepository.findAll();
        if(listAccount.size() == 0) {
// debug
System.out.println("Add Account 2");
System.out.println(account.getName());
            account.setId("1");
        } else {
// debug
System.out.println("Add Account 3");
System.out.println(account.getName());
            long max = 0;
            for(ManagerAccount_ManagerAccount account1 : listAccount){
                max = Math.max(max, Long.valueOf(account1.getId()));
            }
            max++;
            account.setId(max+"");
            account.setAtOdataId("/redfish/v1/AccountService/Accounts/"+max);
            if(account.getPassword()!=null) {
// debug
System.out.println("Add Account 4");
System.out.println(account.getName());
                String encPassword = passwordEncryptorService.encryptPassword(account.getPassword().toString());
                account.setPassword(JsonNullable.of(encPassword));
            }
        }
// debug
System.out.println("Add Account 5");
System.out.println(account.getName());
        accountRepository.save(account);

        System.out.println("Async Account Service Complete");
        if(startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, account);
        }
        return new AsyncResult<Boolean>(true);
    }

    public List<AccountService_AccountService> getAccountServiceListData() {
        List<AccountService_AccountService> accountServiceV1110AccountServiceList = accountServiceRepository.findAll();
        return accountServiceV1110AccountServiceList;
    }

    @Async
    public Future<List<AccountService_AccountService>> getALllAccounts(OffsetDateTime startTime, Integer taskId) {
        List<AccountService_AccountService> accountService_accountServiceList = accountServiceRepository.findAll();
        if( ChronoUnit.SECONDS.between(startTime, OffsetDateTime.now())  > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, accountService_accountServiceList);
        }
        return new AsyncResult<List<AccountService_AccountService>>(accountService_accountServiceList);
    }

    public ManagerAccount_ManagerAccount getAccountById(String Id) throws ChangeSetPersister.NotFoundException {
        ManagerAccount_ManagerAccount userAccount = accountRepository.getById(Id);
        if(userAccount == null)
            throw new ChangeSetPersister.NotFoundException();
        return userAccount;
    }

    @Async
    public Future<RoleCollection> roleCollection(OffsetDateTime startTime, Integer taskId) throws Exception {
        RoleCollection roleCollection = new RoleCollection();
        List<Odata_IdRef> list = new ArrayList<>();
        List<Role_Role> rolesList = rolesRepository.findAll();
        for(Role_Role role : rolesList) {
            list.add(new Odata_IdRef().atOdataId(role.getAtOdataId()));
        }
        roleCollection.setMembersAtOdataCount((long) list.size());
        roleCollection.setMembers(list);
        if(startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, roleCollection);
        }
        return new AsyncResult<RoleCollection>(roleCollection);
    }

    @Async
    public Future<Role_Role> getRole(OffsetDateTime startTime, Integer taskId, String Id) throws ChangeSetPersister.NotFoundException {
        Role_Role roles = rolesRepository.getById(Id);
        if(roles == null)
            throw new ChangeSetPersister.NotFoundException();
        if(startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, roles);
        }
        return new AsyncResult<Role_Role>(roles);
    }

    @Async
    public Future<ExternalAccountProviderCollection> getExternalAccountProviders(OffsetDateTime startTime, Integer taskId) throws ChangeSetPersister.NotFoundException {
        ExternalAccountProviderCollection externalAccountProviders = new ExternalAccountProviderCollection();
        List<ExternalAccountProviderCollection> providerList = externalAccountProviderCollectionRepository.findAll();
        if(providerList==null)
            throw new ChangeSetPersister.NotFoundException();
        List<Odata_IdRef> list = new ArrayList<>();
        for(ExternalAccountProviderCollection obj : providerList) {
            list.add(new Odata_IdRef().atOdataId(obj.getAtOdataId()));
        }
        externalAccountProviders.setMembers(list);
        externalAccountProviders.setMembersAtOdataCount((long) list.size());
        if(startTime.getSecond() > taskWaitTime+1) {
            taskService.updateTaskState(taskId.toString(), Task_TaskState.COMPLETED, externalAccountProviders);
        }
        return new AsyncResult<ExternalAccountProviderCollection>(externalAccountProviders);
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

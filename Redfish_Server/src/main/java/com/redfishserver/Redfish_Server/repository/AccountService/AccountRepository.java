//******************************************************************************************************
// AccountRepository.java
//
// Repository for account.
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


package com.redfishserver.Redfish_Server.repository.AccountService;

import com.redfishserver.Redfish_Server.RFmodels.AllModels.ManagerAccount_ManagerAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AccountRepository extends MongoRepository<ManagerAccount_ManagerAccount, String> {

    @Query(value="{ 'id' : ?0 }")
    ManagerAccount_ManagerAccount getById(String id);

    ManagerAccount_ManagerAccount getByUserName(String userName);

    ManagerAccount_ManagerAccount deleteAccountByUserName(String userName);

}

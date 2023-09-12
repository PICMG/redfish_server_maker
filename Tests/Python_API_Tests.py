# Python_API_Tests.py
# This file is used for testing the Redfish Server APis
# Copyright (C) 2022, PICMG
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

import requests
import json
import collections


configJson = {}

#The below function loads the config file.
def loadConfigJsonFile():
    print('Loading Config File....')
    global configJson
    global credentials
    with open("config.json", 'r') as f:
        configJson = json.load(f)

# The type of parameters of below function are as below
        # response - Requests.response
        # expectedResponseCode - integer
        # expected_OdataId - str
        # expected_respHeader_array - list
def assertResponse(response, expectedResponseCode, expected_OdataId, expected_respHeader_array):
    assert response.status_code == expectedResponseCode
    if expectedResponseCode != 204:
        respBody = json.loads(response.content)
        if expected_OdataId != None:
            if isinstance(respBody, collections.abc.Sequence):
                assert len(
                    respBody) == 0 or expected_OdataId in respBody[0]['@odata.id']
            else:
                assert expected_OdataId in respBody['@odata.id']
    respHeader = json.loads(json.dumps(dict(response.headers)))
    for respH in expected_respHeader_array:
        assert respHeader[respH] != None and respHeader[respH] != ''

# The type of parameters of below function are as below
        # url - str
        # expectedResponseCode - integer
        # expected_OdataId - str
        # expected_respHeader_array - list
        # my_headers - dict
def do_get_request(url, expectedResponseCode, expected_OdataId, expected_respHeader_array, my_headers):
    r = requests.get(url, headers=my_headers)
    assertResponse(r, expectedResponseCode, expected_OdataId,
                   expected_respHeader_array)
    return r

# The type of parameters of below function are as below
        # url - str
        # expectedResponseCode - integer
        # reqBody - dict
        # expected_OdataId - str
        # expected_respHeader_array - list
        # my_headers - dict
def do_post_request(url, expectedResponseCode, reqBody, expected_OdataId, expected_respHeader_array, my_headers):
    if my_headers == None:
        my_headers = {"Content-Type": "application/json"}
    r = requests.post(url, json=reqBody, headers=my_headers)
    assertResponse(r, expectedResponseCode, expected_OdataId,
                   expected_respHeader_array)
    return r

# The type of parameters of below function are as below
        # url - str
        # expectedResponseCode - integer
        # reqBody - dict
        # expected_OdataId - str
        # expected_respHeader_array - list
        # my_headers - dict
def do_patch_request(url, expectedResponseCode, reqBody, expected_OdataId, expected_respHeader_array, my_headers):
    r = requests.patch(url, json=reqBody, headers=my_headers)
    assertResponse(r, expectedResponseCode, expected_OdataId,
                   expected_respHeader_array)
    return r

# The type of parameters of below function are as below
        # url - str
        # expectedResponseCode - integer
        # reqBody - dict
        # expected_respHeader_array - list
        # my_headers - dict
def do_delete_request(url, expectedResponseCode, reqBody, expected_respHeader_array, my_headers):
    r = requests.delete(url, json=reqBody, headers=my_headers)
    assertResponse(r, expectedResponseCode, None,
                   expected_respHeader_array)
    return r

#The below request is used to test session service APIs
def sessionService():
    print('Testing Session Service....')
    url = configJson['domain'] + \
        configJson['api']['session_service'] + '/Sessions'
    expected_OdataId = '/redfish/v1/SessionService/Sessions/'
    expected_respHeader_array = ['Location', 'X-Auth-Token']
    reqBody = configJson['credentials']['auth']
    r = do_post_request(url, 200, reqBody, expected_OdataId,
                        expected_respHeader_array, None)
    respHeader = json.loads(json.dumps(dict(r.headers)))
    return respHeader['X-Auth-Token']

#The below request is used to test root service API
def rootService():
    print('Testing Root Service....')
    url = configJson['domain'] + configJson['api']['root_service']
    expected_OdataId = configJson['api']['root_service']
    expected_respHeader_array = []
    do_get_request(url, 200, expected_OdataId, expected_respHeader_array, None)

#The below request is used to test account service get API
def accountService1(my_headers):
    url = configJson['domain'] + configJson['api']['account_service'] + '/'
    expected_OdataId = configJson['api']['account_service']
    expected_respHeader_array = []
    do_get_request(url, 200, expected_OdataId,
                   expected_respHeader_array, my_headers)

#The below request is used to test account service get API
def accountService2(my_headers):
    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Account'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    do_get_request(url, 200, expected_OdataId,
                   expected_respHeader_array, my_headers)

#The below request is used to test account service APIs
def accountService3(my_headers):
    mockAccount_Id = ''
    mockAccount_Name = 'MockAccount_Name'
    mockAccount_Description = 'MockAccount_Description'
    mockAccount_Username = 'MockAccount_UserName'
    mockAccount_RoleId = 'Administrator 10'
    reqBody = {
        "Name": mockAccount_Name,
        "Description": mockAccount_Description,
        "UserName": mockAccount_Username,
        "RoleId": mockAccount_RoleId
    }

    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Account'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_post_request(url, 201, reqBody, expected_OdataId,
                        expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    mockAccount_Id = respBody['Id']

    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Account/' + mockAccount_Id
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId,
                       expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    mockAccount_Name = mockAccount_Name + '_New'
    mockAccount_Description = mockAccount_Description + '_New'
    reqBody = {
        "Name": mockAccount_Name,
        "Description": mockAccount_Description,
        "UserName": mockAccount_Username,
        "RoleId": mockAccount_RoleId
    }

    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Account'
    expected_respHeader_array = []
    r = do_patch_request(url, 200, reqBody, None,
                         expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    url = url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Account/' + mockAccount_Id
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId,
                       expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Account'
    expected_respHeader_array = []
    r = do_delete_request(
        url, 200, reqBody, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId


def accountService(my_headers):
    print('Testing Account Service....')
    accountService1(my_headers)
    accountService2(my_headers)
    accountService3(my_headers)

#The below request is used to test task service get API
def taskService1(my_headers):
    url = configJson['domain'] + configJson['api']['task_service'] + '/Tasks'
    expected_OdataId = configJson['api']['task_service'] + '/Tasks'
    expected_respHeader_array = []
    do_get_request(url, 200, expected_OdataId,
                   expected_respHeader_array, my_headers)

#The below request is used to test task service APIs
def taskService2(my_headers):
    mockTaskName = "MockTaskName"
    mockTaskStartTime = "2012-03-07T14:44+06:00"
    mockTaskState = "Completed"
    mockTaskStatus = "OK"
    reqBody = {
        "Name": mockTaskName,
        "StartTime": mockTaskStartTime,
        "TaskState": mockTaskState,
        "TaskStatus": mockTaskStatus
    }

    url = configJson['domain'] + configJson['api']['task_service'] + '/Task'
    expected_respHeader_array = ['Location']
    r = do_post_request(url, 201, reqBody, None,
                        expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockTaskName and respBody['TaskState'] == mockTaskState and respBody[
        'TaskStatus'] == mockTaskStatus

    mockAccount_Id = respBody['Id']

    url = configJson['domain'] + \
        configJson['api']['task_service'] + '/Task/' + mockAccount_Id
    expected_OdataId = expected_OdataId = configJson['api']['task_service'] + \
        '/Tasks/' + mockAccount_Id
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId,
                       expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockTaskName and respBody['TaskState'] == mockTaskState and respBody[
        'TaskStatus'] == mockTaskStatus

    url = configJson['domain'] + configJson['api']['task_service'] + '/Task'
    expected_respHeader_array = []
    r = do_delete_request(
        url, 200, reqBody, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockTaskName and respBody['TaskState'] == mockTaskState and respBody[
        'TaskStatus'] == mockTaskStatus

    url = configJson['domain'] + \
        configJson['api']['task_service'] + '/Task/' + mockAccount_Id
    expected_respHeader_array = []
    do_get_request(url, 204, None, expected_respHeader_array, my_headers)

#The below request is used to test task service APIs
def taskService(my_headers):
    print('Testing Task Service....')
    taskService1(my_headers)
    taskService2(my_headers)

#The below request is used to test event service get API
def eventService1(my_headers):
    url = configJson['domain'] + configJson['api']['event_service']
    expected_OdataId = configJson['api']['event_service']
    expected_respHeader_array = []
    do_get_request(url, 200, expected_OdataId,
                   expected_respHeader_array, my_headers)

#The below request is used to test event service get API
def eventService2(my_headers):
    url = configJson['domain'] + \
        configJson['api']['event_service'] + '/Subscriptions'
    expected_OdataId = configJson['api']['event_service'] + '/Subscriptions'
    expected_respHeader_array = []
    do_get_request(url, 200, expected_OdataId,
                   expected_respHeader_array, my_headers)

#The below request is used to test event service APIs
def eventService3(my_headers):
    mockEventName = 'MockEventSubscription'
    mockEventDestination = 'MockEventDestination'
    mockEventAlert = "Alert"
    mockEventProtocol = 'Redfish'
    reqBody = {
        "Name": mockEventName,
        "Destination": mockEventDestination,
        "EventTypes": [
            mockEventAlert
        ],
        "Protocol": mockEventProtocol
    }

    url = configJson['domain'] + \
        configJson['api']['event_service'] + '/Subscriptions'
    expected_OdataId = configJson['api']['event_service'] + '/Subscriptions'
    expected_respHeader_array = ['Location']
    r = do_post_request(url, 201, reqBody, expected_OdataId,
                        expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockEventName and respBody['Destination'] == mockEventDestination and respBody[
        'Protocol'] == mockEventProtocol and len(respBody['EventTypes']) == 1 and respBody['EventTypes'][0] == mockEventAlert
    mockTask_Id = respBody['Id']

    url = configJson['domain'] + configJson['api']['event_service'] + \
        '/Subscriptions/' + mockTask_Id
    expected_respHeader_array = []
    r = do_delete_request(
        url, 200, reqBody, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert ("terminated" in respBody['Message'] and
            "No resolution is required" in respBody['Resolution'] and respBody['Severity'] == "OK")

#The below request is used to test event service APIs
def eventService(my_headers):
    print('Testing Event Service....')
    eventService1(my_headers)
    eventService2(my_headers)
    eventService3(my_headers)

#The below request is used to test actions
def biosChangePassword(my_headers):
    mockPasswordName = "AdminPassword"
    mockOldPassword = "123"
    mockNewPassword = "2424"
    reqBody = {
        "PasswordName": mockPasswordName,
        "OldPassword": mockOldPassword,
        "NewPassword": mockNewPassword
    }

    url = configJson['domain'] + configJson['api']['actions']['system']['base_url'] + \
        configJson['api']['actions']['system']['test_object_Id'] + \
        configJson['api']['actions']['system']['bio_change_password']

    expected_respHeader_array = []
    r = do_post_request(url, 200, reqBody, None,
                        expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert "Success" in respBody['MessageId'] and respBody['Resolution'] == "None" and respBody['Severity'] == "OK"

    reqBody['OldPassword'] = ''
    r = do_post_request(url, 400, reqBody, None,
                        expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert len(
        respBody) == 1 and 'OldPassword in the action ChangePassword is invalid' in respBody[0]['error']['message']

#The below request is used to test actions
def actions(my_headers):
    print('Testing Actions....')
    biosChangePassword(my_headers)


if __name__ == '__main__':
    loadConfigJsonFile()
    rootService()
    authHeader = sessionService()
    my_headers = {
        "Content-Type": "application/json",
        "Authorization": 'Bearer ' + authHeader
    }
    accountService(my_headers)
    taskService(my_headers)
    eventService(my_headers)
    actions(my_headers)
    print('All Tests are Passed!')

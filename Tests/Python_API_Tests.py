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
import pymongo
import collections
import base64
import sseclient
import threading
import queue
import urllib3

configJson = {}

class SseListener:
    events = queue.Queue(1024)
    sseheaders = {}
    sseurl = ""
    tid = None
    response = None

    def __init__(self, url, my_headers, filter):
        print("Initializing thread")
        self.sseurl = url + '?filter=' + filter
        self.sseheaders = my_headers.copy()
        self.sseheaders['Accept'] = 'text/event-stream'
        timeout = urllib3.Timeout(10.0e6, 10.0e6, 10.0e6)
        http = urllib3.PoolManager(cert_reqs='CERT_NONE', timeout=timeout)
        self.response = http.request('GET', self.sseurl, preload_content=False, headers=self.sseheaders)
        self.tid = threading.Thread(target=self.thread_task)
        self.tid.start()

    def __del__(self):
        self.response.close()

    def thread_task(self):
        print("Thread Started")
        client = sseclient.SSEClient(self.response)
        for event in client.events():
            print("Adding Event")
            self.events.put(event)

    def has_event(self):
        return not self.events.empty()

    def get_event(self):
        return self.events.get()


# The below function loads the config file.
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
    if expected_OdataId is not None:
        print(response.request.method, " ", response.url, " ", response.status_code, " ", json.loads(response.content))
    else:
        print(response.request.method, " ", response.url, " ", response.status_code)
    assert response.status_code == expectedResponseCode
    if expectedResponseCode != 204:
        if expected_OdataId is not None:
            respBody = json.loads(response.content)
            if isinstance(respBody, collections.abc.Sequence):
                assert len(
                    respBody) == 0 or expected_OdataId in respBody[0]['@odata.id']
            else:
                assert expected_OdataId in respBody['@odata.id']
    respHeader = json.loads(json.dumps(dict(response.headers)))
    for respH in expected_respHeader_array:
        assert respHeader[respH] is not None and respHeader[respH] != ''

# The type of parameters of below function are as below
        # url - str
        # expectedResponseCode - integer
        # expected_OdataId - str
        # expected_respHeader_array - list
        # my_headers - dict
def do_get_request(url, expectedResponseCode, expected_OdataId, expected_respHeader_array, my_headers):
    r = requests.get(url, headers=my_headers, verify=False)
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
    r = requests.post(url, json=reqBody, headers=my_headers, verify=False)
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
    r = requests.patch(url, json=reqBody, headers=my_headers, verify=False)
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
    r = requests.delete(url, json=reqBody, headers=my_headers, verify=False)
    assertResponse(r, expectedResponseCode, None,
                   expected_respHeader_array)
    return r

# The below request is used to test session service APIs
def sessionService():
    print('Testing Session Service....')

    print('Creating Session with proper credentials....')
    url = configJson['domain'] + \
        configJson['api']['session_service'] + '/Sessions'
    expected_OdataId = '/redfish/v1/SessionService/Sessions/'
    expected_respHeader_array = ['Location', 'X-Auth-Token']
    reqBody = configJson['credentials']['auth']
    r = do_post_request(url, 201, reqBody, expected_OdataId,
                        expected_respHeader_array, [])
    respHeader = json.loads(json.dumps(dict(r.headers)))

    # attempt to create another session for this user
    print('Attempting to create duplicate session....')
    r = do_post_request(url, 201, reqBody, expected_OdataId,
                        expected_respHeader_array, [])

    # attempt to create another session for this user
    print('Attempting signon with invalid credentials....')
    reqBody = configJson['credentials']['auth']
    reqBody["Password"]="awrongpassword"
    r = do_post_request(url, 400, reqBody, None,
                        [], [])

    return respHeader['X-Auth-Token']

#The below request is used to test root service API
def rootService():
    print('Testing Root Service....')

    # test for a response at /redfish
    url = configJson['domain'] + '/redfish'
    r = requests.get(url, verify=False)
    print("   Testing version at /redfish ", end='')
    assert r.status_code == 200
    assert r.text.replace(' ','') == '{"v1":"/redfish/v1/"}'
    assert r.url == url
    print("- PASSED")

    # test for a response at /redfish
    url = configJson['domain'] + '/redfish/'
    r = requests.get(url, verify=False)
    print("   Testing for redirect of /redfish/ ", end='')
    assert r.status_code == 200
    assert r.text.replace(' ','') == '{"v1":"/redfish/v1/"}'
    assert r.url == configJson['domain'] + '/redfish'
    print("- PASSED")

    # test for a response at /redfish/v1/$metadata
    url = configJson['domain'] + '/redfish/v1/$metadata'
    r = requests.get(url, verify=False)
    print("   Testing GET of /redfish/v1/$metadata ", end='')
    assert r.status_code == 200
    assert r.url == url
    print("- PASSED")

    # test for a response at /redfish/v1/odata
    url = configJson['domain'] + '/redfish/v1/odata'
    r = requests.get(url, verify=False)
    print("   Testing GET of /redfish/v1/odata ", end='')
    assert r.status_code == 200
    assert r.url == url
    print("- PASSED")

    # test for read of root service
    url = configJson['domain'] + "/redfish/v1/"
    expected_OdataId = configJson['api']['root_service']
    expected_respHeader_array = []
    print("   Testing GET of /redfish/v1/ ", end='')
    do_get_request(url, 200, expected_OdataId, expected_respHeader_array, None)
    print("- PASSED")

    # test for redirect from /redfish/v1
    url = configJson['domain'] + '/redfish/v1'
    r = requests.get(url, verify=False)
    print("   Testing for redirect from /redfish/v1 ", end='')
    assert r.status_code == 200
    assert r.url == configJson['domain'] + '/redfish/v1/'
    print("- PASSED")


#The below request is used to test account service get API
def accountService1(my_headers):
    url = configJson['domain'] + configJson['api']['account_service']
    expected_OdataId = configJson['api']['account_service']
    expected_respHeader_array = []
    do_get_request(url, 200, expected_OdataId,
                   expected_respHeader_array, my_headers)

#The below request is used to test account service accounts get API
def accountService2(my_headers):
    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Accounts'
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
    mockAccount_RoleId = 'Operator'
    reqBody = {
        "Name": mockAccount_Name,
        "Description": mockAccount_Description,
        "UserName": mockAccount_Username,
        "RoleId": mockAccount_RoleId,
        "Password": "Password"
    }

    # attempt to create a new account
    print("   Creating new account "+mockAccount_Username)
    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Accounts'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_post_request(url, 201, reqBody, expected_OdataId,
                        expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    # try to read back the new account
    mockAccount_Id = respBody['Id']

    url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Accounts/' + mockAccount_Id
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId,
                       expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    # try to patch the new account using account credentials
    print("   Patching new account using account credentials "+mockAccount_Username)
    auth_token = base64.b64encode((mockAccount_Username+":Password").encode("utf-8")).decode("ascii")
    authentication_header = {
        "Content-Type": "application/json",
        "Authorization": "Basic " + auth_token
    }
    reqBody = {
        "Name": mockAccount_Username+"_New",
        "Description": mockAccount_Description,
        "UserName": mockAccount_Username,
        "RoleId": mockAccount_RoleId,
        "Password": "testNewLonger"
    }
    url = url = configJson['domain'] + \
                configJson['api']['account_service'] + '/Accounts/' + mockAccount_Id
    expected_respHeader_array = []

    r = do_patch_request(url, 401, reqBody, None,
                         expected_respHeader_array, authentication_header)

    # Patching the account = just changing the password
    print("   Patching new account using account credentials (Just Password)"+mockAccount_Username)
    auth_token = base64.b64encode((mockAccount_Username+":Password").encode("utf-8")).decode("ascii")
    authentication_header = {
        "Content-Type": "application/json",
        "Authorization": "Basic " + auth_token
    }
    reqBody = {
        "Password": "testNewLonger"
    }
    url = url = configJson['domain'] + \
                configJson['api']['account_service'] + '/Accounts/' + mockAccount_Id
    expected_respHeader_array = []

    r = do_patch_request(url, 200, reqBody, None,
                         expected_respHeader_array, authentication_header)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    # Attempting to patch the account (including password) using admin credentials
    url = url = configJson['domain'] + \
                configJson['api']['account_service'] + '/Accounts/' + mockAccount_Id
    expected_respHeader_array = []

    r = do_patch_request(url, 200, reqBody, None,
                         expected_respHeader_array, my_headers)
    mockAccount_Username = mockAccount_Username+"_New"
    reqBody = {
        "Name": mockAccount_Username,
        "Description": mockAccount_Description,
        "UserName": mockAccount_Username,
        "RoleId": mockAccount_RoleId,
        "Password": "testNewLonger"
    }

    url = url = configJson['domain'] + \
                configJson['api']['account_service'] + '/Accounts/' + mockAccount_Id
    expected_respHeader_array = []

    r = do_patch_request(url, 200, reqBody, None,
                         expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    # verify the second patch took place account was created
    url = url = configJson['domain'] + \
        configJson['api']['account_service'] + '/Accounts/' + mockAccount_Id

    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId,
                       expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == mockAccount_Username and respBody['RoleId'] == mockAccount_RoleId

    # attempt to delete new (patched) account
    #url = configJson['domain'] + \
    #    configJson['api']['account_service'] + '/Accounts'
    print ("   Deleting patched account")
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

    url = configJson['domain'] + configJson['api']['task_service'] + '/Tasks'
    expected_respHeader_array = ['Location']

    r = do_post_request(url, 201, reqBody, None,
                        expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockTaskName and respBody['TaskState'] == mockTaskState and respBody[
        'TaskStatus'] == mockTaskStatus

    mockAccount_Id = respBody['Id']

    url = configJson['domain'] + \
        configJson['api']['task_service'] + '/Tasks/' + mockAccount_Id
    expected_OdataId = expected_OdataId = configJson['api']['task_service'] + \
        '/Tasks/' + mockAccount_Id
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId,
                       expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockTaskName and respBody['TaskState'] == mockTaskState and respBody[
        'TaskStatus'] == mockTaskStatus

    url = configJson['domain'] + configJson['api']['task_service'] + '/Tasks'
    expected_respHeader_array = []
    r = do_delete_request(
        url, 200, reqBody, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockTaskName and respBody['TaskState'] == mockTaskState and respBody[
        'TaskStatus'] == mockTaskStatus

    url = configJson['domain'] + \
        configJson['api']['task_service'] + '/Tasks/' + mockAccount_Id
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
    # this function uses the Bios Change Password action to test the server's ability to 
    # process action requests.  For any valid request, an actioninfo object must exist, but
    # these might not be present if the mockup used is directly from the DMTF repository.
    #
    # If you would like to add the actioninfo manually, you can do so with the following
    # shell command:
    # mongosh RedfishDB --eval 'db.ActionInfo.insertOne({"@odata.type": "#ActionInfo.v1_2_0.ActionInfo","Id": "BiosChangePasswordActionInfo","Name": "BiosChangePassword Action Info","Parameters": [{"Name": "PasswordName","Required": true,"DataType": "String","AllowableValues": ["AdminPassword","UserPassword"]},{"Name": "OldPassword","Required": true,"DataType": "String"},{"Name": "NewPassword","Required": true,"DataType": "String"}],"Oem": {},"@odata.id": "/redfish/v1/Systems/437XR1138R2/Bios/BiosChangePasswordActionInfo"})'
    
    # query mongodb to see if the actioninfo object exists for this test
    client = pymongo.MongoClient('mongodb://localhost:27017/')
    database = client['RedfishDB']
    query_result = database['ActionInfo'].find_one({'Id': 'BiosChangePasswordActionInfo'})
    if query_result is None:
        # here if there is no action info to use for the test
        print('   BiosChangePasswordActionInfo missing from database. Test skipped.')
        return

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


def testChangePasswordAction(my_headers):
    # create a new account that we can use for testing
    mockAccount_Name = 'MockAccount_Name'
    mockAccount_Description = 'MockAccount_Description'
    mockAccount_Username = 'MockAccount_UserName'
    mockAccount_RoleId = 'Operator'
    reqBody = {
        "Name": mockAccount_Name,
        "Description": mockAccount_Description,
        "UserName": "Operator",
        "RoleId": mockAccount_RoleId,
        "Password": "Operator"
    }

    # attempt to create a new account
    url = configJson['domain'] + configJson['api']['account_service'] + '/Accounts'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_post_request(url, 201, reqBody, expected_OdataId, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == "Operator" and respBody['RoleId'] == mockAccount_RoleId
    accountId = respBody['Id']

    # 1. attempt to change the password using basic HTTP authentication with the User's Account
    url = configJson['domain'] + configJson['api']['account_service'] + '/Accounts/'+accountId + '/Actions/ManagerAccount.ChangePassword'
    actionBody = {
        "NewPassword": "newPassword",
        "SessionAccountPassword": "Operator"
    }
    auth_token = base64.b64encode("Operator:Operator".encode("utf-8")).decode("ascii")
    authentication_header = {
        "Content-Type": "application/json",
        "Authorization": "Basic " + auth_token
    }
    r = do_post_request(url, 200, actionBody, None,
                        [], authentication_header)

    # 2. attempt to change the password using basic HTTP authentication with the Admin Account
    auth_token = base64.b64encode("Administrator:test".encode("utf-8")).decode("ascii")
    authentication_header = {
        "Content-Type": "application/json",
        "Authorization": "Basic " + auth_token
    }
    actionBody = {
        "NewPassword": "new2Password",
        "SessionAccountPassword": "test"
    }
    r = do_post_request(url, 200, actionBody, None,
                        [], authentication_header)

    # 3. attempt to change the password using Redfish Authentication with the Admin Account
    actionBody = {
        "NewPassword": "new3Password",
        "SessionAccountPassword": "test"
    }
    r = do_post_request(url, 200, actionBody, None,
                        [], my_headers)

    # Delete the test account
    url = configJson['domain'] + configJson['api']['account_service'] + '/Accounts/' + accountId
    expected_respHeader_array = []
    r = do_delete_request(url, 200, reqBody, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == "Operator" and respBody['RoleId'] == mockAccount_RoleId

def testEtag(my_headers):
    # etag headers must be supported for gets from a manager account

    # create a new account that we can use for testing
    mockAccount_Name = 'MockAccount_Name'
    mockAccount_Description = 'MockAccount_Description'
    mockAccount_Username = 'Operator'
    mockAccount_RoleId = 'Operator'
    reqBody = {
        "Name": mockAccount_Name,
        "Description": mockAccount_Description,
        "UserName": mockAccount_Username,
        "RoleId": mockAccount_RoleId,
        "Password": "Operator"
    }

    # attempt to create a new account
    url = configJson['domain'] + configJson['api']['account_service'] + '/Accounts'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_post_request(url, 201, reqBody, expected_OdataId, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == "Operator" and respBody['RoleId'] == mockAccount_RoleId
    accountid = respBody['@odata.id']

    # get the test account
    url = configJson['domain'] + accountid
    expected_OdataId = accountid
    r = do_get_request(url, 200, expected_OdataId, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    respHeaders = r.headers

    # does an etag header exist?
    etagHeaderValue = respHeaders['ETag']
    if etagHeaderValue == None:
        print("   Error - missing etag header in GET response")
    etagHeaderValue = etagHeaderValue.replace('"', "")

    # does an @odata.etag property exist?
    etagPropertyValue = respBody['@odata.etag']
    if etagPropertyValue == None:
        print("   Error - missing etag property in GET response")

    # do the etag header value and the property value match?
    if etagHeaderValue != etagPropertyValue:
        print("   Error - etag property and header do not match")

    ### GET TESTS ###
    # attempt a get If-Match with the correct etag
    headers = my_headers.copy()
    headers['If-Match'] = '"'+etagHeaderValue+'"'
    do_get_request(url, 200, expected_OdataId, expected_respHeader_array, headers)

    # attempt a get If-Match with the incorrect etag
    headers = my_headers.copy()
    headers['If-Match'] = '"bad_etag"'
    do_get_request(url, 412, None, expected_respHeader_array, headers)

    # attempt a get If-Match with the wildcard etag
    headers = my_headers.copy()
    headers['If-Match'] = '"*"'
    do_get_request(url, 200, expected_OdataId, expected_respHeader_array, headers)

    # attempt a get If-None-Match with the correct etag
    headers = my_headers.copy()
    headers['If-None-Match'] = '"'+etagHeaderValue+'"'
    do_get_request(url, 304, None, expected_respHeader_array, headers)

    # attempt a get If-Match with the incorrect etag
    headers = my_headers.copy()
    headers['If-None-Match'] = '"bad_etag"'
    do_get_request(url, 200, expected_OdataId, expected_respHeader_array, headers)

    # attempt a get If-Match with the wildcard etag
    headers = my_headers.copy()
    headers['If-None-Match'] = '"*"'
    do_get_request(url, 304, None, expected_respHeader_array, headers)

    ### PATCH TESTS ###
    # if Match with wrong etag
    headers = my_headers.copy()
    headers['If-Match'] = '"bad header"'
    body = { "Enabled": True }
    do_patch_request(url, 412, body, None, expected_respHeader_array, headers)

    # if Match with matching etag
    headers = my_headers.copy()
    headers['If-Match'] = '"'+etagHeaderValue+'"'
    body = {"Enabled": True}
    r = do_patch_request(url, 200, body, expected_OdataId, expected_respHeader_array, headers)
    respHeaders = r.headers

    # does an etag header exist?
    etagHeaderValue = respHeaders['ETag']
    if etagHeaderValue == None:
        print("   Error - missing etag header in GET response")

    # if Match with wild etag
    headers = my_headers.copy()
    headers['If-Match'] = '"*"'
    body = {"Enabled": True}
    r = do_patch_request(url, 200, body, expected_OdataId, expected_respHeader_array, headers)
    respHeaders = r.headers

    # does an etag header exist?
    etagHeaderValue = respHeaders['ETag']
    if etagHeaderValue == None:
        print("   Error - missing etag header in GET response")

    # if None Match with wrong etag
    headers = my_headers.copy()
    headers['If-None-Match'] = '"bad"'
    body = {"Enabled": True}
    r = do_patch_request(url, 200, body, expected_OdataId, expected_respHeader_array, headers)
    respHeaders = r.headers

    # does an etag header exist?
    etagHeaderValue = respHeaders['ETag']
    if etagHeaderValue == None:
        print("   Error - missing etag header in GET response")

    # if None Match with correct etag
    headers = my_headers.copy()
    headers['If-None-Match'] = etagHeaderValue
    body = {"Enabled": True}
    r = do_patch_request(url, 304, body, None, expected_respHeader_array, headers)

    # if None Match with wild etag
    headers = my_headers.copy()
    headers['If-None-Match'] = '"*"'
    body = {"Enabled": True}
    r = do_patch_request(url, 304, body, None, expected_respHeader_array, headers)

    # Delete the test account
    url = configJson['domain'] + accountid
    expected_respHeader_array = []
    r = do_delete_request(url, 200, reqBody, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    assert respBody['Name'] == mockAccount_Name and respBody['Description'] == mockAccount_Description and respBody[
        'UserName'] == "Operator" and respBody['RoleId'] == mockAccount_RoleId

def managerResetAction(my_headers):
    # this function uses the ManagerReset action to test the server's ability to
    # process action requests.  For any valid request, an actioninfo object must exist, but
    # these might not be present if the mockup used is directly from the DMTF repository.
    #

    my_headers = {"Content-Type": "application/json"}
    body = {
        "Parameters": {
            "AmountPieces": 10,
            "Flavors": ["Lemon"],
            "Shape": "Sphere"
        }
    }
    url = configJson['domain'] + \
          '/redfish/v1/IIoTJobService/Documents/Recipe1/Actions/IIoTJobDocument.SubmitJob'
    r = requests.post(url, json=body, headers=my_headers, verify=False)
    print(r.request.method, " ", r.url, " ", r.status_code, " ", json.dumps(json.loads(r.content), indent=2))
    assert False

#The below request is used to test actions
def actions(my_headers):
    print('Testing Actions....')
    biosChangePassword(my_headers)


def get_list_of_subscriptions(my_headers):
    url = configJson['domain'] + \
          '/redfish/v1/EventService/Subscriptions'
    r = requests.get(url, headers=my_headers, verify=False)
    if r.status_code!=200:
        assert False
    subscriptions = json.loads(r.content)['Members']
    result = []
    for sub in subscriptions:
        result.append(sub['@odata.id'])
    return result


def test_events(my_headers):

    # remove any existing subscriptions
    subscriptions = get_list_of_subscriptions(my_headers)
    for suburi in subscriptions:
        do_delete_request(configJson['domain'] + suburi, 200, {}, [], my_headers)

    ########################
    # test deletion of event destination
    ########################
    # crate a new SSE subscription
    url = configJson['domain'] + '/redfish/v1/EventService/SSE'
    my_filter = "MessageId eq 'Base.Created'"
    client = SseListener(url, my_headers, my_filter)

    # verify that it was created
    subscriptions = get_list_of_subscriptions(my_headers)
    if len(subscriptions) != 1:
        print("   Failure: SSE connection not created")
        assert False
    print("   Success: SSE connection created")

    # attempt to delete the subscription from the subscription list
    do_delete_request(configJson['domain']+subscriptions[0], 200, {}, [], my_headers)

    subscriptions = get_list_of_subscriptions(my_headers)
    if len(subscriptions) != 0:
        print("   Failure: SSE connection was not removed")
        assert False
    print("   Success: SSE connection removed")

    del client

    ########################
    # test event generation
    ########################
    # crate a new SSE subscription
    client = SseListener(url, my_headers, my_filter)

    # verify that it was created
    subscriptions = get_list_of_subscriptions(my_headers)

    if len(subscriptions) != 1:
        print("   Failure: SSE connection not created")
        assert False
    print("   Success: SSE connection created")

    # sending SubmitTestEvent action for Base.Created message
    print("sending SubmitTestEvent action for Base.Created message")
    action_body = {
            "MessageArgs": [],
            "MessageId": 'Base.Created',
            "MessageSeverity": "OK",
            "OriginOfCondition": "/redfish/v1/EventService",
            "Severity": "OK"
        }

    action_url = configJson['domain'] + '/redfish/v1/EventService/Actions/EventService.SubmitTestEvent'
    r = requests.post(action_url, json=action_body, headers=my_headers, verify=False)
    if r.text:
        print(json.loads(r.content))
    else:
        print(r.request.method, " ", r.url, " ", r.status_code)

    # get and print the event
    event = client.get_event()
    print(event.data.strip())

    action_body={}
    print("Attempting Service TestEventSubscription Action")
    action_url = configJson['domain'] + '/redfish/v1/EventService/Actions/EventService.TestEventSubscription'
    r = requests.post(action_url, json=action_body, headers=my_headers, verify=False)
    if r.text:
        print(json.loads(r.content))
    else:
        print(r.request.method, " ", r.url, " ", r.status_code)

    # get and print the event
    if client.has_event():
        event = client.get_event()
        print(event.data.strip())

    # remove the SSE connection and clean up the threads
    do_delete_request(configJson['domain']+subscriptions[0], 200, {}, [], my_headers)
    del client



def test_account_lockout(my_headers):
    # get the account service capabilities
    print("   Reading account service configuration")
    url = configJson['domain'] + '/redfish/v1/AccountService'
    expected_OdataId = configJson['api']['account_service']
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId, expected_respHeader_array, my_headers)
    accountService = json.loads(r.content)

    print("   Creating the test account")
    reqBody = {
        "Name": "mockAccount_Name",
        "Description": "mockAccount_Description",
        "UserName": "Operator",
        "RoleId": "Administrator",
        "Password": "Operator"
    }
    url = configJson['domain'] + configJson['api']['account_service'] + '/Accounts'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_post_request(url, 201, reqBody, expected_OdataId, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    accountUri = respBody['@odata.id']
    url = configJson['domain']+accountUri

    print("   Testing access with repeated bad passwords")
    bad_token = base64.b64encode("Operator:bogus_password".encode("utf-8")).decode("ascii")
    my_bad_auth = {"Content-Type": "application/json","Authorization": 'Basic ' + bad_token}
    for i in range(0, accountService["AccountLockoutThreshold"]):
        # attempt to access the account with an invalid password (HTTP basic)
        do_get_request(url, 401, None, [], my_bad_auth)

        # now read with the administrator account to make sure the lock has not occurred
        r = do_get_request(url, 200, accountUri, [], my_headers)
        body = json.loads(r.content)
        assert body["Locked"] is False

    # attempt one more access and make sure the account is locked
    do_get_request(url, 401, None, [], my_bad_auth)

    # now read with the administrator account to make sure the lock has not occurred
    r = do_get_request(url, 200, accountUri, [], my_headers)
    body = json.loads(r.content)
    assert body["Locked"] is True

    # Delete the test account
    url = configJson['domain'] + accountUri
    expected_respHeader_array = []
    do_delete_request(url, 200, reqBody, expected_respHeader_array, my_headers)


def test_password_change_required_with_patch(my_headers):
    # get the account service capabilities
    print("   Reading account service configuration")
    url = configJson['domain'] + '/redfish/v1/AccountService'
    expected_OdataId = configJson['api']['account_service']
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId, expected_respHeader_array, my_headers)
    accountService = json.loads(r.content)

    print("   Creating the test account")
    reqBody = {
        "Name": "mockAccount_Name",
        "Description": "mockAccount_Description",
        "UserName": "Operator",
        "RoleId": "Administrator",
        "Password": "Operator",
        "PasswordChangeRequired": True
    }
    url = configJson['domain'] + configJson['api']['account_service'] + '/Accounts'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_post_request(url, 201, reqBody, expected_OdataId, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    accountUri = respBody['@odata.id']
    url = configJson['domain']+accountUri

    print("   Attempt to access resources that are allowed with restricted access")
    account_token = base64.b64encode("Operator:Operator".encode("utf-8")).decode("ascii")
    account_headers = {"Content-Type": "application/json","Authorization": 'Basic ' + account_token}
    do_get_request(url, 200, accountUri, [], account_headers)
    do_get_request(configJson['domain']+'/redfish/v1', 200, '/redfish/v1', [], account_headers)

    print("   Attempt to access resources that are not allowed with restricted access but allowed with unrestricted")
    do_get_request(configJson['domain']+'/redfish/v1/AccountService', 403, None, [], account_headers)
    do_get_request(configJson['domain']+'/redfish/v1/SessionService', 403, None, [], account_headers)

    # attempt to reset the password using a patch to the resource
    reqBody = {
        "Password": "Operator2",
    }
    do_patch_request(url,200, reqBody, accountUri, [], account_headers );
    r = do_get_request(url, 200, accountUri, [],my_headers);
    body = json.loads(r.content)
    assert body["PasswordChangeRequired"] is False

    # Delete the test account
    url = configJson['domain'] + accountUri
    expected_respHeader_array = []
    do_delete_request(url, 200, reqBody, expected_respHeader_array, my_headers)


def test_password_change_required_with_action(my_headers):
    # get the account service capabilities
    print("   Reading account service configuration")
    url = configJson['domain'] + '/redfish/v1/AccountService'
    expected_OdataId = configJson['api']['account_service']
    expected_respHeader_array = []
    r = do_get_request(url, 200, expected_OdataId, expected_respHeader_array, my_headers)
    accountService = json.loads(r.content)

    print("   Creating the test account")
    reqBody = {
        "Name": "mockAccount_Name",
        "Description": "mockAccount_Description",
        "UserName": "Operator",
        "RoleId": "Administrator",
        "Password": "Operator",
        "PasswordChangeRequired": True
    }
    url = configJson['domain'] + configJson['api']['account_service'] + '/Accounts'
    expected_OdataId = configJson['api']['account_service'] + '/Accounts'
    expected_respHeader_array = []
    r = do_post_request(url, 201, reqBody, expected_OdataId, expected_respHeader_array, my_headers)
    respBody = json.loads(r.content)
    accountUri = respBody['@odata.id']
    accountUrl = configJson['domain']+accountUri

    print("   Attempting to create a new session using the new account")
    url = configJson['domain'] + '/redfish/v1/SessionService/Sessions'
    expected_OdataId = '/redfish/v1/SessionService/Sessions/'
    expected_respHeader_array = ['Location', 'X-Auth-Token']
    reqBody = {
        "UserName": "Operator",
        "Password": "Operator"
    }
    r = do_post_request(url, 201, reqBody, expected_OdataId,
                        expected_respHeader_array, [])
    respHeader = json.loads(json.dumps(dict(r.headers)))
    sessionUri = respHeader['Location']
    sessionUrl = configJson['domain']+sessionUri

    print("   Attempting to change the password using the ChangePassword action")
    accountSessionHeaders =  {
        "Content-Type": "application/json",
        "Authorization": 'Bearer ' + respHeader['X-Auth-Token']
    }
    actionurl = configJson['domain'] + accountUri + '/Actions/ManagerAccount.ChangePassword'
    actionBody = {
        "NewPassword": "newPassword",
        "SessionAccountPassword": "Operator"
    }
    do_post_request(actionurl, 200, actionBody, None,
                        [], accountSessionHeaders)
    r = do_get_request(accountUrl, 200, accountUri, [], accountSessionHeaders)
    body = json.loads(r.content)
    assert body["PasswordChangeRequired"] is False

def send_test_event_to_all_subscribers():
    # sending SubmitTestEvent action for Base.Created message
    print("sending SubmitTestEvent action for Base.Created message")
    action_body = {
            "MessageArgs": [],
            "MessageId": 'Base.Created',
            "MessageSeverity": "OK",
            "OriginOfCondition": "/redfish/v1/EventService",
            "Severity": "OK"
        }

    action_url = configJson['domain'] + '/redfish/v1/EventService/Actions/EventService.SubmitTestEvent'
    r = requests.post(action_url, json=action_body, headers=my_headers, verify=False)
    if r.text:
        print(json.loads(r.content))
    else:
        print(r.request.method, " ", r.url, " ", r.status_code)


if __name__ == '__main__':
    # disable warnings from self-signed security certificate
    from urllib3.exceptions import InsecureRequestWarning
    from urllib3 import disable_warnings, PoolManager

    disable_warnings(InsecureRequestWarning)

    loadConfigJsonFile()
    rootService()
    authHeader = sessionService()
    my_headers = {
        "Content-Type": "application/json",
        "Authorization": 'Bearer ' + authHeader
    }

    send_test_event_to_all_subscribers()

    '''
    # test deletion of active session
    url = configJson['domain'] + \
          '/redfish/v1/SessionService/Sessions'
    r = requests.get(url, json={}, headers=my_headers, verify=False)
    print(r.request.method, " ", r.url, " ", r.status_code, " ", json.dumps(json.loads(r.content), indent=2))

    # from the response, find the first session in the session service
    session = json.loads(r.content)["Members"][0]
    url_delete = configJson['domain'] + session["@odata.id"]

    # formulate the session deletion request
    auth_token = base64.b64encode("Administrator:test".encode("utf-8")).decode("ascii")
    authentication_header = {
        "Content-Type": "application/json",
        "Authorization": "Basic " + auth_token
    }
    r = requests.delete(url_delete,json = {}, headers = authentication_header, verify=False)
    print(r.request.method, " ", r.url, " ", r.status_code, " ", json.dumps(json.loads(r.content), indent=2))

    r = requests.get(url, json={}, headers=my_headers, verify=False)
    print(r.request.method, " ", r.url, " ", r.status_code, "")

    r = requests.get(url, json={}, headers=authentication_header, verify=False)
    print(r.request.method, " ", r.url, " ", r.status_code, " ", json.dumps(json.loads(r.content), indent=2))
    '''
    accountService(my_headers)

    print("testing change password action")
    testChangePasswordAction(my_headers)

    print("testing etag functionality")
    testEtag(my_headers)

    print("testing event service")
    test_events(my_headers)

    print("tesing password lockout")
    test_account_lockout(my_headers)

    print("tesing password change required part 1")
    test_password_change_required_with_patch(my_headers)

    print("tesing password change required part 2")
    test_password_change_required_with_action(my_headers)

    #managerResetAction(my_headers)

    # need another way to test the task service - assumptions made by this original code were wrong
    #taskService(my_headers)

    #eventService(my_headers)
    #actions(my_headers)
    print('All Tests are Passed!')

# InitializeRedfishServer.py
# This file is used for generating the Redfish Models, enums, EnumConverters,
# public and private keys, and creating a database in MongoDB.
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

import os
import re

import wget
from zipfile import ZipFile
import shutil
import json
import pymongo

credentials = {}
configJson = {}


# The below function gets the MongoClient URL and database name from config file
def get_mongo_creds():
    mongo_client_url = credentials['mongo_creds']['mongo_client_url']
    mongo_database = credentials['mongo_creds']['mongo_database']
    return mongo_client_url, mongo_database


# The below function drops the MongoDB database
def drop_mongo_collection(collection_name):
    mongo_client_url, mongo_database = get_mongo_creds()
    print('Dropping Collection : ', collection_name)
    client = pymongo.MongoClient(mongo_client_url)
    database = client[mongo_database]
    collection = database[collection_name]
    collection.drop()


# The below function is used to insert data into the MongoDB database
def execute_mongo_query(data, table):
    mongo_client_url, mongo_database = get_mongo_creds()
    client = pymongo.MongoClient(mongo_client_url)
    database = client[mongo_database]
    collection = database[table]
    if '@odata.id' in data:
        # create an easily searchable odata.id field
        data['_odata_id'] = data['@odata.id']
        data['_odata_type'] = data['@odata.type'].split('.')[0].replace('#', '')
    result = collection.insert_one(data)
    print('Query Executed for : ', table, result)


# The below function is a helper function used to get the latest version of Message registries and
# privilege registry data files.
def compare_version_number(version1, version2):
    version1_array = version1.split('.')
    version2_array = version2.split('.')
    n = min(len(version1_array), len(version2_array))
    for i in range(n):
        v1 = int(version1_array[i])
        v2 = int(version2_array[i])
        if v1 > v2:
            return 0
        if v1 < v2:
            return 1
    return 0


# the below function is used to insert Message Registry data into the database.
def initialize_message_registry_db(dir_path):
    all_files = []
    for path, currentDirectory, files in os.walk(dir_path):
        for file in files:
            if file.endswith('.json'):
                all_files.append(os.path.join(path, file))

    recent_files_map = {}
    for file in all_files:
        with open(file, 'r') as f:
            data = json.load(f)
        if '@odata.type' in data:
            if "PrivilegeRegistry" in data['Id']:
                curr_version_number = data['Id'].split('_')[1]
            else:
                curr_version_number = data['Id'][data['Id'].find('.') + 1:]
            if data['Name'] not in recent_files_map:
                recent_files_map[data['Name']] = dict(
                    versionNumber=curr_version_number, file=file)
            else:
                prev_version_number = recent_files_map[data['Name']]["versionNumber"]
                if compare_version_number(prev_version_number, curr_version_number) == 1:
                    recent_files_map[data['Name']] = dict(
                        versionNumber=curr_version_number, file=file)

    for k, v in recent_files_map.items():
        file_path = v['file']
        with open(file_path, 'r') as f:
            data = json.load(f)
        table_name = data['@odata.type'].split(".")[-1]
        execute_mongo_query(data, table_name)


# The below function inserts mockup data from json files into the database.
def initialize_db(mockup_dir_path):
    all_files = []
    for path, currentDirectory, files in os.walk(os.path.expanduser(mockup_dir_path)):
        for file in files:
            if file.endswith('.json'):
                all_files.append(os.path.join(path, file))
    for file in all_files:
        with open(file, 'r') as f:
            data = json.load(f)
        if '@odata.type' in data:
            if "MessageRegistry" in data['@odata.type']:
                continue
            table_name = data['@odata.type'].split(".")[-1]
            if '@odata.id' in data:
                table_name = 'RedfishObject'
            execute_mongo_query(data, table_name)


# The below function inserts odata file data into the database.
def create_odata_file_entry(mockup_dir_path):
    if not os.path.exists(os.path.expanduser(mockup_dir_path + '/odata/index.json')):
        return

    file = os.path.expanduser(mockup_dir_path + '/odata/index.json')
    with open(file, 'r') as f:
        data = f.read()
    execute_mongo_query({"data": data}, 'odata_file')


# The below function inserts metadata file data into the database.
def create_metadata_file_entry(mockup_dir_path):
    if not os.path.exists(os.path.expanduser(mockup_dir_path + '/$metadata/index.xml')):
        return

    file = os.path.expanduser(mockup_dir_path + '/$metadata/index.xml')
    with open(file, 'r') as f:
        data = f.read()
    execute_mongo_query({"data": data}, 'metadata_file')


# the below function is used to insert Privilege Registry data into the database.
def create_privilege_database(mockup_dir_path):
    temp_privilege_registry_dir_name = 'privilegeRegistry'
    os.makedirs(temp_privilege_registry_dir_name)
    os.chdir(temp_privilege_registry_dir_name)
    redfish_credentials = credentials['redfish_creds']
    file_name = redfish_credentials['privilege_file_name']
    zip_file_name = file_name + '.zip'
    zip_file_url = redfish_credentials['mockup_url'] + zip_file_name
    print('Downloading the Mockups from Redfish Server : ', zip_file_url)
    wget.download(zip_file_url)
    mockup_zip = ZipFile(zip_file_name)
    mockup_zip.extractall()
    mockup_zip.close()
    curr_dir = mockup_dir_path + "/" + temp_privilege_registry_dir_name
    initialize_message_registry_db(curr_dir)


# The below function downloads the redfish mockup data if mockup_file_path is not specified in config.
# If mockup_file_path is specified it reads json files from that path.
def download_and_initialize_redfish_mockups():
    destination_dir = os.getcwd()
    if configJson["mockup_file_path"] == "":
        temp_mockup_dir_name = "mockups"
        redfish_creds = credentials['redfish_creds']
        mockups_dir = destination_dir + '/' + temp_mockup_dir_name
        file_name = redfish_creds['mockup_file_name']
        zip_file_name = file_name + '.zip'
        zip_file_url = redfish_creds['mockup_url'] + zip_file_name
        print('Downloading the Mockups from Redfish Server : ', zip_file_url)
        mockup_dir_name = redfish_creds['mockup_dir_name']
        mockup_dir_path = mockups_dir + '/' + mockup_dir_name + '/'

        print(mockup_dir_path)

        if os.path.exists(mockups_dir):
            shutil.rmtree(mockups_dir)

        os.makedirs(mockups_dir)
        os.chdir(mockups_dir)
        wget.download(zip_file_url)
        mockup_zip = ZipFile(zip_file_name)
        mockup_zip.extractall()
        mockup_zip.close()

        initialize_db(mockup_dir_path)
        create_odata_file_entry(mockup_dir_path)
        create_metadata_file_entry(mockup_dir_path)

        # PrivilegeRegistry
        create_privilege_database(mockups_dir)
        print(mockups_dir)
        os.chdir(destination_dir)
        # remove mockup dir
        if os.path.exists(mockups_dir):
            shutil.rmtree(mockups_dir)
    else:
        initialize_db(configJson["mockup_file_path"])
        create_odata_file_entry(configJson["mockup_file_path"])
        create_metadata_file_entry(configJson["mockup_file_path"])
        temp_mockup_dir_name = "mockups"
        mockups_dir = destination_dir + '/' + temp_mockup_dir_name

        if os.path.exists(mockups_dir):
            shutil.rmtree(mockups_dir)

        os.makedirs(mockups_dir)
        os.chdir(mockups_dir)

        create_privilege_database(mockups_dir)
        os.chdir(destination_dir)

        # remove mockup dir
        if os.path.exists(mockups_dir):
            shutil.rmtree(mockups_dir)

    os.chdir(destination_dir)


# The below function loads the config file
def load_config_json_file():
    global configJson
    global credentials
    with open("config.json", 'r') as f:
        configJson = json.load(f)
    credentials = configJson['credentials']


# This function generates a cache of schema metadata within the mongodb database
# The cache lets the server validate post/patch information against the schema
# prior to making modifications to the data served.
def generate_schema_cache_and_security_table():
    # create a temporary folder
    start_directory = os.getcwd()
    if os.path.exists("./_sb_temp"):
        shutil.rmtree('./_sb_temp')
    os.makedirs("./_sb_temp")
    os.chdir('./_sb_temp')

    # download the schema bundle from the specified URL
    print('Downloading the Schema Bundle from Redfish Server : ', credentials["schema_bundle_url"])
    wget.download(credentials["schema_bundle_url"])

    # unzip the schema bundle
    zip_file_name = credentials["schema_bundle_url"].split('/')[-1]
    bundle_zip = ZipFile(zip_file_name)
    bundle_zip.extractall()
    bundle_zip.close()

    # change folders to the json-schema subfolder of the bundle
    # this folder holds all the released json schema files for the
    # current version of the schema bundle
    os.chdir('./json-schema')

    # copy json schema from local schema repository
    if not configJson['local_schema_path'] == "":
        local_path = os.path.expanduser(configJson['local_schema_path']) + '/json'
        for filename in os.listdir(local_path):
            if os.path.exists(filename):
                os.remove(filename)
            shutil.copy(local_path + "/" + filename, filename)

    # loop for each json file in the folder
    for filename in os.listdir():
        # load the file into a dictionary
        with open(filename) as jsonfile:
            schema_dict = json.load(jsonfile)
            objname = filename
            entry = {'source': objname, 'schema': json.dumps(schema_dict)}

            # add schema to cache
            print('Adding ' + filename + ' to schema cache')
            execute_mongo_query(entry, 'json_schema')

            obj_base_name = objname.split('.')[0]
            if "definitions" in schema_dict and obj_base_name in schema_dict['definitions']:
                create_security_table_entry(obj_base_name, schema_dict['definitions'][obj_base_name])

    # remove the temporary folder
    os.chdir(start_directory)
    shutil.rmtree('./_sb_temp')


def create_security_table_entry(name, json_obj):
    if 'uris' not in json_obj:
        return

    mongo_client_url, mongo_database = get_mongo_creds()
    mongo_client = pymongo.MongoClient(mongo_client_url)
    database = mongo_client[mongo_database]
    collection = database['privileges_table']

    # find the security permissions for the object
    privileges_registry = database['PrivilegeRegistry'].find_one({})
    mappings = privileges_registry['Mappings']
    for mapping in mappings:
        if mapping['Entity'] == name:
            # search for any uris associated with this object
            for uri in json_obj['uris']:
                # replace any wildcard fields with regular expression syntax
                # check to see if the uri exists in the security table
                regex_uri = re.sub('{[^}]+}', '[^\/]+', uri)

                result = {'uri': regex_uri, 'Entity': name, 'OperationMap': mapping['OperationMap']}

                # overwrite any previous operation map
                collection.insert_one(result)


# The below function is the entry point of this file
if __name__ == "__main__":
    # load the configuration switches from the configuration file
    load_config_json_file()

    # drop the redfish database if it exists
    os.system('mongosh RedfishDB --eval "printjson(db.dropDatabase())"')

    # download the mockup from the specified uri and
    # build the mongoDB database from the mockup.
    download_and_initialize_redfish_mockups()

    # initialize schema cache
    generate_schema_cache_and_security_table()

    # set the administrator account password
    os.system(
        'mongosh RedfishDB --eval "db.RedfishObject.updateOne({_odata_type:\'ManagerAccount\', ' +
        'UserName:\'Administrator\'},{\'\$set\':{Password:\'test\'}})"')


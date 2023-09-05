# InitializeRedfishServer.py
# This file is used for generating the Redfish Models, enums, EnumCoverters, 
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
import wget
from zipfile import ZipFile
import shutil
import json
import pymongo
import yaml

credentials = {}
configJson = {}

#The below function gets the MongoClient URL and database name from config file
def getMongoCreds():
    mongoClientUrl = credentials['mongo_creds']['mongo_client_url']
    mongoDatabase = credentials['mongo_creds']['mongo_database']
    return mongoClientUrl, mongoDatabase

#The below function drops the MongoDB database
def dropMongoCollection(collectionName):
    mongoClientUrl, mongoDatabase = getMongoCreds()
    print('Dropping Collection : ', collectionName)
    client = pymongo.MongoClient(mongoClientUrl)
    database = client[mongoDatabase]
    mycol = database[collectionName]
    mycol.drop()

#The below function is used to insert data into the MongoDB database
def executeMongoQuery(data, table):
    mongoClientUrl, mongoDatabase = getMongoCreds()
    client = pymongo.MongoClient(mongoClientUrl)
    database = client[mongoDatabase]
    collection = database[table]
    result = collection.insert_one(data)
    print('Query Executed for : ', table, result)

# the below function is a helper function used to get the latest version of Message registries and privilege registry data files.
def compareVersionNumber(version1, version2):
    version1Array = version1.split('.')
    version2Array = version2.split('.')
    n = min(len(version1Array), len(version2Array))
    for i in range(n):
        v1 = int(version1Array[i])
        v2 = int(version2Array[i])
        if v1 > v2:
            return 0
        if v1 < v2:
            return 1
    return 0

#the below function is used to insert Message Registry data into the database.
def initializeMessageRegistryDB(dir_path):
    allFiles = []
    for path, currentDirectory, files in os.walk(dir_path):
        for file in files:
            if file.endswith('.json'):
                allFiles.append(os.path.join(path, file))
    data = None
    recentFilesMap = {}
    for file in allFiles:
        with open(file, 'r') as f:
            data = json.load(f)
        if '@odata.type' in data:
            if "PrivilegeRegistry" in data['Id']:
                curr_version_number = data['Id'].split('_')[1]
            else:
                curr_version_number = data['Id'][data['Id'].find('.')+1:]
            if data['Name'] not in recentFilesMap:
                recentFilesMap[data['Name']] = dict(
                    versionNumber=curr_version_number, file=file)
            else:
                prev_version_number = recentFilesMap[data['Name']
                                                     ]["versionNumber"]
                if compareVersionNumber(prev_version_number, curr_version_number) == 1:
                    recentFilesMap[data['Name']] = dict(
                        versionNumber=curr_version_number, file=file)

    for k, v in recentFilesMap.items():
        filePath = v['file']
        with open(filePath, 'r') as f:
            data = json.load(f)
        table_name = data['@odata.type'].split(".")[-1]
        executeMongoQuery(data, table_name)

#The below function inserts mockup data from json files into the database.
def initializeDB(mockup_dir_path):
    allFiles = []
    for path, currentDirectory, files in os.walk(mockup_dir_path):
        for file in files:
            if file.endswith('.json'):
                allFiles.append(os.path.join(path, file))
    for file in allFiles:
        data = None
        with open(file, 'r') as f:
            data = json.load(f)
        if '@odata.type' in data:
            if "MessageRegistry" in data['@odata.type']:
                continue
            table_name = data['@odata.type'].split(".")[-1]
            executeMongoQuery(data, table_name)

#the below function is used to insert Privilege Registry data into the database.
def createPrivilegeDatabase(mockup_dir_path):
    tempPrivilegeRegistryDirName = 'privilegeRegistry'
    os.makedirs(tempPrivilegeRegistryDirName)
    os. chdir(tempPrivilegeRegistryDirName)
    redfishCreds = credentials['redfish_creds']
    file_name = redfishCreds['priviledge_file_name']
    zip_file_name = file_name + '.zip'
    zip_file_url = redfishCreds['mockup_url'] + zip_file_name
    print('Downloading the Mockups from Redfish Server : ', zip_file_url)
    wget.download(zip_file_url)
    mockup_zip = ZipFile(zip_file_name)
    mockup_zip.extractall()
    mockup_zip.close()
    curr_dir = mockup_dir_path + "/" + tempPrivilegeRegistryDirName
    initializeMessageRegistryDB(curr_dir)

#The below function downloads the redfish mockup data if json_file_path is not specified in config.
#If json_file_path is specified it reads json files from that path.
def download_and_initialize_redfish_mockups():
    currrent_dir = os.getcwd()
    if configJson["json_file_path"] == "":
        tempMockupDirName = "mockups"        
        redfishCreds = credentials['redfish_creds']
        mockups_dir = currrent_dir+'/'+tempMockupDirName
        file_name = redfishCreds['mockup_file_name']
        zip_file_name = file_name + '.zip'
        zip_file_url = redfishCreds['mockup_url'] + zip_file_name
        print('Downloading the Mockups from Redfish Server : ', zip_file_url)
        mockup_dir_name = redfishCreds['mockup_dir_name']
        mockup_dir_path = mockups_dir + '/' + mockup_dir_name + '/'

        print(mockup_dir_path)

        if os.path.exists(mockups_dir) == True:
            shutil.rmtree(mockups_dir)

        os.makedirs(mockups_dir)
        os. chdir(mockups_dir)
        wget.download(zip_file_url)
        mockup_zip = ZipFile(zip_file_name)
        mockup_zip.extractall()
        mockup_zip.close()

        initializeDB(mockup_dir_path)

        # PrivilegeRigistry
        createPrivilegeDatabase(mockups_dir)
        print(mockups_dir)
        os.chdir(currrent_dir)
        # remove mockup dir
        if os.path.exists(mockups_dir):
            shutil.rmtree(mockups_dir)
    else:
        initializeDB(configJson["json_file_path"])
        tempMockupDirName = "mockups"        
        redfishCreds = credentials['redfish_creds']
        mockups_dir = currrent_dir+'/'+tempMockupDirName
        os.makedirs(mockups_dir)
        os. chdir(mockups_dir)
        createPrivilegeDatabase(mockups_dir)
        os.chdir(currrent_dir)
        # remove mockup dir
        if os.path.exists(mockups_dir):
            shutil.rmtree(mockups_dir)    

    os.chdir(currrent_dir)

#The below function clones the github repository.
def cloneRepo():
    currrent_dir = os.getcwd()
    repoName = credentials['repo_name']
    repositoryUrl = credentials['repository_url']
    print('Cloning the Repository : ', repoName)
    repo_dir = currrent_dir+'/'+repoName
    print('Clonning Dir: ', currrent_dir)
    print('repo_dir: ', repo_dir)
    if os.path.exists(repo_dir) == True:
        shutil.rmtree(repo_dir)

    # cloning the repository
    os.system("git clone -c core.longpaths=true " + repositoryUrl)

    # Generate Certificates for Authorization
    generateCertificates(currrent_dir, repo_dir)

#The below function is used to generate certificates for authorization
def generateCertificates(currrent_dir, repo_dir):
    print('Generating Certificates....')
    certDir = repo_dir + \
        '/Redfish_Server/src/main/java/com/redfishserver/Redfish_Server/config/certs'
    os. chdir(certDir)

    os.system('openssl genrsa -out privatekey.pem 2048')
    os.system('openssl rsa -in privatekey.pem -pubout -out publickey.pem')
    os.system(
        'openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in privatekey.pem -out pkcs8.key')
    os. chdir(currrent_dir)

#The below function downloads all Redfish models from the DMTF website
def downloadModels():
    currrent_dir = os.getcwd()
    destination_folder = currrent_dir + credentials['repo_all_models_path']
    os. chdir(destination_folder)
    modelDirName = credentials['repo_all_models_dir_name']
    if os.path.exists(modelDirName) == True:
        shutil.rmtree(modelDirName)
    os.mkdir(modelDirName)
    action_info_schema = getLatestRegistrySchema(credentials['action_info_schema_url'], "ActionInfo_ActionInfo")
    generateModels(action_info_schema,currrent_dir)
    privilege_registry_schema = getLatestRegistrySchema(credentials['privilege_registry_schema_url'],  "PrivilegeRegistry_PrivilegeRegistry")
    generateModels(privilege_registry_schema,currrent_dir)
    message_registry_schema = getLatestRegistrySchema(credentials['message_registry_schema_url'], 'MessageRegistry_MessageRegistry')
    generateModels(message_registry_schema,currrent_dir)
    schema_url = credentials['schema_url']
    generateModels(schema_url,currrent_dir)
    updateRedfishModelswithMongoDBAnnotations(currrent_dir)

#The below funciton is a helper function to get latest schema for Action Info, MessageRegistry and PrivilegeRegistry.
def getLatestRegistrySchema(schema_url, schema_name):
    yaml_file_name = schema_url.split("/")[-1]
    wget.download(schema_url)
    with open(yaml_file_name, 'r') as file:
        yaml_data = yaml.safe_load(file)
    latest_version = yaml_data['components']['schemas'][schema_name]['anyOf'][-1]['$ref'].split("#")[0]
    os.remove(yaml_file_name)
    return latest_version

#The below function generates the Java Redfish models from Yaml files using OpenAPI generator
def generateModels(schema_url,currrent_dir):
    generator_url = credentials['generator_url']
    destination_folder = currrent_dir + credentials['repo_all_models_path']
    os.chdir(destination_folder)
    modelDirName = credentials['repo_all_models_dir_name']
    destination_folder = destination_folder + '/' + modelDirName
    yaml_file_name = schema_url.split("/")[-1]
    jar_file_name = generator_url.split("/")[-1]
    wget.download(generator_url)
    wget.download(schema_url)
    if(str(yaml_file_name).startswith("ActionInfo") or str(yaml_file_name).startswith("PrivilegeRegistry")  or str(yaml_file_name).startswith("MessageRegistry")):
        with open(yaml_file_name, 'r') as file:
            yaml_data = yaml.safe_load(file)
        yaml_data["openapi"] = '3.0.1'
        yaml_data["paths"] = object()
        temp ={}
        temp["title"] = "Redfish Server API"
        yaml_data["info"] = temp
        del yaml_data["title"]
        with open(yaml_file_name, 'w') as file:
            yaml.dump(yaml_data, file)
    json_data = {
        "basePackage": "com.tutorial.codegen",
        "configPackage": "com.tutorial.codegen.config",
        "apiPackage": "com.tutorial.codegen.controllers",
        "modelPackage": "com.tutorial.codegen.model",
        "groupId": "com.tutorial",
        "artifactId": "spring-boot-codegenerator"}
    with open('conf.json', 'w') as f:
        json.dump(json_data, f, indent=2)

    os.system("java -jar openapi-generator-cli-4.3.1.jar generate -g spring -i " +
              yaml_file_name + " -c conf.json -o spring-boot-codegenerator --skip-validate-spec")
    src = os.getcwd().replace("\\", "/") + \
        "/spring-boot-codegenerator/src/main/java/com/tutorial/codegen/model"
    src_files = os.listdir(src)
    for file_name in src_files:
        full_file_name = os.path.join(src, file_name)
        if os.path.isfile(full_file_name):
            shutil.copy(full_file_name, destination_folder)

    os.remove(jar_file_name)
    os.remove("conf.json")
    os.remove(yaml_file_name)
    shutil.rmtree(os.getcwd().replace("\\", "/")+'/spring-boot-codegenerator')
    

#THe below function updates the Redfish models to remove the version number and add MongoDB annotations to all models.
#This function also generates Reading and Writing converters for all Redfish Enums.
def updateRedfishModelswithMongoDBAnnotations(prev_currrent_dir):
    currrent_dir = os.getcwd()
    models_dir = currrent_dir + "/" + credentials['repo_all_models_dir_name']
    updated_class_name_map = {}
    updated_enum_name_map = {}
    arr_file = []
    files_to_remove = []
    enums_list = []

    for filename in os.listdir(models_dir):
        arr_file.append(filename)
    #Modifying the names of Enums to replace version number with underscore
    for f in range(len(arr_file)-1, -1, -1):
        file = os.path.join(models_dir, arr_file[f])
        file = file.replace('\\', '//')
        with open(file, 'r') as filehandle:
            data = filehandle.readlines()

        i = 0
        while i < len(data):
            if data[i].startswith("public enum"):
                table_name = data[i].split(" ")[2]
                new_table_name = ""
                for z in range(len(table_name)):
                    if table_name[z] == "V" and table_name[z+1].isnumeric():
                        x = z+1
                        while table_name[x].isnumeric():
                            x += 1
                        new_table_name = table_name[:z] + "_" + table_name[x:]
                        if new_table_name not in updated_enum_name_map:
                            updated_enum_name_map[new_table_name] = table_name
                            data[i] = "public enum " + new_table_name + "   {\n"
                            break
                        else:
                            files_to_remove.append(table_name)
                            break

            i += 1
        with open(file, 'w') as filehandle:
            filehandle.writelines(data)
    #Updating the Enum names used in each class
    for filename1 in os.listdir(models_dir):
        updated_file = os.path.join(models_dir, filename1)
        with open(updated_file, 'r') as filehandle:
            data = filehandle.readlines()
        i = 0
        while i < len(data):
            for key, value in updated_enum_name_map.items():
                if key in data[i]:
                    data[i] = data[i].replace(value, key)
            i += 1

        with open(updated_file, 'w') as filehandle:
            filehandle.writelines(data)
    #Updating the models to include MongoDB annotations
    for f in range(len(arr_file)-1, -1, -1):
        file = os.path.join(models_dir, arr_file[f])
        file = file.replace('\\', '//')
        with open(file, 'r') as filehandle:
            data = filehandle.readlines()

        i = 0
        while i < len(data):
            if data[i].startswith("package"):
                data[i] = "package com.redfishserver.Redfish_Server.RFmodels.AllModels;\n\n"
                data.insert(i+1, "import org.springframework.data.mongodb.core.mapping.Document;\n")
                data.insert(i+2, "import org.springframework.data.mongodb.core.mapping.Field;\n")
                data.insert(i+3, "import org.bson.types.ObjectId;\n")
                data.insert(i+4, "import org.springframework.data.annotation.Id;")
                i += 4
            if data[i].startswith("import com.tutorial.codegen.model"):
                data[i] = data[i].replace("com.tutorial.codegen.model","com.redfishserver.Redfish_Server.RFmodels.AllModels")
            if data[i].startswith("public class"):
                data.insert(i + 1, "  @Field(\"_id\")\n  @Id\n  private ObjectId _id;\n\n")
                if "Collection" in data[i]:
                    table_name = data[i].split(" ")[2]
                    updated_table_name = table_name.split("Collection")[0] + "Collection"
                    data[i] = "public class " + updated_table_name +"   {\n"
                    data.insert(i, "@Document(\"" + updated_table_name + "\")\n")
                    updated_class_name_map[updated_table_name] = table_name
                else:
                    table_name = data[i].split(" ")[2]
                    new_table_name = ""
                    new_class_name = ""
                    for k in range(len(table_name) - 1):
                        if table_name[k] == "V" and table_name[k+1].isnumeric():
                            j = k+1
                            while table_name[j].isnumeric():
                                j += 1
                            if table_name[:k] == table_name[j:]:
                                new_table_name = table_name[:k]
                            else:
                                new_table_name = table_name[:k] + "_" + table_name[j:]
                            new_class_name = table_name[:k] + "_" + table_name[j:]
                            if new_class_name not in updated_class_name_map:
                                updated_class_name_map[new_class_name] = table_name
                                data[i] = "public class " + new_class_name + "   {\n"
                                data.insert(i, "@Document(\"" + new_table_name + "\")\n")
                                break
                            else:
                                files_to_remove.append(table_name)
                                break
                i += 3
            if data[i].startswith("  @JsonProperty"):
                property_name = data[i].split('"')[1]
                data.insert(i+1, "  @Field(\"" + property_name + "\")\n")
                i += 1
            if data[i].startswith("@Pattern"):
                data[i] = data[i].replace("\\", "\\\\")
            if "OdataV4IdRef" in data[i]:
                data[i] = data[i].replace("OdataV4IdRef", "Odata_IdRef")
            i += 1

        with open(file, 'w') as filehandle:
            filehandle.writelines(data)
    #Changing the class names to replace version number with underscore.
    for filename1 in os.listdir(models_dir):
        updated_file = os.path.join(models_dir, filename1)
        with open(updated_file, 'r') as filehandle:
            data = filehandle.readlines()

        i = 0
        while i < len(data):
            for k in range(len(data[i])):
                if k + 1 < len(data[i]) and data[i][k] == "V" and data[i][k+1].isnumeric() and data[i][k+2].isnumeric():
                    j = k+2
                    while data[i][j].isnumeric():
                        j += 1
                    data[i] = data[i].replace(data[i][k:j], "_")
                    continue
            i += 1

        with open(updated_file, 'w') as filehandle:
            filehandle.writelines(data)
    #Updating the class names in every file.
    for filename1 in os.listdir(models_dir):
        updated_file = os.path.join(models_dir, filename1)
        with open(updated_file, 'r') as filehandle:
            data = filehandle.readlines()
        i = 0
        while i < len(data):
            for key, value in updated_class_name_map.items():
                if key in data[i]:
                    data[i] = data[i].replace(value, key)
            i += 1

        with open(updated_file, 'w') as filehandle:
            filehandle.writelines(data)

    print('current dir', os.getcwd())
    print('models_dir', models_dir)
    os.chdir(models_dir)
    #Keeping the latest version for each model and removing the old version models.
    for f in files_to_remove:
        os.remove(f+'.java')
    #Updating the file names to remove version numbers with underscore.
    for k, v in updated_class_name_map.items():
        os.rename(v+'.java', k+'.java')
    for k, v in updated_enum_name_map.items():
        os.rename(v+'.java', k+'.java')
    #Updating Task_Task Model to integrate our custom TaskMonitor class
    with open("Task_Task.java", 'r') as filehandle:
        data = filehandle.readlines()
    i = 0
    while i < len(data):
        if data[i].startswith("package"):
            data.insert(i+1, "import com.redfishserver.Redfish_Server.RFmodels.custom.TaskMonitor;\n")
            i += 1
        if "String taskMonitor" in data[i]:
            data[i] = data[i].replace("String taskMonitor", "TaskMonitor taskMonitor")
        if "String getTaskMonitor()" in data[i]:
            data[i] = data[i].replace("String getTaskMonitor()", "TaskMonitor getTaskMonitor()")
        i += 1
    with open("Task_Task.java", 'w') as filehandle:
        filehandle.writelines(data)
    #Generating Enum Converters
    arr_file = []
    for filename in os.listdir(models_dir):
        arr_file.append(filename)
    for f in range(len(arr_file)-1, -1, -1):
        file = os.path.join(models_dir, arr_file[f])
        file = file.replace('\\','//')
        with open(file, 'r') as filehandle:
            data = filehandle.readlines()

        i = 0
        while i< len(data):
            if data[i].startswith("public enum"):
                table_name = data[i].split(" ")[2]
                enums_list.append(table_name)
                break                
            i+=1
    config_dir = prev_currrent_dir + "/" + credentials['repo_config_path']
    converters_dir = config_dir + "/converters"
    os.chdir(converters_dir)
    for converter in enums_list:
        with open(converter+"ReadingConverter.java", 'w') as filehandle:
            filehandle.write("package com.redfishserver.Redfish_Server.config.converters;\n\nimport com.redfishserver.Redfish_Server.RFmodels.AllModels." + converter + ";\nimport org.springframework.core.convert.converter.Converter;\nimport org.springframework.data.convert.ReadingConverter;\n\n@ReadingConverter\npublic class " + converter+"ReadingConverter implements Converter<String, " + converter + "> {\n    @Override\n    public " + converter + " convert(String source) {\n        return " + converter + ".fromValue(source);\n    }\n}")
        with open(converter+"WritingConverter.java", 'w') as filehandle:
            filehandle.write("package com.redfishserver.Redfish_Server.config.converters;\n\nimport com.redfishserver.Redfish_Server.RFmodels.AllModels." + converter + ";\nimport org.springframework.core.convert.converter.Converter;\nimport org.springframework.data.convert.WritingConverter;\n\n@WritingConverter\npublic class " + converter+"WritingConverter implements Converter<" + converter +",String> {\n    @Override\n    public String convert("+ converter + " source) {\n        return source.getValue();\n    }\n}")

    os.chdir(config_dir) #config directory
    with open("MongoConfiguration.java", 'r') as filehandle:
        config_data = filehandle.readlines()

    i = 0
    while i < len(config_data):
        if config_data[i].startswith("        return new MongoCustomConversions"):
            break
        i += 1
    for converter1 in enums_list:
        config_data.insert(i+1,"                new " + converter1+"ReadingConverter(),\n")
        config_data.insert(i+2,"                new " + converter1+"WritingConverter(),\n")

    with open("MongoConfiguration.java", 'w') as filehandle:
        filehandle.writelines(config_data)
    os.chdir(prev_currrent_dir)

#The below function loads the config file
def loadConfigJsonFile():
    global configJson
    global credentials
    with open("config.json", 'r') as f:
        configJson = json.load(f)
    credentials = configJson['credentials']

#The below function start the Redfish Server
def start_Redfish_Server():
    current_dir = os.getcwd()
    pom_path = current_dir + configJson['credentials']['repo_server_pom_path']
    os.chdir(pom_path)
    os.system('mvn clean install')
    os.system('mvn spring-boot:run')

#The below function is the entry point of this file
if __name__ == "__main__":
    loadConfigJsonFile()
    cloneRepo()
    downloadModels()
    download_and_initialize_redfish_mockups()
    start_Redfish_Server()

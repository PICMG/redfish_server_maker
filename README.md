# OpenAPI Automation for Redfish Server Generation

<img src=" 	https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white" />   <img src="https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white"/>

## Background
Redfish is an API standard created by the DTMF organization (https://www.dmtf.org) that uses RESTful interface semantics to access a schema-based data models
for system management. Redfish is suitable for a wide range of applications, from stand-alone servers to complex, composable infrastructures, and to large-scale cloud environments. PICMG (https://www.PICMG.org) has been working in collaboration with the DMTF to extend the Redfish API to support the needs of industrial automation and Factory 4.0.  

This project auto-generates an open-sourced framework for a generic Redfish Server using OpenAPI (https://www.openapis.org) and Java. The resulting auto-generated framework can then be extended to support server-specific behaviors.  The framework can also be used to test new redfish schema in actual use-cases. 

**This code is provided as-is.  If you choose to use this project, or its resulting auto-generated code, it is your responsibility to determine its suitability for your intended purpose.**

### features
The features for this project are:
* fully configurable through json configuration file
* automated code generation using python scripting
* automated database generation from mockup url and Redfish schema

The features for the resulting server framework are:
* Redfish role-based security
* Redfish messaging
* Redfish events
* Static and dynamic data sources
* MongoDB database
* Java 1.8 compatible source code

## Installation
There are multiple steps for installing the tools to support this project.  These are outlined below.
### Install OpenJdk 17
Execute the following commands at the linux terminal prompt:
```
sudo apt-get install openjdk-17-jdk
java -version
```
If this step has completed properly, you should see the following message:
```
openjdk version "17.0.8.1" 2023-08-24
OpenJDK Runtime Environment (build 17.0.8.1+1-Ubuntu-0ubuntu122.04)
OpenJDK 64-Bit Server VM (build 17.0.8.1+1-Ubuntu-0ubuntu122.04, mixed mode, sharing)
```
### Install Apache Maven
Execute the following commands at the linux terminal prompt:
```
wget https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz
tar -xvf apache-maven-3.8.8-bin.tar.gz
sudo mv apache-maven-3.8.8 /opt/
rm apache-maven-3.8.8-bin.tar.gz
```
Add the following to the end of your user .profile file:
```
M2_HOME='/opt/apache-maven-3.8.8'
PATH="$M2_HOME/bin:$PATH"
export PATH
```
Execute the following command to apply changes to your session:
```
source .profile
```
### Install MongoDB Community
Install MongoDB Community Edition by following the instructions found on the MongoDB web site (https://www.mongodb.com).
You may wish to set the mongodb service to start each time your machine boots (see instructions on mongodb.com).
install mongo client tools
sudo apt install mongodb-clients

### Installing Python Library Modules
Depending on your python installation, you may need to install wget, pymongo and yaml libraries.  you can do this by executing the following command-line prompt:
```
sudo apt-get install python3-wget python3-pymongo python3-yaml xmltodict
```
Note: if you get errors during the build process that mongodb keys cannot have dots in them (e.g., "@odata.id') then you need to update the version of pymongo that you are using. 

## Configuration
Once installation is complete, you may wish to modify the config.json file found in the root of this project repository.  the only values that might need to be chaged are:
* mockup_file_path - set this parameter to the path of a local Redfish mockup folder and the mockup will be used to create the server instance instead of a mockup from the DMTF mockup bundle.
* local_schema_path - If you have created custom schema, set this path to the location of the custom schema folder.  For this parameter to work properly, the schema path must have two subfolders: yaml, and json.  The yaml path should hold .yaml files for each of your new schema as well as a openapi.yaml file for all the Redfish objects including your new files.  A best practice is to reference the standard schema on http:/redfish.dmtf.org/schemas/v1, and all the new models using a local file path.  The json path should include the json equivalent of the yaml files.
* credentials.repository_destination - a path to where you want the resulting server instance to be built.
* credentials.redfish_creds
    * mockup_url - a path to the DMTF Redfish repository where mockup bundles are stored
    * mockup_file_name - the name of the mockup bundle to use for the server build
    * priviledge_file_name - the name of the privilege definitions file on the DMTF Redfish site
    * mockup_dir_name - the folder name within the mockup bundle to use for the server mockup.
By default, the build process will instantiate a server based on DSP2043_2022.2 (public-rackmount1)

## Building A Server Instance
from the command prompt, execute the following command
```
python3 initializeRedfishServer.py
```
The automated script process will
1. clone the server template files from PICMG.org
2. download redfish schema from DMTF.org
3. execute the OpenAPI generator to create java code for the schema
4. download the redfish mockup from DMTF.org (or copy the local mockup files)
5. populate the mongoDB database (RedfishDB) with tables for the server build
6. build the server instance by invoking maven
7. run the server instance

## Testing the Server Instance
A rudementary server test is provided as an example in the Tests folder of this repository.  To execute the tests, run the following command at the command prompt in the Tests folder.
```
python3 Python_API_Tests.py
```

## Completing the server
Once built, you may need to implement behaviors for the controllers for each of the classes that you use.  Future work for this template will automate error checking and error responses according to the Redfish specification.  More information about the OpenAPI architecture can be found at the OpenAPI website (https://learn.openapis.org/) 

## Acknowledgement
The orginal implementation of this automated framework was developed for PICMG by a student team at Arizona State University.  PICMG is thankful to have worked with ASU on this project and especially thanks the students for their efforts:
- Manan Soni
- Sambudha Nath
- Govind Venugopal
- Mayank Tewatia
- Vishnu Preetham Reddy Dasari
- Sudhanva Hanumanth Rao

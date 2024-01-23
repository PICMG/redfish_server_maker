# OpenAPI Automation for Redfish Server Generation

<img src=" 	https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white" />   <img src="https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white"/>

## Background
Redfish is an API standard created by the DTMF organization (https://www.dmtf.org) that uses RESTful interface semantics to access a schema-based data models
for system management. Redfish is suitable for a wide range of applications, from stand-alone servers to complex, composable infrastructures, and to large-scale cloud environments. PICMG (https://www.PICMG.org) has been working in collaboration with the DMTF to extend the Redfish API to support the needs of industrial automation and Factory 4.0.  

This project creates a MongoDB representation of all the required datastructures for a referenced Redfish server mockup. The resulting database is intended to be used with the PICMG/server_template GitHub repository.  The combined framework can then be extended to support server-specific behaviors.  The framework can also be used to test new redfish schema in actual use-cases. 

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
Install MongoDB Community Edition by following the instructions found on the MongoDB website (https://www.mongodb.com).
You may wish to set the mongodb service to start each time your machine boots (see instructions on mongodb.com).
install mongo client tools
sudo apt install mongodb-clients

### Installing Python Library Modules
Depending on your python installation, you may need to install wget, pymongo and yaml libraries.  you can do this by executing the following command-line prompt:
```
sudo apt-get install python3-wget python3-pymongo python3-yaml xmltodict
```
Note: if you get errors during the build process that mongodb keys cannot have dots in them (e.g., '@odata.id') then you need to update the version of pymongo that you are using. 

### Downloading the PICMG Redfish server template
Download the most recent version of the PICMG Redfish server template project from GitHub (https://github.com/PICMG/redfish_server_template) by using the following command at the linux command prompt:
```
gh repo clone PICMG/redfish_server_template
```

### Creating Security Certificates
The server template requires two different security certificates for proper function.  The following instructions create self-signed certificates.  This is sufficient for debugging purposes, however, for actual deployment, signed certificates should be used.
The first certificate is used for authentication.  It should be placed in the /src/main/java/org/picmg/redfish_server_template/config/certs folder within the picmg_server_template repository that was downloaded in the previous step.  To do this:
1. open a terminal window
2. change your current directory to the /src/main/java/org/picmg/redfish_server_template/config/certs folder within the redfish_server_template repository
3. execute the following commands
```
openssl genrsa -out privatekey.pem 2048
openssl rsa -in privatekey.pem -pubout -out publickey.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in privatekey.pem -out pkcs8.key
```
The second certificate is required for HTTPS encryption.  This certificate can be placed anywhere but the server files will need to be updated so that the server can access the proper key pair.  To make the key pair, execute the following commands at the linux command prompt:
```
keytool -genkeypair -alias <key_alias> -keyalg RSA -keysize 4096 -storetype PKCS12 -keystore <filename>.p12 -validity 3650 -storepass <key_store_password>
```
Lastly, the application-server.yml file within the /src/main/java/resources folder of the redfish_server_template repository need to be updated.  The value of "key-store" should point to the "p12" file that was created with the keytool utility.  The value of "key-store-password" should be changed to the value of <key_store_password> that was used when generating the key pair.  The value of "key-alias" should match the value of the <key_alias> that was used when creating the key pair, and the value of the "key-password" should match the "key-store-password". 

## Configuration
Once installation is complete, you may wish to modify the config.json file found in the root of this project repository.  the only values that might need to be changed are:
* mockup_file_path - set this parameter to the path of a local Redfish mockup folder and the mockup will be used to create the server instance instead of a mockup from the DMTF mockup bundle.
* local_schema_path - If you have created custom schema, set this path to the location of the custom schema folder.  For this parameter to work properly, the schema path must have two subfolders: yaml, and json.  The yaml path should hold .yaml files for each of your new schema as well as an openapi.yaml file for all the Redfish objects including your new files.  A best practice is to reference the standard schema on http:/redfish.dmtf.org/schemas/v1, and all the new models using a local file path.  The json path should include the json equivalent of the yaml files.
* credentials.https_port: The port the server should use for https requests. 
* credentials.http_port: The port the server should use for http requests.
* credentials.path_to_https_keystore: The path and filename of the certificate that should be used for HTTPS communications.
* credentials.key_alias: The alias name of the key to use within the specified certificate file.
* credentials.key_store_password: The keystore password for the certificate that should be used for HTTPS communications.
* credentials.key_password: The key password for the certificate that should be used for HTTPS communications.
* credentials.redfish_creds
    * mockup_url - a path to the DMTF Redfish repository where mockup bundles are stored
    * mockup_file_name - the name of the mockup bundle to use for the server build
    * privilege_file_name - the name of the privilege definitions file on the DMTF Redfish site
    * mockup_dir_name - the folder name within the mockup bundle to use for the server mockup.
By default, the build process will instantiate a server based on DSP2043_2022.2 (public-rackmount1)

## Building the Database Files
from the command prompt, execute the following command
```
python3 initializeRedfishServer.py
```
The automated script process will
1. download redfish schema from DMTF.org
2. download the redfish mockup from DMTF.org (or copy the local mockup files)
3. populate the mongoDB database (RedfishDB) with tables for the server build

## Starting the Server
Open a linux terminal and execute the following command from the root of the redfish_server_template repository:
```
mvn spring-boot:run
```

## Testing the Server Instance
A simple server test is provided as an example in the Tests folder of this repository.  To execute the tests, run the following command at the command prompt in the Tests folder.
```
python3 Python_API_Tests.py
```

## Customizing the server
Once built, you may need to implement behaviors for the controllers for each of the classes that you use.  More information on this can be found in the readme for the redfish_server_template.

## Acknowledgement
The original implementation of this automated framework was developed for PICMG by a student team at Arizona State University.  PICMG is thankful to have worked with ASU on this project, and especially thanks the students for their efforts:
- Manan Soni
- Sambudha Nath
- Govind Venugopal
- Mayank Tewatia
- Vishnu Preetham Reddy Dasari
- Sudhanva Hanumanth Rao

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
sudo apt-get install python3-wget python3-pymongo python3-yaml
```

## Configuration
 
## Building A Server Instance

## Extending Behaviors

### Actions

### Adding Schema

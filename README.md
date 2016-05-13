[![Build Status](https://travis-ci.org/Unipoole/unipoole-service.svg?branch=master)](https://travis-ci.org/Unipoole/unipoole-service)
[![License](https://img.shields.io/badge/License-ECL%202.0-blue.svg)](https://opensource.org/licenses/ECL-2.0)
# unipoole-service
The Unipoole Service is the intelligence and processing engine of all data communication between the Unipoole Client and Sakai.
This is a deployable project.

** Unipoole Service Architecture **
<img src="http://unipoole.github.io/images/unipoole-service/service-architecture.jpg" style="max-width: 100%" />

** Highlevel Overview **
<img src="http://unipoole.github.io/images/unipoole-service/highlevel-overview.png" style="max-width: 100%" />

This diagram illustrates on a very high level how unipoole, unipoole clients, and sakai fit together.  A unipoole server will host the Unipoole-Service, and the Admin tool.
The admin tool communicates with the Unipoole-Service using REST calls. When a client is created using the Admin tool, the bulk of the work is actually done on the Unipoole-Service (initiated with a REST call from the Admin Tool).
Unipoole-Service communicates to a single sakai server via SOAP Webservice calls. (take note there is NOT a load balancer between Unipool-Service and Sakai)
When a student connects to sakai the user will connect to a Load Balancer which in turn works with a selected sakai instance.
When a student uses the Unipoole client (desktop or mobile) the client connects directly to the Unipoole Service using REST calls. The unipoole client never directly communicates with Sakai.

## Building
```bash
git clone https://github.com/Unipoole/unipoole-service.git
cd unipoole-service
mvn clean install
```

## Deployment
If the Maven build is successful there will be a `unipoole-service.war` archive in the target folder of the build. This archive can be deployed to your Tomcat instance.
Please note: If you deploy to more then one instance only one instance can have the property `creator.content.manage.modules` set to `true`.

## Documentation
* [Configuration](./docs/configuration.md)
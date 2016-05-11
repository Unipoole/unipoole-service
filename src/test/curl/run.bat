@ECHO OFF
set BASE_URL=http://unipoole.yourdomain.ac.za/unipoole-service
set USERNAME=your_student
set MODULE_ID=AFL1501-13-S1
set CLIENT_VERSION=1.0.0
set DEVICE_ID=123
set PASSWORD=YourPassword
set TOOLNAME=sakai.announcements
rem doc: The full url is broken up in;
rem doc: - Base: The part above that include the context
rem doc: - subset: what the service is about
rem doc: - name: the name of the call, "what do you want"
rem doc: - parameters.
rem doc: In all services deviceId is optional.
rem doc:  So it it is in the parameters then there is a service where it is not in the parameters
rem doc:  If it is in the body you don't need to set it

rem -- auth --
@ECHO --- USER LOGIN ---
rem doc: Logs a user in and return a login object containing the session id if successful (username, password)
@ECHO ON
curl -X GET %BASE_URL%/service-auth/login/%USERNAME%/%PASSWORD%
curl -X POST --header Content-Type:"application/json" --data {\"password\":\"%PASSWORD%\"} %BASE_URL%/service-auth/login/%USERNAME%
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- USER REGISTER ---
rem doc: Register a user on the synch service. (username, moduleId, clientVersion)
@ECHO ON
curl -X POST --header Content-Type:"application/json" --data {"moduleId":"math101","deviceId":"123","password":"%PASSWORD%","tools":{"client.base":{"clientCodeVersion":"1.0.0","clientContentVersion":"201301010101"}}} %BASE_URL%/service-auth/register/%USERNAME%
rem {"moduleId":"math101","deviceId":"123","password":"%PASSWORD%",
rem		"tools":{
rem			"unisa.resources":{"clientCodeVersion":"1.0.0","clientContentVersion":"1.0.0"},
rem			"unisa.schedule":{"clientCodeVersion":"1.0.0","clientContentVersion":"1.0.0"}
rem		}
rem	}
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- STATUS ---
rem doc: this retrieve versions of code and content for current stuff (toolName)
@ECHO ON
curl -X GET %BASE_URL%/service-status/usernames
curl -X GET %BASE_URL%/service-status/deviceids/%USERNAME%
curl -X GET %BASE_URL%/service-status/moduleids/%USERNAME%/%DEVICE_ID%
curl -X GET %BASE_URL%/service-status/clientCodeVersion
curl -X GET %BASE_URL%/service-status/clientContentVersion/%MODULE_ID%
curl -X GET %BASE_URL%/service-status/toolCodeVersion/%TOOLNAME%
curl -X GET %BASE_URL%/service-status/toolContentVersion/%MODULE_ID%/%TOOLNAME%
curl -X GET %BASE_URL%/service-status/codeVersions
curl -X GET %BASE_URL%/service-status/contentVersions/%MODULE_ID%
curl -X GET %BASE_URL%/service-status/tools
@ECHO OFF
rem doc: this retrieve versions of code and content for clients (users) (username, deviceid, moduleId, toolName)
@ECHO ON
curl -X GET %BASE_URL%/service-status/clientCodeVersion/%USERNAME%/%DEVICE_ID%
curl -X GET %BASE_URL%/service-status/clientContentVersion/%USERNAME%/%DEVICE_ID%/%MODULE_ID%
curl -X GET %BASE_URL%/service-status/toolCodeVersion/%USERNAME%/%DEVICE_ID%/%TOOLNAME%
curl -X GET %BASE_URL%/service-status/toolContentVersion/%USERNAME%/%DEVICE_ID%/%MODULE_ID%/%TOOLNAME%
curl -X GET %BASE_URL%/service-status/codeVersions/%USERNAME%/%DEVICE_ID%
curl -X GET %BASE_URL%/service-status/contentVersions/%USERNAME%/%DEVICE_ID%/%MODULE_ID%
curl -X GET %BASE_URL%/service-status/clientStatus/%USERNAME%/%DEVICE_ID%/%MODULE_ID%
curl -X GET %BASE_URL%/service-status/status/%USERNAME%/%DEVICE_ID%/%MODULE_ID%/%TOOLNAME%
@ECHO OFF

@ECHO  
@ECHO ------------------------------------------------------------------------------
@ECHO --- CLIENT SYNCH/UPDATE ---
rem client
rem doc: Retrieve the updated version of the client. Just the files updated since the given version. the retrieved version will also be listed in the response (username, deviceId, clientVersion)
@ECHO ON
curl -X GET %BASE_URL%/service-synch/clientUpdate/%USERNAME%/%DEVICE_ID%/%CLIENT_VERSION%
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------
@ECHO --- TOOL SYNCH/UPDATE ---
rem doc: Retrieve the updated version of the tool code. Just the files updated since the given version. the retrieved version will also be listed in the response (username, deviceId, toolName, toolVersion)
@ECHO ON
curl -X GET %BASE_URL%/service-synch/toolUpdate/%USERNAME%/%DEVICE_ID%/%TOOLNAME%/334
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

rem content
@ECHO --- CONTENT SYNCH/UPDATE ---
rem doc: retrieve the current tool content (username, deviceId, moduleId, toolName, toolContentVersion)
@ECHO ON
curl -X POST --header Content-Type:"application/json" --data {\"password\":\"%PASSWORD%\"} %BASE_URL%/service-synch/clientContent/%USERNAME%/%DEVICE_ID%/%MODULE_ID%/100
curl -X POST --header Content-Type:"application/json" --data {\"password\":\"%PASSWORD%\"} %BASE_URL%/service-synch/contentUpdate/%USERNAME%/%DEVICE_ID%/%MODULE_ID%/%TOOLNAME%/100
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- COMBINATION SYNCH ---
rem doc: combination status check
@ECHO ON
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-synch/synchStatus/%USERNAME%/%DEVICE_ID%/%MODULE_ID%
curl -X POST --header Content-Type:"application/json" --data {\"deviceId\":\"%DEVICE_ID%\"} %BASE_URL%/service-synch/synchStatus/%USERNAME%/%MODULE_ID%
@ECHO OFF
rem {"status":"SUCCESS","message":null,"moduleId":"math101","deviceId":"123",
rem		"clientStatus":{"clientCodeVersion":"1.0.0","clientContentVersion":"1.0.0","currentCodeVersion":"1.0.0","currentContentVersion":"1.0.0","codeSynchSize":123,"contentSynchSize":456},
rem		"tools":{
rem			"resources":{"clientCodeVersion":"1.0.0","clientContentVersion":"1.0.0","currentCodeVersion":"1.0.0","currentContentVersion":"1.0.0","codeSynchSize":123,"contentSynchSize":456},
rem			"schedule":{"clientCodeVersion":"1.0.0","clientContentVersion":"1.0.0","currentCodeVersion":"1.0.0","currentContentVersion":"1.0.0","codeSynchSize":123,"contentSynchSize":456}
rem		}
rem	}
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- UPDATE TOOL VERSION ---
rem doc: updates the client's tool code and content versions (username, deviceId, toolname, version)
@ECHO ON
curl -X PUT --header Content-Type:"application/json" %BASE_URL%/service-synch/clientCodeVersion/%USERNAME%/%DEVICE_ID%/%CLIENT_VERSION%
curl -X PUT --header Content-Type:"application/json" %BASE_URL%/service-synch/codeVersion/%USERNAME%/%DEVICE_ID%/%TOOLNAME%/123
curl -X PUT --header Content-Type:"application/json" %BASE_URL%/service-synch/clientContentVersion/%USERNAME%/%DEVICE_ID%/%MODULE_ID%/%CLIENT_VERSION%
curl -X PUT --header Content-Type:"application/json" %BASE_URL%/service-synch/contentVersion/%USERNAME%/%DEVICE_ID%/%MODULE_ID%/%TOOLNAME%/123
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- UPDATE TOOL CONTENT ---
rem doc: uploads the client's tool content (username, deviceId, moduleId, toolName)
@ECHO ON
curl -X PUT --header Content-Type:"application/json" --data {\"password\":\"%PASSWORD%\",\"Stuff\":\"here\"} %BASE_URL%/service-synch/content/%USERNAME%/%DEVICE_ID%/%MODULE_ID%/%TOOLNAME%
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- Client Creator Retrieve Modules ---
rem doc: Retrieve all the modules for the criteria (year, semester, moduleCode)
@ECHO ON
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/modules/13/S1/AFL1501
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/tools/%MODULE_ID%
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- Client Creator Create client ---
rem doc: Create Clients
@ECHO ON
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/createClient
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/createClient/%MODULE_ID%
rem All the POST create methods take a optional map properties
curl -X POST --header Content-Type:"application/json" --data {\"name\":\"piet\",\"email\":\"piet@yourdomain.ac.za\",\"tools\":[\"announcements\",\"faq\"]} %BASE_URL%/service-creator/createClient/%MODULE_ID%
curl -X POST --header Content-Type:"application/json" --data {\"password\":\"%PASSWORD%\"} %BASE_URL%/service-creator/myClient/%USERNAME%
curl -X POST --header Content-Type:"application/json" --data {\"password\":\"%PASSWORD%\"} %BASE_URL%/service-creator/myClient/%USERNAME%/%MODULE_ID%
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/download/downloadKey
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/download/file/downloadKey
curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/download/compressed/downloadKey
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------

@ECHO --- Client Code Creator ---
rem doc: Manages client code (toolname, tool version, location)
@ECHO ON
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"somewhere\"} %BASE_URL%/service-code/new/client/1.0.1
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"somewhere\"} %BASE_URL%/service-code/new/%TOOLNAME%/1.0.1
@ECHO OFF
@ECHO  
@ECHO ------------------------------------------------------------------------------
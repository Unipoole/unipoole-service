@ECHO OFF
set BASE_URL=http://unipoole.yourdomain.ac.za/unipoole-service
set USERNAME=your_student
rem set MODULE_ID=AFL1501-13-S1
set MODULE_ID=AFL1501-13-S1-43T
set CLIENT_VERSION=1.0.0
set DEVICE_ID=3
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

@ECHO ON

curl -X GET --header Content-Type:"application/json" %BASE_URL%/service-creator/modules/13/S1/AFL
@ECHO ------------------------------------------------------------------------------
@ECHO OFF
set BASE_URL=http://unipoole.yourdomain.ac.za/unipoole-service
set LOCATION=C:/workspace/client-base/trunk/target
@ECHO ON
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/client.base-1.0.0.zip\"} %BASE_URL%/service-code/new/client.base/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/sakai.announcements-1.0.0.zip\"} %BASE_URL%/service-code/new/sakai.announcements/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/sakai.home-1.0.0.zip\"} %BASE_URL%/service-code/new/sakai.home/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/sakai.melete-1.0.0.zip\"} %BASE_URL%/service-code/new/sakai.melete/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/sakai.resources-1.0.0.zip\"} %BASE_URL%/service-code/new/sakai.resources/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/sakai.samigo-1.0.0.zip\"} %BASE_URL%/service-code/new/sakai.samigo/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/sakai.schedule-1.0.0.zip\"} %BASE_URL%/service-code/new/sakai.schedule/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/sakai.yaft-1.0.0.zip\"} %BASE_URL%/service-code/new/sakai.yaft/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/unisa.faqs-1.0.0.zip\"} %BASE_URL%/service-code/new/unisa.faqs/1.0.0
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"%LOCATION%/unisa.welcome-1.0.0.zip\"} %BASE_URL%/service-code/new/unisa.welcome/1.0.0
@ECHO ------------------------------------------------------------------------------

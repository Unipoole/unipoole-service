# Tool Versions
V_CLIENT_BASE=1.0.4
V_SAKAI_ANNOUNCEMENTS=1.0.0
V_SAKAI_HOME=1.0.0
V_SAKAI_MELETE=1.0.2
V_SAKAI_RESOURCES=1.0.1
V_SAKAI_SAMIGO=1.0.0
V_SAKAI_SCHEDULE=1.0.0
V_SAKAI_YAFT=1.0.0
V_UNISA_FAQS=1.0.0
V_UNISA_WELCOME=1.0.0


BASE_URL=http://unipoole.yourdomain.ac.za/unipoole-service
LOCATION=/home/username/workspace/client-base/target
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/client.base-${V_CLIENT_BASE}.zip\"} $BASE_URL/service-code/new/client.base/${V_CLIENT_BASE}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/sakai.announcements-${V_SAKAI_ANNOUNCEMENTS}.zip\"} $BASE_URL/service-code/new/sakai.announcements/${V_SAKAI_ANNOUNCEMENTS}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/sakai.home-${V_SAKAI_HOME}.zip\"} $BASE_URL/service-code/new/sakai.home/${V_SAKAI_HOME}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/sakai.melete-${V_SAKAI_MELETE}.zip\"} $BASE_URL/service-code/new/sakai.melete/${V_SAKAI_MELETE}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/sakai.resources-${V_SAKAI_RESOURCES}.zip\"} $BASE_URL/service-code/new/sakai.resources/${V_SAKAI_RESOURCES}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/sakai.samigo-${V_SAKAI_SAMIGO}.zip\"} $BASE_URL/service-code/new/sakai.samigo/${V_SAKAI_SAMIGO}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/sakai.schedule-${V_SAKAI_SCHEDULE}.zip\"} $BASE_URL/service-code/new/sakai.schedule/${V_SAKAI_SCHEDULE}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/sakai.yaft-${V_SAKAI_YAFT}.zip\"} $BASE_URL/service-code/new/sakai.yaft/${V_SAKAI_YAFT}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/unisa.faqs-${V_UNISA_FAQS}.zip\"} $BASE_URL/service-code/new/unisa.faqs/${V_UNISA_FAQS}
echo "";
curl -X PUT --header Content-Type:"application/json" --data {\"location\":\"$LOCATION/unisa.welcome-${V_UNISA_WELCOME}.zip\"} $BASE_URL/service-code/new/unisa.welcome/${V_UNISA_WELCOME}
echo "";
echo "done"

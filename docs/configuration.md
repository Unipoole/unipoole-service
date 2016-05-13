# Configuration
The Unipoole-Service has very complex configuration through Spring with reasonable defaults.

## application.properties
This file can be found in the deployment in `WEB-INF/config`. It can also be places in the directory specified by the `UNIPOOLE_HOME` environment variable.
This file contains the configuration values most likely to change.


|Property|Set This|Default/Example|Description|
|----------|----------|-----------------|-----------|
|jdbc.driverClassName | oracle.jdbc.OracleDriver | The database driver class. |
|jdbc.url | X | jdbc:oracle:thin:@oracle.opencollab.co.za:1521:uni_dev | The database URL containing the server name/IP, port and database name. |
|jdbc.username | X | Uni-User | The database username. |
|jdbc.password | X | MyPassword | The database password. |
|creator.base.directory | X | /opt/unipoole/store | The base directory that will be used to store client code and content from the LMS. If Unipoole Service is running as a cluster this needs to point to a shared directory. |
|creator.temp.directory | X | /opt/unipoole/temp | A temp directory space. |
|creator.download.url | X | http://unipoole.opencollab.co.za:8081/unipoole-service/service-creator/download/ | The full URL to the download service. Will always end on service-creator/download/ |
|creator.default.user.name |   | Unipoole Creator | Any name to use in mails when clients are created. Note: This will only be used if the user did not provide a name in the Admin Tool. |
|creator.default.user.email | X | sakai@opencollab.co.za | A email address to send mails to when clients are created. Note: This will only be used if the user did not provide a email in the Admin Tool. |
|creator.content.manage.modules | X | true/false | Whether this instance of Unipoole Service will run the Module Management process which creates synch content packages. Only one instance can run this process. |
|task.schedule.default.delay | X | Any number | The default delay before a scheduled task runs for the first time in minutes. |
|task.schedule.default.frequency | X | Any number | The delay between runs for a scheduled tasks in minutes. |
|mail.default.from | X | unipoole@opencollab.co.za | The from address to use in the mail service. |
|mail.default.replyto | X | unipoole@opencollab.co.za | The reply-to address to use in the mail service. |
|mail.template.directory | X | /opt/unipoole/mailtemplates | The directory where the mail templates are stored. |
|sakai.admin.username | X | oc_admin | A Sakai admin user. The user need admin rights on Sakai to retrieve data from any site or user. |
|sakai.admin.password | X | 779@h | The admin user password. |
|sakai.session.timeout | X | 5 | The amount of time in minutes to cache the Sakai sessions on the Unipoole Service side. This helps to have less calls to Sakai. This amount must be less then the Sakai Session timeout. |
|sakai.service.wsdl.base | X | http://unipoole.opencollab.co.za:8080/sakai-axis/ | The Sakai Axis URL. |
|sakai.site.type.master |   | onlcourse | The site type for the master modules. |
|sakai.site.type.group |   | onlgroup | The site type for the group modules. |




## JPA Configuration
Please change the `persistence.xml` file located at `src/main/resources/META-INF/persistence.xml`. Add the following under the properties section (line 16+).
```xml
<property name="hibernate.hbm2ddl.auto" value="none"/>
<property name="hibernate.default_schema" value="SAKAIDBA" />
```

## Other Configuration
The Unipoole-Service use configuration from a file called `application.properties`. There is a configuration file in the deployment (`unipoole-service.war`) with the default properties under `WEB-INF\config\application.properties`. When the Unipoole-Service run it will also look for application.properties in the `UNIPOOLE_HOME` (set in the environmental variables) which will override the default properties.
The following must be set:
```ini
#database details
jdbc.driverClassName=oracle.jdbc.OracleDriver
jdbc.url=jdbc:oracle:thin:@your.oracle.server:1521:yourDB
jdbc.username=${database.username}
jdbc.password=${database.password}
 
#Creator details
creator.base.directory=${temp.path}/unipoole
creator.temp.directory=${temp.path}/unipooleTemp
creator.download.url=${unipoole.url}/service-creator/download/
#defaults to use for client creation mails.
creator.default.user.name=Unipoole Creator
creator.default.user.email=unipoole@yourdomain.ac.za
#Whether the content synch process should run on this node
#NB. Only one node should be true!
creator.content.manage.modules=true
 
#The task service
#The default delay before a scheduled task runs for the first time in minutes.
task.schedule.default.delay=2
#The delay between runs for a scheduled tasks in minutes.
task.schedule.default.frequency=10
 
mail.default.from=unipoole@yourdomain.ac.za
mail.default.replyto=unipoole@yourdomain.ac.za
mail.template.directory=${temp.path}/temp/unipoole/mail-templates
 
#Sakai service details
sakai.admin.username=yourAdminUsername
sakai.admin.password=secrect
#The session timeout in minutes so we can cache sessions, keep this shorter then Sakai.
sakai.session.timeout=5
#The base url for content.
sakai.content.base.url=http://yoursakai.ac.za:8080/access/content
#The Sakai Axis URL.
sakai.service.wsdl.base=http://yoursakai.ac.za:8080/sakai-axis/
#"onlcourse": main module. "onlgroup": group site of the main module.
sakai.site.type.master=onlcourse
sakai.site.type.group=onlgroup
```
## Cache Configuration
There is a cache configuration for clustering using Hazelcast. To activate the clustered cache config update the `WEB-INF/config/unipoole-services.xml` (`src/main/webapp/WEB-INF/config/unipoole-services.xml`) file. Remove the `MapCacheManager` and enable the `HazelcastCacheManager`.
```xml
<!-- Cluster Caching -->
   <!-- Map Cache manager
   <bean class="coza.opencollab.unipoole.service.util.impl.MapCacheManager" />
    -->
   <!-- Hazelcast -->
  <bean class="coza.opencollab.unipoole.service.util.impl.HazelcastCacheManager" />
```

The configuration for the cache is in the file `hazelcast.xml` (`src/main/resources/hazelcast.xml`). See Hazelcast Config on configuration options.
The important part is to either have Multicast enabled (`<multicast enabled="true">`) OR have TCP-IP enabled (`<tcp-ip enabled="true">`) but not both. If TCP-IP is enabled list the members participating in the cache in that section.

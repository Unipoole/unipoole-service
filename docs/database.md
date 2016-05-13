# Database
<img src="http://unipoole.github.io/images/unipoole-service/sync-flow-classes.png" style="max-width: 100%" />


|Table|Description|
|UNI_TOOL_VERSION|Keeps track of which version of a tool is loaded on a device.|
|UNI_DEVICE_REG|Keeps track of devices that are registered to unipoole service (mobile and desktop). A "MASTER" device also exists which is used for Unipoole itself|
|UNI_MODULE_REG|Keeps track of which modules are registered on which devices. A "MASTER" module registration also exists which is used for unipoole itself.|
|UNI_CONTENT_VERSION|Keeps of which version of content is available for module registrations. A "MASTER" version also exist which keeps track of the latest version which is not specific to a user device|
|UNI_CODE_RELEASE|Indicates when versions of code was released.|
|UNI_CODE_RELEASE_VERSION|Indicates which versions of each tool was released when a code release was made|
|UNI_CONTENT_RELEASE|Indicates which versions of content has been released|
|UNI_CONTENT_RELEASE_VERSION|Indicates which versions of content for each tool was released.|
|UNI_MANAGED_MODULE|Modules that unipoole are currently managing (synching)|

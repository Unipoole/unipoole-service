--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--
--  Add unique indexes to only allow one active row in the following tables
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--

CREATE UNIQUE INDEX UNI_CONTENT_VERSION_ACTIVE_IDX ON UNI_CONTENT_VERSION
 (CASE WHEN ACTIVE = '0' THEN NULL ELSE MODULE_REGISTRATION_ID||TOOL_NAME||ACTIVE END);
 
 CREATE UNIQUE INDEX UNI_TOOL_VERSION_ACTIVE_IDX ON UNI_TOOL_VERSION
 (CASE WHEN ACTIVE = '0' THEN NULL ELSE DEVICE_REGISTRATION_ID||TOOL_NAME||ACTIVE END);
 
CREATE UNIQUE INDEX UNI_CODE_RELEASE_ACTIVE_IDX ON UNI_CODE_RELEASE
 (CASE WHEN ACTIVE = '0' THEN NULL ELSE RELEASE_NAME||ACTIVE END);
 
CREATE UNIQUE INDEX UNI_CONTENT_RELEASE_ACTIVE_IDX ON UNI_CONTENT_RELEASE
 (CASE WHEN ACTIVE = '0' THEN NULL ELSE RELEASE_NAME||ACTIVE END);
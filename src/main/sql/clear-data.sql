--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--
--  Clears all synching data in the Unipoole database.
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--

DELETE FROM UNI_CODE_RELEASE_VERSION;
DELETE FROM UNI_CODE_RELEASE;
DELETE FROM UNI_CONTENT_RELEASE_VERSION;
DELETE FROM UNI_CONTENT_RELEASE;
DELETE FROM UNI_CONTENT_VERSION;
DELETE FROM UNI_MODULE_REG WHERE DEVICE_REGISTRATION_ID = '1';
UPDATE UNI_MANAGED_MODULE SET LAST_SYNC = null;
DELETE FROM UNI_TOOL_VERSION;
DELETE FROM UNI_DEVICE_REG;
delete from uni_managed_module;
COMMIT;

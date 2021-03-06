--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--
--  Create statements for all tables and sequences in the Unipoole database.
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX--

--------------------------------------------------------
--  DDL for Table UNI_MANAGED_MODULE
--------------------------------------------------------
CREATE TABLE UNI_MANAGED_MODULE (
    ID NUMBER NOT NULL,
    MODULE_ID VARCHAR2(64) NOT NULL,
    MASTER_MODULE_ID VARCHAR2(64) NULL,
    LAST_SYNC TIMESTAMP(6) NULL,
    ACTIVE SMALLINT NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_MANAGED_MODULE_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_MANAGED_MODULE_U UNIQUE (MODULE_ID)
);
CREATE SEQUENCE UNI_MANAGED_MODULE_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE; 
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_DEVICE_REG
--------------------------------------------------------
CREATE TABLE UNI_DEVICE_REG (
    ID NUMBER NOT NULL,
    USERNAME VARCHAR2(24) NOT NULL,
    DEVICE_ID VARCHAR2(64) NOT NULL,
    ACTIVE SMALLINT NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_DEVICE_REG_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_DEVICE_REG_U UNIQUE (USERNAME, DEVICE_ID)
);
CREATE SEQUENCE UNI_DEVICE_REG_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE; 
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_MODULE_REG
--------------------------------------------------------
CREATE TABLE UNI_MODULE_REG (
    ID NUMBER NOT NULL,
    DEVICE_REGISTRATION_ID NUMBER NOT NULL,
    MODULE_ID VARCHAR2(64) NOT NULL,
    ACTIVE SMALLINT NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_MODULE_REG_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_MODULE_REG_U UNIQUE (DEVICE_REGISTRATION_ID, MODULE_ID),
    CONSTRAINT UNI_MODULE_REG_FK FOREIGN KEY (DEVICE_REGISTRATION_ID) REFERENCES UNI_DEVICE_REG (ID)
);
CREATE SEQUENCE UNI_MODULE_REG_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE; 
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_TOOL_VERSION
--------------------------------------------------------
CREATE TABLE UNI_TOOL_VERSION (
    ID NUMBER NOT NULL,
    DEVICE_REGISTRATION_ID NUMBER NOT NULL,
    TOOL_NAME VARCHAR2(64) NOT NULL,
    TOOL_VERSION VARCHAR2(24) NOT NULL,
    ACTIVE SMALLINT NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_TOOL_VERSION_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_TOOL_VERSION_U UNIQUE (DEVICE_REGISTRATION_ID, TOOL_NAME, TOOL_VERSION),
    CONSTRAINT UNI_TOOL_VERSION_FK FOREIGN KEY (DEVICE_REGISTRATION_ID) REFERENCES UNI_DEVICE_REG (ID)
);
CREATE SEQUENCE UNI_TOOL_VERSION_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE; 
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_CONTENT_VERSION
--------------------------------------------------------
CREATE TABLE UNI_CONTENT_VERSION (
    ID NUMBER NOT NULL,
    MODULE_REGISTRATION_ID NUMBER NOT NULL,
    TOOL_NAME VARCHAR2(64) NOT NULL,
    CONTENT_VERSION VARCHAR2(24) NOT NULL,
    ACTIVE SMALLINT NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_CONTENT_VERSION_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_CONTENT_VERSION_U UNIQUE (MODULE_REGISTRATION_ID, TOOL_NAME, CONTENT_VERSION),
    CONSTRAINT UNI_CONTENT_VERSION_FK FOREIGN KEY (MODULE_REGISTRATION_ID) REFERENCES UNI_MODULE_REG (ID)
);
CREATE SEQUENCE UNI_CONTENT_VERSION_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE;
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_CODE_RELEASE
--------------------------------------------------------
CREATE TABLE UNI_CODE_RELEASE (
    ID NUMBER NOT NULL,
    RELEASE_NAME VARCHAR2(64) NOT NULL,
    RELEASE_VERSION VARCHAR2(64) NOT NULL,
    RELEASED TIMESTAMP(6) NOT NULL,
    ACTIVE SMALLINT NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_CODE_RELEASE_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_CODE_RELEASE_U UNIQUE (RELEASE_NAME, RELEASE_VERSION)
);
CREATE SEQUENCE UNI_CODE_RELEASE_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE;
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_CODE_RELEASE_VERSION
--------------------------------------------------------
CREATE TABLE UNI_CODE_RELEASE_VERSION (
    ID NUMBER NOT NULL,
    CODE_RELEASE_ID NUMBER NOT NULL,
    TOOL_NAME VARCHAR2(64) NOT NULL,
    TOOL_VERSION VARCHAR2(64) NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_CODE_RELEASE_VERSION_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_CODE_RELEASE_VERSION_U UNIQUE (CODE_RELEASE_ID, TOOL_NAME),
    CONSTRAINT UNI_CODE_RELEASE_VERSION_FK FOREIGN KEY (CODE_RELEASE_ID) REFERENCES UNI_CODE_RELEASE (ID)
);
CREATE SEQUENCE UNI_CODE_RELEASE_VERSION_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE;
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_CONTENT_RELEASE
--------------------------------------------------------
CREATE TABLE UNI_CONTENT_RELEASE (
    ID NUMBER NOT NULL,
    MODULE_ID VARCHAR2(64) NOT NULL,
    RELEASE_NAME VARCHAR2(64) NOT NULL,
    RELEASE_VERSION VARCHAR2(64) NOT NULL,
    RELEASED TIMESTAMP(6) NOT NULL,
    ACTIVE SMALLINT NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_CONTENT_RELEASE_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_CONTENT_RELEASE_U UNIQUE (MODULE_ID, RELEASE_NAME, RELEASE_VERSION)
);
CREATE SEQUENCE UNI_CONTENT_RELEASE_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE;
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_CONTENT_RELEASE_VERSION
--------------------------------------------------------
CREATE TABLE UNI_CONTENT_RELEASE_VERSION (
    ID NUMBER NOT NULL,
    CONTENT_RELEASE_ID NUMBER NOT NULL,
    TOOL_NAME VARCHAR2(64) NOT NULL,
    CONTENT_VERSION VARCHAR2(64) NOT NULL,
    LAST_UPDATED TIMESTAMP(6) NOT NULL,
    CONSTRAINT UNI_CONTENT_RELEASE_VERSION_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_CONTENT_RELEASE_VERSION_U UNIQUE (CONTENT_RELEASE_ID, TOOL_NAME),
    CONSTRAINT UNI_CONTENT_RELEASE_VERSION_FK FOREIGN KEY (CONTENT_RELEASE_ID) REFERENCES UNI_CONTENT_RELEASE (ID)
);
CREATE SEQUENCE UNI_CONTENT_REL_VERSION_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE;
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
--------------------------------------------------------
--  DDL for Table UNI_CONTENT_MAPPING
--------------------------------------------------------
CREATE TABLE UNI_CONTENT_MAPPING (
	ID NUMBER NOT NULL,
    SITE_FROM_ID VARCHAR2(36) NOT NULL,
    SITE_TO_ID VARCHAR2(36) NOT NULL,
    TOOL_NAME VARCHAR2(36) NOT NULL,
    TOOL_FROM_ID VARCHAR2(255) NOT NULL,
    TOOL_TO_ID VARCHAR2(255) NOT NULL,
    CONSTRAINT UNI_CONTENT_MAPPING_PK PRIMARY KEY (ID),
    CONSTRAINT UNI_CONTENT_MAPPING_U UNIQUE (SITE_FROM_ID, SITE_TO_ID, TOOL_NAME, TOOL_FROM_ID)
);
CREATE SEQUENCE UNI_CONTENT_MAPPING_SEC INCREMENT BY 1 START WITH 1 NOMAXVALUE;
--XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

COMMIT;
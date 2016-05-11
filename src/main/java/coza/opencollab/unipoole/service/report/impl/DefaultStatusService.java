package coza.opencollab.unipoole.service.report.impl;

import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.dbo.ContentVersion;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.dbo.ModuleRegistration;
import coza.opencollab.unipoole.service.dbo.ToolVersion;
import coza.opencollab.unipoole.service.report.StatusService;
import coza.opencollab.unipoole.service.report.so.Status;
import coza.opencollab.unipoole.service.synch.SynchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The default implementation of the status service.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class DefaultStatusService implements StatusService{
    /**
     * The dao
     */
    @Autowired
    private Dao dao;
    /**
     * The synchronise service
     */
    @Autowired
    private SynchService synchService;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUsernames(){
        return dao.getUsernames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDeviceIds(String username) {
        return dao.getDeviceIds(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getModuleIds(String username, String deviceId) {
        return dao.getModuleIds(username, deviceId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getTools(){
        return dao.getMasterToolNames();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentCodeVersion(String toolName){
        ToolVersion toolVersion = dao.getMasterToolVersion(toolName);
        return toolVersion==null?null:toolVersion.getToolVersion();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getCurrentCodeVersions(){
        List<ToolVersion> toolVersions = dao.getMasterToolVersions();
        Map<String, String> versions = new HashMap<String, String>();
        for(ToolVersion toolVersion: toolVersions){
            versions.put(toolVersion.getToolName(), toolVersion.getToolVersion());
        }
        return versions;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentContentVersion(String moduleId, String toolName){
        ContentVersion contentVersion = dao.getMasterContentVersion(moduleId, toolName);
        return contentVersion==null?null:contentVersion.getContentVersion();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getCurrentContentVersions(String moduleId){
        List<ContentVersion> contentVersions = dao.getMasterContentVersions(moduleId);
        Map<String, String> versions = new HashMap<String, String>();
        for(ContentVersion contentVersion: contentVersions){
            versions.put(contentVersion.getToolName(), contentVersion.getContentVersion());
        }
        return versions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientCodeVersion(String username, String deviceId, String toolName) {
        ToolVersion toolVersion = dao.getToolVersionWithNullCheck(username, deviceId, toolName);
        return toolVersion==null?null:toolVersion.getToolVersion();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getClientCodeVersions(String username, String deviceId){
        List<ToolVersion> toolVersions = dao.getToolVersionsWithNullCheck(username, deviceId);
        Map<String, String> versions = new HashMap<String, String>();
        for(ToolVersion toolVersion: toolVersions){
            versions.put(toolVersion.getToolName(), toolVersion.getToolVersion());
        }
        return versions;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientContentVersion(String username, String deviceId, String moduleId, String toolName){
        DeviceRegistration deviceRegistration = dao.getDeviceRegistration(username, deviceId);
        ModuleRegistration moduleRegistration = dao.getModuleRegistration(deviceRegistration, moduleId);
        ContentVersion contentVersion = dao.getContentVersion(moduleRegistration, toolName);
        return contentVersion==null?null:contentVersion.getContentVersion();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getClientContentVersions(String username, String deviceId, String moduleId){
        List<ContentVersion> contentVersions = dao.getContentVersionsWithNullCheck(username, deviceId, moduleId);
        Map<String, String> versions = new HashMap<String, String>();
        for(ContentVersion contentVersion: contentVersions){
            versions.put(contentVersion.getToolName(), contentVersion.getContentVersion());
        }
        return versions;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Status getStatus(String username, String deviceId, String moduleId, String toolName){
        return synchService.getStatus(username, deviceId, moduleId, toolName);
    }
}

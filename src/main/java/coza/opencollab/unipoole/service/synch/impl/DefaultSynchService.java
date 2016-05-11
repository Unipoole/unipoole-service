package coza.opencollab.unipoole.service.synch.impl;

import static coza.opencollab.unipoole.service.ErrorCodes.CONTENT_VERSION;

import coza.opencollab.unipoole.service.Defaults;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.auth.AuthenticationService;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.auth.so.Login;
import coza.opencollab.unipoole.service.code.CodeService;
import coza.opencollab.unipoole.service.content.ContentService;
import coza.opencollab.unipoole.service.dbo.ContentMapping;
import coza.opencollab.unipoole.service.dbo.ContentVersion;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.dbo.ModuleRegistration;
import coza.opencollab.unipoole.service.dbo.ToolVersion;
import coza.opencollab.unipoole.service.event.EventCodes;
import coza.opencollab.unipoole.service.event.EventService;
import coza.opencollab.unipoole.service.report.StatusService;
import coza.opencollab.unipoole.service.report.so.Status;
import coza.opencollab.unipoole.service.synch.SynchService;
import coza.opencollab.unipoole.service.synch.so.SyncContentMapping;
import coza.opencollab.unipoole.service.synch.so.SynchContent;
import coza.opencollab.unipoole.service.synch.so.SynchStatus;
import coza.opencollab.unipoole.service.synch.so.UpdateContent;
import coza.opencollab.unipoole.service.synch.so.UpdateStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * A default implementation of the synch service.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class DefaultSynchService implements SynchService {

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(DefaultSynchService.class);
    
    /**
     * The auth service
     */
    @Autowired
    private AuthenticationService authenticationService;
    /**
     * The event service.
     */
    @Autowired
    private EventService eventService;
    /**
     * The code service.
     */
    @Autowired
    private CodeService codeService;
    /**
     * The content service.
     */
    @Autowired
    private ContentService contentService;
    /**
     * The status service.
     */
    @Autowired
    private StatusService statusService;
    /**
     * The dao
     */
    @Autowired
    private Dao dao;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SynchContent getUpdate(String username, String deviceId, String toolName, String toolVersion) {
        updateCodeVersion(username, deviceId, toolName, toolVersion);
        SynchContent content = codeService.getToolSynch(toolName, toolVersion);
        eventService.addEventForUser(username, EventCodes.SYNCH_TOOL_REQUEST, deviceId, toolName + "(" + content.getVersion() + ")");
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SynchContent getContent(String username, String password, String deviceId, String moduleId, String toolName, String contentVersion) {
        //must be able to log in
        Login login = authenticationService.login(username, password);
        authenticationService.logout(login);
        if (!login.isSuccessful()) {
            return new SynchContent(login);
        }
        //Just to make sure that the user had this version in their record.
//        updateContentVersion(username, deviceId, moduleId, toolName, contentVersion);
        SynchContent content = contentService.getContentSynch(username, deviceId, moduleId, toolName, contentVersion);
        eventService.addEventForUser(username, EventCodes.SYNCH_CONTENT_REQUEST, deviceId + "-" + toolName + "(" + content.getVersion() + ")", moduleId);
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Status getStatus(String username, String deviceId, String moduleId, String toolName) {
        Status status = new Status();
        DeviceRegistration deviceRegistration = dao.getDeviceRegistration(username, deviceId);
        ModuleRegistration moduleRegistration = dao.getModuleRegistrationWithNullCheck(username, deviceId, moduleId);
        ToolVersion clientToolVersion = dao.getToolVersion(deviceRegistration, toolName);
        if (clientToolVersion == null) {
            status.setClientCodeVersion("Unknown");
            status.setCurrentCodeVersion(dao.getMasterToolVersion(toolName).getToolVersion());
            status.setCodeSynchSize(-1L);
        } else {
            SynchContent codeSynchContent = codeService.getToolSynch(toolName, clientToolVersion.getToolVersion());
            status.setClientCodeVersion(clientToolVersion.getToolVersion());
            status.setCurrentCodeVersion(codeSynchContent.getVersion());
            status.setCodeSynchSize(codeSynchContent.getSize());
        }
        ContentVersion clientContentVersion = dao.getContentVersion(moduleRegistration, toolName);
        if (clientContentVersion == null) {
            status.setClientContentVersion("Unknown");
            status.setCurrentContentVersion(dao.getMasterContentVersion(moduleId, toolName).getContentVersion());
            status.setContentSynchSize(-1L);
        } else {
            SynchContent contentSynchContent = contentService.getContentSynch(username, deviceId, moduleId, toolName, clientContentVersion.getContentVersion());
            status.setClientContentVersion(clientContentVersion.getContentVersion());
            status.setCurrentContentVersion(contentSynchContent.getVersion());
            status.setContentSynchSize(contentSynchContent.getSize());
        }
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SynchStatus getSynchStatus(String username, SynchStatus synchStatus) {
        //check tool versions for the tool sent up
        Map<String, Status> tools = synchStatus.getTools();
        if (tools == null) {
            tools = new HashMap<String, Status>();
        }
        for (Map.Entry<String, Status> entry : tools.entrySet()) {
            Status status = entry.getValue();
//            updateCodeVersion(username, synchStatus.getDeviceId(), entry.getKey(), status.getClientCodeVersion());
//            updateContentVersion(username, synchStatus.getDeviceId(), synchStatus.getModuleId(), entry.getKey(), status.getClientContentVersion());
            SynchContent codeSynchContent = codeService.getToolSynch(entry.getKey(), status.getClientCodeVersion());
            status.setCurrentCodeVersion(codeSynchContent.getVersion());
            SynchContent contentSynchContent = contentService.getContentSynch(username, synchStatus.getDeviceId(), synchStatus.getModuleId(), entry.getKey(), status.getClientContentVersion());
            status.setCurrentContentVersion(contentSynchContent.getVersion());
            status.setCodeSynchSize(codeSynchContent.getSize());
            status.setContentSynchSize(contentSynchContent.getSize());
        }
//        //now check if we are missing tools. 
        DeviceRegistration deviceRegistration = dao.getMasterDeviceRegistration();
        List<String> toolNames = dao.getToolNames(deviceRegistration); // Get the tools names
        for (String toolName : toolNames) {
            if (!tools.keySet().contains(toolName)) {
                Status status = new Status();
                status.setClientCodeVersion(synchStatus.getTools().get(toolName).getClientCodeVersion());
                status.setClientContentVersion(synchStatus.getTools().get(toolName).getClientContentVersion());
                SynchContent codeSynchContent = codeService.getToolSynch(toolName, status.getClientCodeVersion());
                status.setCurrentCodeVersion(codeSynchContent.getVersion());
                SynchContent contentSynchContent = contentService.getContentSynch(username, synchStatus.getDeviceId(), synchStatus.getModuleId(), toolName, status.getClientContentVersion());
                status.setCurrentContentVersion(contentSynchContent.getVersion());
                status.setCodeSynchSize(codeSynchContent.getSize());
                status.setContentSynchSize(contentSynchContent.getSize());
                synchStatus.addToolStatus(toolName, status);
            }
        }
        return synchStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UpdateStatus updateCodeVersion(String username, String deviceId, String toolName, String toolVersion) {
        UpdateStatus updateStatus = new UpdateStatus(ServiceCallStatus.SUCCESS);
        DeviceRegistration deviceRegistration = dao.getDeviceRegistration(username, deviceId);
        ToolVersion tv = dao.getToolVersion(deviceRegistration, toolName, toolVersion);
        if (tv == null) {
            dao.createToolVersion(deviceRegistration, toolName, toolVersion);
        } else  if(!tv.isActive()){
            dao.deactivateAllToolVersions(deviceRegistration.getId(), toolName);
            tv.setLastUpdated();
            tv.setActive(true);
            dao.update(tv);
        }
        return updateStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UpdateStatus updateContentVersion(String username, String deviceId, String moduleId, String toolName, String contentVersion) {
        UpdateStatus updateStatus = new UpdateStatus(ServiceCallStatus.SUCCESS);
        if (StringUtils.isEmpty(contentVersion)) {
            updateStatus.setStatus(ServiceCallStatus.ERROR);
            updateStatus.setErrorCode(CONTENT_VERSION);
            updateStatus.setMessage("The content version is empty.");
            return updateStatus;
        }
        ModuleRegistration moduleRegistration = dao.getModuleRegistrationWithNullCheck(username, deviceId, moduleId);
        ContentVersion cv = dao.getContentVersion(moduleRegistration, toolName, contentVersion);
        if (cv == null) {
            dao.createContentVersion(moduleRegistration, toolName, contentVersion);
        } else if(!cv.isActive()){
            dao.deactivateAllContentVersions(moduleRegistration.getId(), toolName);
            cv.setLastUpdated();
            cv.setActive(true);
            dao.update(cv);
        }
        return updateStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateContent updateContent(String username, String password, String deviceId, String moduleId, String toolName, Map<String, ?> content, String originalContent) {
        //must be able to log in
        Login login = authenticationService.login(username, password);
        authenticationService.logout(login);
        if (!login.isSuccessful()) {
            return new UpdateContent(login);
        }
        return contentService.updateUserContent(username, deviceId, moduleId, toolName, content, originalContent);
    }

	@Override
	public SyncContentMapping getContentMapping(String toSiteId, String toolId) {
		List<ContentMapping> contentMappingList = dao.getContentMapping(toSiteId, toolId);
		SyncContentMapping scm = new SyncContentMapping();
		
		if(contentMappingList != null){
			Map<String, String> mappings = new HashMap<String, String>();
			String fromId;
			for(ContentMapping cm : contentMappingList){
				fromId = cm.getToolFromId();
				
				// Log a warning if we found a duplicate
				if (mappings.containsKey(fromId)){
					LOG.warn("Duplicate id found for a from tool mapping. toSite=" + toSiteId + " ,toolId="+toolId+", fromId="+fromId);
				}
				mappings.put(fromId, cm.getToolToId());
			}
			scm.setMappings(mappings);
		}
		
		return scm;
	}
}

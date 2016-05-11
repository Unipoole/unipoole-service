package coza.opencollab.unipoole.service.code.impl;

import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.dbo.ToolVersion;
import coza.opencollab.unipoole.service.code.CodeService;
import coza.opencollab.unipoole.service.code.CodeLoader;
import coza.opencollab.unipoole.service.creator.StorageService;
import coza.opencollab.unipoole.service.util.StorageFileHandler;
import coza.opencollab.unipoole.service.creator.so.CreatorResponse;
import coza.opencollab.unipoole.service.synch.so.SynchContent;
import coza.opencollab.unipoole.service.util.VersionComparer;
import coza.opencollab.unipoole.util.JsonParser;
import java.io.File;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * The code service for client code.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class DefaultCodeService implements CodeService{
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultCodeService.class);
    /**
     * The dao
     */
    @Autowired
    private Dao dao;
    /**
     * The Storage service.
     */
    @Autowired
    private StorageService storageService;
    /**
     * The version comparer for the code.
     */
    private VersionComparer versionComparer;
    /**
     * The code loader to use.
     */
    private CodeLoader codeLoader;

    /**
     * Set the Dao.
     */
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    /**
     * The Storage service.
     */
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * The version comparer for the code.
     */
    public void setVersionComparer(VersionComparer versionComparer) {
        this.versionComparer = versionComparer;
    }
    
    /**
     * The code loader to use.
     */
    public void setCodeLoader(CodeLoader codeLoader) {
        this.codeLoader = codeLoader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CreatorResponse loadTool(String toolName, String toolVersion, String location) {
        CreatorResponse response = new CreatorResponse(ServiceCallStatus.SUCCESS);
        if(StringUtils.isEmpty(toolName) || StringUtils.isEmpty(toolVersion) || StringUtils.isEmpty(location)){
            response.setStatus(ServiceCallStatus.ERROR);
            response.setErrorCode(INVALID_VALUE);
            response.setMessage("Invalid values. [toolName=" + toolName + ":toolVersion=" + toolVersion + ":location=" + location + "]");
            return response;
        }
        if(!versionComparer.isValidVersion(toolVersion)){
            response.setStatus(ServiceCallStatus.ERROR);
            response.setErrorCode(INVALID_VALUE);
            response.setMessage("The version (" + toolVersion + ") is not valid.\nIt must be in the format '" + versionComparer.getVersionPattern() + "'.");
            return response;
        }
        File source = new File(location);
        if(!isValidLocation(toolName, toolVersion, source)){
            response.setStatus(ServiceCallStatus.ERROR);
            response.setErrorCode(INVALID_LOCATION);
            response.setMessage("The location (" + location + ") is not valid.\nIt must be a directory or zip file containing the tool code in the root.");
            return response;
        }

        //check if the version is greater then the existing one.
        DeviceRegistration masterDeviceRegistration = dao.getMasterDeviceRegistration();
        ToolVersion currentToolVersion = dao.getToolVersion(masterDeviceRegistration, toolName);
        if (currentToolVersion != null && (versionComparer.compare(currentToolVersion.getToolVersion(), toolVersion) > -1)) {
            response.setStatus(ServiceCallStatus.ERROR);
            response.setErrorCode(INVALID_VERSION);
            response.setMessage("The tool version (" + toolVersion + ") is not greater then the previous version (" + currentToolVersion.getToolVersion() + ").");
            return response;
        }
        codeLoader.load(toolName, toolVersion, source);
        response.setDescription("New version will be loaded and availible soon: " +toolName + "-" + toolVersion);
        return response;
    }

    /**
     * Test whether the location is a valid location and the contents is 
     * the tool and version specified.
     * 
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @param source The source location of the tool code.
     */
    private boolean isValidLocation(String toolName, String toolVersion, File source) {
        String aboutContents;
        if(!source.exists()){
            return false;
        }
        StorageFileHandler fileHandler = storageService.getFileHandler(source);
        if(fileHandler == null){
            throw new UnipooleException(FILE_MANIPULATION, "No file handler for the source (" + source.getName() + ").");
        }
        try{
            aboutContents = fileHandler.getFileContents(source, "data/about.json", "unipoole/data/about.json");
        }catch(Exception e){
            LOG.warn("Could not find the about.json file in the source (" + source.getAbsolutePath() + ").", e);
            return false;
        }
        Map<String, String> about = (Map<String, String>) JsonParser.parseJsonToMap(aboutContents);
        return toolName.equals(about.get("name")) && toolVersion.equals(about.get("version"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SynchContent getToolSynch(String toolName, String fromToolVersion) {
        DeviceRegistration masterDeviceRegistration = dao.getMasterDeviceRegistration();
        ToolVersion currentToolVersion = dao.getToolVersion(masterDeviceRegistration, toolName);
        ToolVersion clientToolVersion = dao.getToolVersion(masterDeviceRegistration, toolName, fromToolVersion);
        SynchContent content = new SynchContent(ServiceCallStatus.ERROR);
        if(currentToolVersion == null){
            content.setVersion("Unknown");
            content.setSize(0L);
            content.setMessage("The current tool version is not set.");
            return content;
        }
        content.setVersion(currentToolVersion.getToolVersion());
        if (clientToolVersion == null){
            content.setMessage("The tool version (" + fromToolVersion + ") does not exist.");
            if(codeLoader.silentlyUpdateNonExistingVersions()){
                LOG.debug("Changing from version to " + codeLoader.getNonExistingVersion() + " since version " + fromToolVersion + " does not exist.");
                fromToolVersion = codeLoader.getNonExistingVersion();
            }else{
                content.setErrorCode(TOOL_VERSION);
                return content;
            }
        }
        int compared = versionComparer.compare(currentToolVersion.getToolVersion(), fromToolVersion);
        if(compared == 0){
            content.setStatus(ServiceCallStatus.SUCCESS);
            content.setSize(0L);
            return content;
        }else if(compared < 0){
            content.setErrorCode(INVALID_VERSION);
            content.setMessage("The tool version (" + fromToolVersion + ") is not less then the previous version (" + currentToolVersion.getToolVersion() + ").");
            return content;
        }
        return codeLoader.getSynchContent(toolName, fromToolVersion, currentToolVersion.getToolVersion());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getToolSynchSize(String toolName, String fromToolVersion){
        return getToolSynch(toolName, fromToolVersion).getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getNewCodeReleaseFile() {
        return codeLoader.createCodeRelease();
    }
}

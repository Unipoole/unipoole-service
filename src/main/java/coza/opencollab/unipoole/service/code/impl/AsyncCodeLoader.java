package coza.opencollab.unipoole.service.code.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.Defaults;
import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.code.CodeLoader;
import coza.opencollab.unipoole.service.creator.impl.AbstractLoader;
import coza.opencollab.unipoole.service.dbo.CodeRelease;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.dbo.ToolVersion;
import coza.opencollab.unipoole.service.synch.so.SynchContent;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.StorageFileReader;
import coza.opencollab.unipoole.service.util.StorageFileWriter;
import coza.opencollab.unipoole.util.JsonParser;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

/**
 * This is a asynchronous implementation of the {@link CodeLoader}.
 * <p>
 * This class will;
 * - copy the source code to the local code location
 * - register the new version
 * - deactivate the old
 * - work out the synchronize data
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class AsyncCodeLoader extends AbstractLoader implements CodeLoader{

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey(){
        return "code";
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void load(String toolName, String toolVersion, File source) {
        LOG.info("Loading Asynch Runner (toolName:" + toolName + ", toolVersion:" + toolVersion + ", source:" + source.getAbsolutePath() + ").");
        taskService.scheduleTask(new ToolLoaderRunner(toolName, toolVersion, source));
    }

    /**
     * Do the work.
     * Note: Public for the transactional annotation
     */
    @Transactional
    public void loadTool(String toolName, String toolVersion, File source) {
        //copy it to the code storage location
        File destination = copy(toolName, toolVersion, source);
        //register it in the database
        DeviceRegistration masterDeviceRegistration = dao.getMasterDeviceRegistration();
        ToolVersion currentToolVersion = dao.getToolVersion(masterDeviceRegistration, toolName);
        ToolVersion newToolVersion = dao.createToolVersion(masterDeviceRegistration, toolName, toolVersion);
        if(currentToolVersion == null){
            LOG.info("There is no current version, so this is a new tool, not just a new version.");
            currentToolVersion = dao.createToolVersion(masterDeviceRegistration, toolName, getNonExistingVersion());
        }
        //record what is new for synchronisation
        compare(currentToolVersion, newToolVersion, destination);
        dao.deactivateAllOther(newToolVersion);
        LOG.info("Done with Runner (toolName:" + toolName + ", toolVersion:" + toolVersion + ").");
    }

    /**
     * Copy the given file to the local code location
     */
    private File copy(String toolName, String toolVersion, File source) {
        LOG.info("Start to copy source (" + source.getAbsolutePath() + ") to destination.");
        File destination = storageService.getStorageFile(getKey(), getName(toolName, toolVersion));
        try{
            storageService.copyToStorage(source, destination);
            FileSystemUtils.deleteRecursively(source);
            LOG.info("Done coping source to destination (" + destination.getAbsolutePath() + ").");
            return destination;
        } catch (UnipooleException e) {
            FileSystemUtils.deleteRecursively(destination);
            LOG.error("Could not copy the tool code from the location (" + source.getAbsolutePath() + ").", e);
            throw e;
        }
    }

    /**
     * Compare the two code versions and create a synch package.
     */
    private void compare(ToolVersion currentToolVersion, ToolVersion newToolVersion, File newCode) {
        LOG.info("Start to compare current version to version " + newToolVersion.getToolVersion() + ".");
        StorageFileReader newFileReader = storageService.getStorageFileReader(newCode);
        List<StorageEntry> entries;
        
        File currentCode = storageService.getStorageFile(getKey(), getName(currentToolVersion.getToolName(), currentToolVersion.getToolVersion()));
        if(currentCode.exists()){
            StorageFileReader currentFileReader = null;
            try{
                currentFileReader = storageService.getStorageFileReader(currentCode);
                entries = entryComparer.diff(currentFileReader.getEntries(), newFileReader.getEntries());
            }finally{
                if(currentFileReader != null){
                    currentFileReader.close();
                }
            }
        }else{
            entries = newFileReader.getEntries();
        }
        
        //write the diff entries to a new synch spot
        File synchFile = storageService.getStorageFile(getKey(), getName(newToolVersion.getToolName(), currentToolVersion.getToolVersion(), newToolVersion.getToolVersion()));
        StorageFileWriter fileWriter = storageService.getStorageFileWriter(synchFile);
        try{
            fileWriter.write(entries);
            LOG.info("Done comparing version " + currentToolVersion.getToolVersion() + " to " + newToolVersion.getToolVersion() + ".");
            LOG.info("Result of compare in " + synchFile.getAbsolutePath());
        }finally{
            //cleanup
            newFileReader.close();
            fileWriter.close();
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    @Transactional
    public SynchContent getSynchContent(String toolName, String fromToolVersion, String toToolVersion) {
        LOG.info("Retrieving synch content for toolname=" + toolName + "(" + fromToolVersion + " to " + toToolVersion + ").");
        DeviceRegistration masterDeviceRegistration = dao.getMasterDeviceRegistration();
        ToolVersion fromTV = dao.getToolVersion(masterDeviceRegistration, toolName, fromToolVersion);
        ToolVersion toTV = dao.getToolVersion(masterDeviceRegistration, toolName, toToolVersion);
        SynchContent content = new SynchContent(ServiceCallStatus.SUCCESS);
        File synchFile = null;
        try {
            synchFile = createSyncFile(fromTV, toTV);
        } catch (IOException e) {
            FileSystemUtils.deleteRecursively(synchFile);
            LOG.error("Cannot create the synch content.", e);
            content.setStatus(ServiceCallStatus.EXCEPTION);
            content.setErrorCode(FILE_MANIPULATION);
            content.setMessage("Cannot create the synch content.");
            return content;
        }
        content.setContentName(synchFile.getName());
        content.setMimeType(storageService.getFileHandler(synchFile).getMimeType());
        try {
            content.setContent(FileCopyUtils.copyToByteArray(synchFile));
            content.setSize(synchFile.length());
        } catch (IOException e) {
            LOG.error("Cannot read the synch content from file (" + synchFile.getAbsolutePath() + ").", e);
            content.setStatus(ServiceCallStatus.EXCEPTION);
            content.setErrorCode(FILE_MANIPULATION);
            content.addMessage("Cannot read the synch content.");
        }
        content.setVersion(toToolVersion);
        return content;
    }
    
    /**
     * Create the file containing the synch data between the versions.
     */
    private File createSyncFile(ToolVersion fromToolVersion, ToolVersion toToolVersion) throws IOException{
        String toolName = fromToolVersion.getToolName();
        String fromVersion = fromToolVersion.getToolVersion();
        String toVersion = toToolVersion.getToolVersion();
        File synchFile = storageService.getStorageFile(getKey(), getName(toolName, fromVersion, toVersion));
        File tempDir = storageService.getTempDirectory(getKey(), getName(toolName, fromVersion, toVersion));
        try{
            acquireLock(toolName, fromVersion, toVersion);
            if(synchFile.exists()){
                return synchFile;
            }
            LOG.info("Building synch content for toolname=" + toolName + "(" + fromVersion + " > " + toVersion + ").");
            DeviceRegistration deviceRegistration = dao.getMasterDeviceRegistration();
            ToolVersion previousToolVersion = fromToolVersion;
            List<ToolVersion> toolVersions = dao.getToolVersions(deviceRegistration, toolName, fromVersion, toVersion);
            for(ToolVersion toolVersion: toolVersions){
                File thisSynchFile = storageService.getStorageFile(getKey(), getName(toolName, previousToolVersion.getToolVersion(), toolVersion.getToolVersion()));
                storageService.copyToDirectory(thisSynchFile, tempDir);
                previousToolVersion = toolVersion;
            }
            checkDeleted(tempDir);
            storageService.copyToStorage(tempDir, synchFile);
            return synchFile;
        }finally{
            releaseLock(toolName, fromVersion, toVersion);
            FileSystemUtils.deleteRecursively(tempDir);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public File createCodeRelease() {
        List<ToolVersion> toolVersions = dao.getMasterToolVersions();
        if(toolVersions == null || toolVersions.isEmpty()){
            throw new UnipooleException(TOOL_VERSION, "There are no Client Tools loaded!");
        }
        CodeRelease codeRelease = dao.getCodeRelease();
        if(codeRelease == null || hasNewCode(codeRelease.getReleased(), toolVersions)){
            dao.deactivate(codeRelease);
            codeRelease = dao.createCodeRelease(getReleaseName(), getNewVersion());
        }
        try{
            return createNewCodeRelease(codeRelease, toolVersions);
        } catch (IOException e) {
            LOG.error("Cannot create the code release.", e);
            throw new UnipooleException(FILE_MANIPULATION, "Cannot create the code release.", e);
        }
    }

    /**
     * Check if there is new code version created after the code release.
     */
    private boolean hasNewCode(Timestamp released, List<ToolVersion> toolVersions) {
        for (ToolVersion toolVersion : toolVersions) {
            if (released.before(toolVersion.getLastUpdated())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create the synch file containing all the tools.
     * Note: Public for the transactional annotation
     */
    @Transactional
    public File createNewCodeRelease(CodeRelease codeRelease, List<ToolVersion> toolVersions) throws IOException {
        File releaseFile = storageService.getStorageFile(getKey(), getName(codeRelease.getReleaseName(), codeRelease.getReleaseVersion()));
        File tempDir = storageService.getTempDirectory(getKey(), getName(codeRelease.getReleaseName(), codeRelease.getReleaseVersion()));
        File tempToolDir = new File(tempDir, "tools");
        File writeTo;
        try{
            acquireLock(codeRelease.getReleaseName());
            if(releaseFile.exists()){
                return releaseFile;
            }
            Map toolDescriptions = new HashMap();
            Map toolsLocal = new HashMap();
            Map tools = new HashMap();
            for (ToolVersion toolVersion : toolVersions) {
                Map toolsLocalVersion = new HashMap();
                Map toolsVersion = new HashMap();
                File toolFile = storageService.getStorageFile(getKey(), getName(toolVersion.getToolName(), toolVersion.getToolVersion()));
                if(Defaults.DEFAULT_CLIENT_BASE_NAME.equals(toolVersion.getToolName())){
                    writeTo = tempDir;
                }else{
                    writeTo = new File(tempToolDir, toolVersion.getToolName());
                }
                storageService.copyToDirectory(toolFile, writeTo);
                String aboutContents;
                if(Defaults.DEFAULT_CLIENT_BASE_NAME.equals(toolVersion.getToolName())){
                    aboutContents = storageService.getFileContents(toolFile, "unipoole/data/about.json");
                }else{
                    aboutContents = storageService.getFileContents(toolFile, "data/about.json");
                }
                Map aboutTool = JsonParser.parseJsonToMap(aboutContents);
                toolDescriptions.put(toolVersion.getToolName(), aboutTool.get("toolDescription"));
                toolsLocalVersion.put("clientCodeVersion", toolVersion.getToolVersion());
                toolsLocalVersion.put("clientContentVersion", "0");
                toolsLocal.put(toolVersion.getToolName(), toolsLocalVersion);
                toolsVersion.put("clientCodeVersion", toolVersion.getToolVersion());
                toolsVersion.put("currentCodeVersion", toolVersion.getToolVersion());
                toolsVersion.put("codeSynchSize", 0);
                toolsVersion.put("clientContentVersion", "0");
                tools.put(toolVersion.getToolName(), toolsVersion);
                dao.createCodeReleaseVersion(codeRelease, toolVersion.getToolName(), toolVersion.getToolVersion());
            }
            //write meta data
            Map unipooleData = new HashMap();
            unipooleData.put("toolDescriptions", toolDescriptions);
            unipooleData.put("toolsLocal", toolsLocal);
            unipooleData.put("tools", tools);
            unipooleData.put("deviceId", "1");
            File unipooleDataFile = new File(tempDir, "unipoole/data/unipooleData.json");
            if(unipooleDataFile.createNewFile()){
                FileCopyUtils.copy(JsonParser.writeJsonBytes(unipooleData), unipooleDataFile);
            }
            Map<String, String> meta = new HashMap<String, String>();
            meta.put("version", codeRelease.getReleaseVersion());
            meta.put("name", codeRelease.getReleaseName());
            File aboutFile = new File(tempDir, "data/about.json");
            if(aboutFile.getParentFile().mkdir() && aboutFile.createNewFile()){
                FileCopyUtils.copy(JsonParser.writeJsonBytes(meta), aboutFile);
            }
            storageService.copyToStorage(tempDir, releaseFile);
            return releaseFile;
        }finally{
            releaseLock(codeRelease.getReleaseName());
            FileSystemUtils.deleteRecursively(tempDir);
        }
    }
    
    /**
     * A class that is used to run the actual execution.
     */
    private class ToolLoaderRunner implements Runnable{
        /**
         * The tool name.
         */
        private final String toolName;
        /**
         * The tool version.
         */
        private final String toolVersion;
        /**
         * The tool source location.
         */
        private final File source;
        
        /**
         * Set all Constructor.
         */
        public ToolLoaderRunner(String toolName, String toolVersion, File source){
            this.toolName = toolName;
            this.toolVersion = toolVersion;
            this.source = source;
        }
        
        /**
         * Calls the <code>loadTool</code> method to do the work.
         */
        @Override
        public void run() {
            try{
                loadTool(toolName, toolVersion, source);
            }catch(Exception e){
                LOG.error("A unexpected exception occured.", e);
            }
        }
    }
}

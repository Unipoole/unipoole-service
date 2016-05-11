package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.Defaults;
import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.content.ContentConverter;
import coza.opencollab.unipoole.service.content.ContentLoader;
import coza.opencollab.unipoole.service.creator.impl.AbstractLoader;
import coza.opencollab.unipoole.service.dbo.ContentRelease;
import coza.opencollab.unipoole.service.dbo.ContentVersion;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.dbo.ManagedModule;
import coza.opencollab.unipoole.service.dbo.ModuleRegistration;
import coza.opencollab.unipoole.service.lms.LMSClient;
import coza.opencollab.unipoole.service.synch.so.SynchContent;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.StorageFileReader;
import coza.opencollab.unipoole.service.util.StorageFileWriter;
import coza.opencollab.unipoole.service.util.StorageMemoryWriter;
import coza.opencollab.unipoole.shared.Module;
import coza.opencollab.unipoole.shared.Tool;
import coza.opencollab.unipoole.util.JsonParser;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

/**
 * This is a asynchronous implementation of the {@link ContentLoader}.
 * <p>
 * This class will; - check the lms from time to time for new data - retrieve
 * the content and register the new version - deactivate the old - work out the
 * synchronize data
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class AsyncContentLoader extends AbstractLoader implements ContentLoader {

    //TODO: Remove before going live, just for testing
    private int Counter_1 = 0;
    
    /**
     * A list of tools that does not exist in the lms that also need to be
     * added.
     */
    private List<Tool> nonLMSTools = new ArrayList<Tool>();
    /**
     * Whether this loader should start the scheduled jobs
     */
    private boolean scheduleManagedModules = false;
    /**
     * The lms client
     */
    @Autowired
    private LMSClient lmsClient;
    /**
     * The content converters for all the tools.
     */
    Map<String, ContentConverter> contentConvertors;
    
    /**
     * With the change to the Master site name structure we need to do string 
     * comparison operations to find the master sites also to find group sites 
     * we need to remove the master suffix ( "-Master" ) from the moduleIds
     */
    private static String masterSuffix;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return "content";
    }

    /**
     * A list of tools that does not exist in the lms that also need to be
     * added.
     */
    public void setNonLMSTools(List<Tool> nonLMSTools) {
        this.nonLMSTools = nonLMSTools;
    }

    /**
     * Whether this loader should start the scheduled jobs
     */
    public void setScheduleManagedModules(boolean scheduleManagedModules) {
        this.scheduleManagedModules = scheduleManagedModules;
    }

    /**
     * The content converters for all the tools.
     */
    public void setContentConvertors(Map<String, ContentConverter> contentConvertors) {
        this.contentConvertors = contentConvertors;
    }

    /**
     * The content converter for the tool name.
     */
    private ContentConverter getContentConverter(String toolName) {
        return contentConvertors.get(toolName);
    }

    /**
     * initiates the loader
     */
    @PostConstruct
    public void init() {
        LOG.info("Initializing...");
        if (scheduleManagedModules) {
            LOG.info("Starting Module schedules...");
            List<ManagedModule> managedModules = dao.getManagedModules();
            for (ManagedModule managedModule : managedModules) {
                taskService.scheduleTaskAtFixedRate(managedModule.getModuleId(), new ContentLoaderRunner(managedModule.getModuleId()));
            }
        }
        LOG.info("Initialized.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void load(String moduleId) {
        ManagedModule managedModule = dao.getManagedModule(moduleId);
        if (managedModule == null) {
            dao.createManagedModule(moduleId);
        } else if (!managedModule.isActive()) {
            managedModule.setActive(true);
            dao.update(managedModule);
        }
        if (!taskService.isTaskScheduled(moduleId)) {
            LOG.info("Loading runner for module (" + moduleId + ").");
            taskService.scheduleTaskAtFixedRate(moduleId, new ContentLoaderRunner(moduleId));
        }
    }

    /**
     * Do the work. Note: Public for the transactional annotation
     */
    @Transactional
    public void loadContent(String moduleId) {
        LOG.info(Counter_1 + ". Start loading content for module (" + moduleId + ")." + new Date());
        ManagedModule managedModule = dao.getManagedModule(moduleId);
        if (!managedModule.isActive()) {
            LOG.info("Canceling module (" + moduleId + ").");
            taskService.cancelTask(managedModule.getModuleId());
            return;
        }
        //check if this is a master site, then add the groups sites
        boolean isMaster = true;
        int newGroupModules = 0;
        List<Module> modules = null;
        if(moduleId.contains(masterSuffix)){
            modules = lmsClient.getGroupModules(moduleId.replace(masterSuffix, ""));
        }else{
            modules = lmsClient.getGroupModules(moduleId);
        }
        for (Module module : modules) {
            ManagedModule mm = dao.getManagedModule(module.getId());
            if (mm == null) {
                dao.createManagedModule(module.getId(), moduleId);
                load(module.getId());
                newGroupModules++;
            } else if (module.getId().equals(moduleId)) {
                //this is a groupsite
                isMaster = false;
            } else if (mm.getMasterModuleId() == null) {
                //update the master module id
                mm.setMasterModuleId(moduleId);
                dao.update(mm);
            }
        }
        if (isMaster) {
            LOG.info("This is a master site (" + moduleId + ").");
            if (newGroupModules > 0 && managedModule.getLastSync() != null) {
                LOG.info("Just loaded  " + newGroupModules + " new group module(s), so will sit out and let them run first.");
                return;
            }
        } else {
            LOG.info("This is a group site (" + moduleId + ").");
            String masterModuleId = managedModule.getMasterModuleId();
            if (masterModuleId == null || dao.getManagedModule(masterModuleId).getLastSync() == null) {
                LOG.info("The master module has not loaded yet, so will sit out and let it run first.");
                return;
            }
        }

        List<Tool> tools = lmsClient.getTools(moduleId);
        tools.addAll(nonLMSTools);
        DeviceRegistration deviceRegistration = dao.getMasterDeviceRegistration();
        ModuleRegistration moduleRegistration = dao.getModuleRegistration(deviceRegistration, moduleId);
        if (moduleRegistration == null) {
            LOG.info("Create new module registration (" + moduleId + ").");
            moduleRegistration = dao.createModuleRegistration(deviceRegistration, moduleId);
            if (!isMaster) {
                copyMasterContentToGroup(managedModule, deviceRegistration, moduleRegistration, tools);
            }
        }
        for (Tool tool : tools) {
            LOG.info("Loading module (" + moduleId + ") tool (" + tool.getName() + ").");
            ContentVersion oldContentVersion;
            List<StorageEntry> entries;
            String newVersion;
            try {
                oldContentVersion = dao.getContentVersion(moduleRegistration, tool.getName());
                Date fromDate;
                if (oldContentVersion == null) {
                    fromDate = null;
                } else {
                    fromDate = getVersionDate(oldContentVersion.getContentVersion());
                }
                Object data = lmsClient.getToolContent(moduleId, tool.getName(), fromDate);
                if (data == null) {
                    LOG.debug("No data for module (" + moduleId + ") tool (" + tool.getName() + ").");
                    continue;
                }
                newVersion = getNewVersion();
                ContentConverter contentConverter = getContentConverter(tool.getName());
                if (contentConverter == null) {
                    LOG.warn("No converter for tool (" + tool.getName() + ").");
                    continue;
                }
                entries = contentConverter.convert(moduleId, data);
                if (entries == null || entries.isEmpty()) {
                    LOG.debug("No data for module (" + moduleId + ") tool (" + tool.getName() + ").");
                    continue;
                }
            } catch (UnipooleException e) {
                LOG.warn("Could not get the data for module (" + moduleId + ") tool (" + tool.getName() + ").", e);
                continue;
            }
            createStorageFile(entries, moduleId, tool.getName(), newVersion);
            ContentVersion newContentVersion = dao.createContentVersion(moduleRegistration, tool.getName(), newVersion);
            dao.deactivateAllOther(newContentVersion);
            LOG.info( Counter_1 +". End of for loop " + new Date());
        }
        managedModule.setLastSync();
        LOG.info("Commiting content for module (" + moduleId + ").");
        dao.update(managedModule);
        LOG.info( Counter_1 + ". End of Function "+ new Date());
        Counter_1++;
    }

    /**
     * Copy the master data to the group for new groups.
     */
    @Transactional
    public void copyMasterContentToGroup(ManagedModule managedModule, DeviceRegistration deviceRegistration, ModuleRegistration moduleRegistration, List<Tool> tools) {
        LOG.info("Copying the master module (" + managedModule.getMasterModuleId() + ") content to the module (" + managedModule.getModuleId() + ").");
        ModuleRegistration masterModuleRegistration = dao.getModuleRegistration(deviceRegistration, managedModule.getMasterModuleId());
        if (masterModuleRegistration == null) {
            LOG.info("No master module!");
            return;
        }
        for (Tool tool : tools) {
            LOG.info("Copying tool " + tool.getName());
            List<ContentVersion> masterContentVersions = dao.getContentVersions(masterModuleRegistration, tool.getName());
            if (masterContentVersions == null || masterContentVersions.isEmpty()) {
                LOG.debug("No content for tool " + tool.getName());
                continue;
            }
            for (int i = 0; i < masterContentVersions.size(); i++) {
                ContentVersion masterContentVersion = masterContentVersions.get(i);
                dao.deactivateAllContentVersions(moduleRegistration.getId(), masterContentVersion.getToolName());
                copyFromMasterToGroup(masterModuleRegistration, masterContentVersion, moduleRegistration);
            }
        }
    }

    /**
     * Write the entries to the storage.
     */
    private File createStorageFile(List<StorageEntry> entries, String... values) {
        LOG.debug("Writing storage for (" + Arrays.toString(values) + ").");
        File storageDestination = storageService.getStorageFile(getKey(), getName(values));
        StorageFileWriter storage = null;
        try {
            storage = storageService.getStorageFileWriter(storageDestination);
            storage.write(entries);
            return storageDestination;
        } catch (UnipooleException e) {
            FileSystemUtils.deleteRecursively(storageDestination);
            throw e;
        } finally {
            if (storage != null) {
                storage.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SynchContent getSynchContent(String moduleId, String toolName, String fromContentVersion, String toContentVersion) {
        LOG.debug("Retrieving synch content for toolname=" + toolName + "(" + fromContentVersion + " to " + toContentVersion + ").");
        DeviceRegistration masterDeviceRegistration = dao.getMasterDeviceRegistration();
        ModuleRegistration moduleRegistration = dao.getModuleRegistration(masterDeviceRegistration, moduleId);
        File synchFile;
        SynchContent content = new SynchContent(ServiceCallStatus.SUCCESS);
        try {
            acquireLock(moduleId, toolName, fromContentVersion, toContentVersion);
            synchFile = createSynchFile(moduleRegistration, toolName, fromContentVersion, toContentVersion);
        } finally {
            releaseLock(moduleId, toolName, fromContentVersion, toContentVersion);
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
            content.addMessage("Cannot read the content synch content.");
        }
        content.setVersion(toContentVersion);
        return content;
    }

    /**
     * Compare the versions and write the entries to the synch.
     */
    @Transactional
    public File createSynchFile(ModuleRegistration moduleRegistration, String toolName, String fromVersion, String toVersion) {
        File synchDestination = storageService.getStorageFile(getKey(), getName(moduleRegistration.getModuleId(), toolName, fromVersion, toVersion));
        if (synchDestination.exists()) {
            //this is already done
            return synchDestination;
        }
        LOG.debug("Writing synch for module (" + moduleRegistration.getModuleId() + ") tool (" + toolName + ") version " + fromVersion + " to version " + toVersion + ".");
        List<ContentVersion> contentVersions = dao.getContentVersions(moduleRegistration, toolName, fromVersion, toVersion);
        List<StorageEntry> entries = null;
        StorageFileWriter synch = storageService.getStorageFileWriter(synchDestination);
        StorageFileReader previousReader = null;
        StorageFileReader reader = null;
        try {
            for (ContentVersion contentVersion : contentVersions) {
                try {
                    File file = storageService.getStorageFile(getKey(), getName(moduleRegistration.getModuleId(), toolName, contentVersion.getContentVersion()));
                    reader = storageService.getStorageFileReader(file);
                    if (entries != null && !entries.isEmpty()) {
                        entries = entryComparer.merge(entries, reader.getEntries());
                    } else {
                        entries = reader.getEntries();
                    }
                } finally {
                    //closing previous reader. The current reader could still be used
                    //in the next for loop when the content of the entries are
                    //read.
                    if (previousReader != null) {
                        previousReader.close();
                    }
                    previousReader = reader;
                }
            }
            synch.write(entries);
            LOG.debug("Done comparing version " + fromVersion + " to " + toVersion + ".");
            LOG.debug("Result of compare in " + synchDestination.getAbsolutePath());
            return synchDestination;
        } finally {
            if (previousReader != null) {
                previousReader.close();
            }
            if (synch != null) {
                synch.close();
            }
        }
    }

    /**
     * Copy a master module content version (and content) to the group module.
     *
     * @param moduleRegistration The group module registration.
     * @param masterContentVersion The master module content version that must
     * be copied.
     * @param moduleRegistration The group module registration.
     * @return The new group content version.
     */
    @Transactional
    public ContentVersion copyFromMasterToGroup(ModuleRegistration masterModuleRegistration, ContentVersion masterContentVersion, ModuleRegistration moduleRegistration) {
        List<Module> modules = lmsClient.getGroupModules(moduleRegistration.getModuleId());
        File source = storageService.getStorageFile(getKey(), getName(masterModuleRegistration.getModuleId(), masterContentVersion.getToolName(), masterContentVersion.getContentVersion()));
        File destination = storageService.getStorageFile(getKey(), getName(moduleRegistration.getModuleId(), masterContentVersion.getToolName(), modules.get(0).getCreatedDate()));
        try {
            FileCopyUtils.copy(source, destination);
        } catch (IOException e) {
            throw new UnipooleException(FILE_MANIPULATION, "Could not copy the master content.", e);
        }
        return dao.createContentVersion(moduleRegistration, masterContentVersion.getToolName(), modules.get(0).getCreatedDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SynchContent getUserContent(String username, String moduleId, String toolName, String fromContentVersion) {
        String newVersion = getNewVersion();
        Object data = lmsClient.getUserContent(username, moduleId, toolName, getVersionDate(fromContentVersion));
        if (data == null) {
            LOG.debug("No data for user (" + username + "), module (" + moduleId + ") tool (" + toolName + ").");
            return null;
        }
        ContentConverter contentConverter = getContentConverter(toolName);
        if (contentConverter == null) {
            LOG.warn("No converter for tool (" + toolName + ").");
            return null;
        }
        List<StorageEntry> entries = contentConverter.convertUserData(moduleId, data);
        if (entries == null || entries.isEmpty()) {
            LOG.debug("No data for user (" + username + "), module (" + moduleId + ") tool (" + toolName + ").");
            return null;
        }
        StorageMemoryWriter writer = storageService.getStorageMemoryWriter();
        writer.write(entries);
        SynchContent content = new SynchContent(ServiceCallStatus.SUCCESS);
        content.setContentName(getName(username, moduleId, toolName, newVersion));
        content.setVersion(newVersion);
        content.setMimeType(writer.getMimeType());
        byte[] memData = writer.getContent();
        content.setSize(memData.length);
        content.setContent(memData);
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ?> updateUserContent(String username, String moduleId, String toolName, Map<String, ?> content, String originalContent) {
        return lmsClient.updateUserContent(username, moduleId, toolName, content, originalContent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SynchContent merge(SynchContent contentA, SynchContent contentB) {
        StorageFileReader readerA = null;
        StorageFileReader readerB = null;
        StorageFileWriter writer = null;
        try {
            File tempLocation = storageService.getTempDirectory(getKey(), "merge");
            File tempSynch = File.createTempFile(contentB.getContentName(), "synch", tempLocation);
            File tempA = File.createTempFile(contentA.getVersion(), "A", tempLocation);
            File tempB = File.createTempFile(contentB.getVersion(), "B", tempLocation);
            FileCopyUtils.copy(contentA.getContent(), tempA);
            FileCopyUtils.copy(contentB.getContent(), tempB);
            readerA = storageService.getStorageFileReader(tempA);
            readerB = storageService.getStorageFileReader(tempB);
            List<StorageEntry> entries = entryComparer.merge(readerA.getEntries(), readerB.getEntries());
            writer = storageService.getStorageFileWriter(tempSynch);
            writer.write(entries);
            FileSystemUtils.deleteRecursively(tempA);
            FileSystemUtils.deleteRecursively(tempB);
            SynchContent content = new SynchContent(ServiceCallStatus.SUCCESS);
            content.setContentName(tempSynch.getName());
            content.setMimeType(storageService.getFileHandler(tempSynch).getMimeType());
            content.setSize(tempSynch.length());
            content.setVersion(contentA.getVersion());
            content.setContent(FileCopyUtils.copyToByteArray(tempSynch));
            FileSystemUtils.deleteRecursively(tempSynch);
            return content;
        } catch (IOException e) {
            LOG.error("Could not merge the contents (" + contentA + "," + contentB + ")", e);
            throw new UnipooleException(FILE_MANIPULATION, "Could not merge the contents.", e);
        } finally {
            if (readerA != null) {
                readerA.close();
            }
            if (readerB != null) {
                readerB.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public File createContentRelease(String moduleId, List<String> toolNames, File codeReleaseFile) {
        List<ContentVersion> contentVersions = dao.getContentVersions(dao.getMasterModuleRegistration(moduleId));
        ContentRelease contentRelease = dao.getContentRelease(moduleId);
        if (contentRelease == null || hasNewContent(contentRelease.getReleased(), contentVersions)) {
            dao.deactivate(contentRelease);
            contentRelease = dao.createContentRelease(moduleId, getReleaseName(), getNewVersion());
        }
        try {
            return createNewContentRelease(codeReleaseFile, contentRelease, contentVersions);
        } catch (IOException e) {
            LOG.error("Cannot create the content release.", e);
            throw new UnipooleException(FILE_MANIPULATION, "Cannot create the content release.", e);
        }
    }

    /**
     * Check if there is new content version created after the content release.
     */
    @Transactional
    public boolean hasNewContent(Timestamp released, List<ContentVersion> contentVersions) {
        for (ContentVersion contentVersion : contentVersions) {
            if (released.before(contentVersion.getLastUpdated())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create the synch file containing all the client code and content. Note:
     * Public for the transactional annotation
     */
    @Transactional
    public File createNewContentRelease(File codeReleaseFile, ContentRelease contentRelease, List<ContentVersion> contentVersions) throws IOException {
        File releaseFile = storageService.getStorageFile(getKey(), getName(contentRelease.getModuleId(), contentRelease.getReleaseName(), contentRelease.getReleaseVersion()));
        File tempDir = storageService.getTempDirectory(getKey(), getName(contentRelease.getModuleId(), contentRelease.getReleaseName(), contentRelease.getReleaseVersion()));
        File tempToolDir = new File(tempDir, "tools");
        File writeTo;
        try {
            acquireLock(contentRelease.getModuleId(), contentRelease.getReleaseName());
            if (releaseFile.exists()) {
                return releaseFile;
            }
            storageService.copyToDirectory(codeReleaseFile, tempDir);
            String uniData = new String(FileCopyUtils.copyToByteArray(new File(tempDir, "unipoole/data/unipooleData.json")), Defaults.UTF8);
            Map unipooleData = JsonParser.parseJsonToMap(uniData);
            unipooleData.put("moduleId", contentRelease.getModuleId());
            Map toolDescriptions = (Map) unipooleData.get("toolDescriptions");
            if (toolDescriptions == null) {
                toolDescriptions = new HashMap();
                unipooleData.put("toolDescriptions", toolDescriptions);
            }
            Map toolsLocal = (Map) unipooleData.get("toolsLocal");
            if (toolsLocal == null) {
                toolsLocal = new HashMap();
                unipooleData.put("toolsLocal", toolsLocal);
            }
            Map tools = (Map) unipooleData.get("tools");
            if (tools == null) {
                tools = new HashMap();
                unipooleData.put("tools", tools);
            }
            for (ContentVersion contentVersion : contentVersions) {
                Map toolsLocalVersion = (Map) toolsLocal.get(contentVersion.getToolName());
                if (toolsLocalVersion == null) {
                    toolsLocalVersion = new HashMap();
                    toolsLocal.put(contentVersion.getToolName(), toolsLocalVersion);
                }
                toolsLocalVersion.put("clientContentVersion", contentVersion.getContentVersion());
                Map toolsVersion = (Map) tools.get(contentVersion.getToolName());
                if (toolsVersion == null) {
                    toolsVersion = new HashMap();
                    tools.put(contentVersion.getToolName(), toolsVersion);
                }
                toolsVersion.put("clientContentVersion", contentVersion.getContentVersion());
                toolsVersion.put("currentContentVersion", contentVersion.getContentVersion());
                toolsVersion.put("contentSynchSize", 0);
                File toolFile = storageService.getStorageFile(getKey(), getName(contentRelease.getModuleId(), contentVersion.getToolName(), contentVersion.getContentVersion()));
                if (Defaults.DEFAULT_CLIENT_BASE_NAME.equals(contentVersion.getToolName())) {
                    writeTo = tempDir;
                } else {
                    writeTo = new File(tempToolDir, contentVersion.getToolName());
                }
                storageService.copyToDirectory(toolFile, writeTo);
                //get any extra content related to the 
                ContentConverter contentConverter = getContentConverter(contentVersion.getToolName());
                if (contentConverter.hasExtraResources(contentRelease.getModuleId())) {
                    Map<String, File> files = contentConverter.getExtraResources(contentRelease.getModuleId());
                    File writeData = new File(writeTo, "data");
                    for (Map.Entry<String, File> entry : files.entrySet()) {
                        String dir = entry.getKey();
                        File file = entry.getValue();
                        dir = dir.substring(0, dir.length() - (file.getName().length() + 1));
                        File writeDestination = new File(writeData, dir);
                        storageService.copyToDirectory(file, writeDestination);
                    }
                }
                dao.createContentReleaseVersion(contentRelease, contentVersion.getToolName(), contentVersion.getContentVersion());
            }
            updateMenu(contentRelease.getModuleId(), toolDescriptions);
            //write unidata
            FileCopyUtils.copy(JsonParser.writeJson(unipooleData).getBytes(Defaults.UTF8), new File(tempDir, "unipoole/data/unipooleData.json"));
            //write meta data
            String about = new String(FileCopyUtils.copyToByteArray(new File(tempDir, "data/about.json")), Defaults.UTF8);
            Map<String, String> meta = (Map<String, String>) JsonParser.parseJsonToMap(about);
            meta.put("content", contentRelease.getReleaseVersion());
            meta.put("name", contentRelease.getReleaseName());
            FileCopyUtils.copy(JsonParser.writeJson(meta).getBytes(Defaults.UTF8), new File(tempDir, "data/about.json"));
            storageService.copyToStorage(tempDir, releaseFile);
            return releaseFile;
        } finally {
            FileSystemUtils.deleteRecursively(tempDir);
            releaseLock(contentRelease.getModuleId(), contentRelease.getReleaseName());
        }
    }

    /**
     * Update the menu flags for active tools in the module.
     */
    private void updateMenu(String moduleId, Map toolDescriptions) {
        List<Tool> tools = lmsClient.getTools(moduleId);
        for (Tool tool : tools) {
            Map toolDesc = (Map) toolDescriptions.get(tool.getName());
            if (toolDesc == null) {
                toolDesc = new HashMap();
                toolDescriptions.put(tool.getName(), toolDesc);
            }
            toolDesc.put("menu", !Defaults.DEFAULT_CLIENT_BASE_NAME.equals(tool.getName())
                    && !Defaults.WELCOME_TOOL_NAME.equals(tool.getName()));
        }
    }

    /**
     * A runnable to load content.
     */
    private class ContentLoaderRunner implements Runnable {

        /**
         * The module id.
         */
        private final String moduleId;

        /**
         * Constructor setting the module id.
         */
        ContentLoaderRunner(String moduleId) {
            this.moduleId = moduleId;
        }

        @Override
        public void run() {
            try {
                loadContent(moduleId);
            } catch (Exception e) {
                LOG.error("A unexpected exception occured.", e);
            }
        }
    }

    public static String getMasterSuffix() {
        return masterSuffix;
    }

    public static void setMasterSuffix(String masterSuffix) {
        AsyncContentLoader.masterSuffix = masterSuffix;
    }    
}

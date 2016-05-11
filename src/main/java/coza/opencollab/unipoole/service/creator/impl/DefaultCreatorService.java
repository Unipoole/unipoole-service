package coza.opencollab.unipoole.service.creator.impl;

import coza.opencollab.unipoole.UnipooleException;
import static coza.opencollab.unipoole.service.ErrorCodes.FILE_MANIPULATION;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.code.CodeService;
import coza.opencollab.unipoole.service.content.ContentService;
import coza.opencollab.unipoole.service.creator.CreatorService;
import coza.opencollab.unipoole.service.creator.StorageService;
import coza.opencollab.unipoole.service.creator.so.CreatorClient;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.dbo.ManagedModule;
import coza.opencollab.unipoole.service.event.EventCodes;
import coza.opencollab.unipoole.service.event.EventService;
import coza.opencollab.unipoole.service.lms.LMSClient;
import coza.opencollab.unipoole.service.mail.MailService;
import coza.opencollab.unipoole.service.task.TaskService;
import coza.opencollab.unipoole.service.util.impl.ZipFileHandler;
import coza.opencollab.unipoole.shared.Module;
import coza.opencollab.unipoole.shared.Tool;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * The default implementation of the creator service.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class DefaultCreatorService implements CreatorService {
    /**
     * The task service.
     */
    @Autowired
    private TaskService taskService;
    /**
     * The lms client, injected.
     */
    @Autowired
    private LMSClient lmsClient;
    /**
     * The event service injected.
     */
    @Autowired
    private EventService eventService;
    /**
     * The content service.
     */
    @Autowired
    private ContentService contentService;
    /**
     * The code service.
     */
    @Autowired
    private CodeService codeService;
    /**
     * The Storage service.
     */
    @Autowired
    private StorageService storageService;
    /**
     * The mail service.
     */
    @Autowired
    private MailService mailService;
    /**
     * The dao.
     */
    @Autowired
    private Dao dao;
    /**
     * The url to use for downloads.
     */
    private String downloadUrl;
    /**
     * The default properties for the creation.
     */
    private Map<String, String> defaultProperties;

    /**
     * The url to use for downloads.
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * The default properties for the creation.
     */
    public void setDefaultProperties(Map<String, String> defaultProperties) {
        this.defaultProperties = defaultProperties;
    }
    
    /**
     * Update the properties to include the default properties. Also remove all non-String properties.
     */
    private Map<String, String> updateProperties(Map<String, ?> properties){
        Map<String, String> props = new HashMap<String, String>(defaultProperties);
        if (properties != null) {
            for (Map.Entry<String, ?> entry : properties.entrySet()) {
                if (String.class.isInstance(entry.getValue())) {
                    props.put(entry.getKey(), (String) entry.getValue());
                }
            }
        }
        return props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> getModules(String year, String semester, String moduleCode) {
        List<Module> modules = lmsClient.getModules(year, semester, moduleCode);
        eventService.addEvent(EventCodes.CREATOR_RETRIEVE_MODULES, year + ":" + semester + ":" + moduleCode);
        return modules;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tool> getTools(String moduleId) {
        List<Tool> tools = lmsClient.getTools(moduleId);
        eventService.addEvent(EventCodes.CREATOR_RETRIEVE_TOOLS, null, moduleId);
        return tools;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreatorClient createClient(Map<String, String> properties) {
        File clientReleaseFile = codeService.getNewCodeReleaseFile();
        CreatorClient result = new CreatorClient(ServiceCallStatus.SUCCESS);
        result.setName(clientReleaseFile.getName());
        result.setDownloadKey(storageService.getDownloadKey(clientReleaseFile));
        StringBuilder description = new StringBuilder("The Client Release was created and can be downloaded from the given URL");
        properties = updateProperties(properties);
        String destination = properties.get("destination");
        if(!StringUtils.isEmpty(destination)){
            File dest = new File(destination, clientReleaseFile.getName());
            try {
                FileCopyUtils.copy(clientReleaseFile, dest);
                description.append(" or retrieved from ");
                description.append(destination);
            } catch (IOException e) {
                Logger.getLogger(DefaultCreatorService.class).warn("Could not write the client.", e);
            }
        }
        description.append(".");
        result.setDescription(description.toString());
        sendMail(result, true, false, null, null, properties);
        return setURL(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreatorClient createClient(String moduleId, Map<String, String> properties) {
        return createClient(moduleId, dao.getMasterToolNames(), properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreatorClient createClient(String moduleId, List<String> toolNames, Map<String, String> properties) {
        properties = updateProperties(properties);
        File clientReleaseFile = codeService.getNewCodeReleaseFile();
        //first check if content for this module is availible.
        ManagedModule managedModule = dao.getManagedModule(moduleId);
        CreatorClient result = new CreatorClient(ServiceCallStatus.SUCCESS);
        if(managedModule == null || managedModule.getLastSync() == null){
            //cannot create a release now, will have to do later.
            if(managedModule == null){
                contentService.loadContent(moduleId);
            }
            result.setName(null);
            result.setDescription("The Client Release will be created and a mail sent to " + properties.get("email") + " with the download URL.");
            result.setDownloadKey(null);
            if(!properties.containsKey("CreateClientTask")){
                CreateClientTask task = new CreateClientTask(moduleId, toolNames, properties);
                task.future = taskService.scheduleTaskAtFixedRate(CreateClientTask.class.getSimpleName() + "-" + moduleId, task, 5L);
            }
        }else{
            File clientModuleReleaseFile = contentService.getNewContentReleaseFile(moduleId, toolNames, clientReleaseFile);
            result.setName(clientModuleReleaseFile.getName());
            StringBuilder description = new StringBuilder("The Client Release is created and can be downloaded from the given URL");
            String destination = properties.get("destination");
            if (!StringUtils.isEmpty(destination)) {
                File dest = new File(destination, clientModuleReleaseFile.getName());
                try {
                    FileCopyUtils.copy(clientModuleReleaseFile, dest);
                    description.append(" or retrieved from ");
                    description.append(destination);
                } catch (IOException e) {
                    Logger.getLogger(DefaultCreatorService.class).warn("Could not write the client.", e);
                }
            }
            description.append(".");
            result.setDescription(description.toString());
            result.setDownloadKey(storageService.getDownloadKey(clientModuleReleaseFile));
            result = setURL(result);
            sendMail(result, true, true, moduleId, toolNames, properties);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreatorClient createClient(String username, String password, Map<String, String> properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreatorClient createClient(String username, String password, String moduleId, Map<String, String> properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public File getDownloadFile(String downloadKey){
        return getDownloadFile(downloadKey, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public File getDownloadFile(String downloadKey, boolean compressed){
        return storageService.getDownloadFile(downloadKey, compressed);
    }
    
    /**
     * Sets the URL for the download.
     */
    private CreatorClient setURL(CreatorClient creatorClient){
        if(StringUtils.isEmpty(creatorClient.getDownloadKey())){
            return creatorClient;
        }
        StringBuilder buf = new StringBuilder(downloadUrl);
        buf.append(creatorClient.getDownloadKey());
        creatorClient.setUrl(buf.toString());
        return creatorClient;
    }

    /**
     * Send a mail to the user about the newly created client.
     */
    private void sendMail(CreatorClient result, boolean containCode, boolean containContent, String moduleId, List<String> toolNames, Map<String, String> properties) {
        mailService.sendMail(properties.get("email"), "createClient", new Object[]{
                result.getName(),
                properties.get("name"), 
                result.getDescription(),
                result.getUrl(),
                containCode,
                containContent,
                moduleId,
                toolNames});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientData(String module) {
        File latestReleaseFile = contentService.getLatestContentReleaseFile(module);
            ZipFileHandler zipHandler = new ZipFileHandler();
        try {
            return zipHandler.getFileContents(latestReleaseFile, "unipoole/data/unipooleData.json");
        } catch (IOException e) {
            throw new UnipooleException(FILE_MANIPULATION, "Could not retrieve the contents of the latest release zip file.", e);
        }
    }
    
    /**
     * A runnable to check when a client is created.
     */
    class CreateClientTask implements Runnable{
        private String moduleId;
        private List<String> toolNames;
        private Map<String, String> properties;
        private ScheduledFuture future;
        
        /**
         * Set all constructor.
         */
        public CreateClientTask(String moduleId, List<String> toolNames, Map<String, String> properties){
            this.moduleId = moduleId;
            this.toolNames = toolNames;
            this.properties = properties;
            properties.put("CreateClientTask", "running");
        }
        
        @Override
        public void run() {
            CreatorClient result = createClient(moduleId, toolNames, properties);
            if(result.getDownloadKey() != null){
                future.cancel(false);
            }
        }
    }
}

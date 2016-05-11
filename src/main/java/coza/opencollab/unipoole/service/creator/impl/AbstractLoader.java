package coza.opencollab.unipoole.service.creator.impl;

import coza.opencollab.unipoole.service.creator.StorageService;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.task.TaskService;
import coza.opencollab.unipoole.service.util.CacheManager;
import coza.opencollab.unipoole.service.util.StorageEntryComparer;
import coza.opencollab.unipoole.service.util.impl.DeletedFileEntry;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * A abstract implementation to share some standard code.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public abstract class AbstractLoader {
    /**
     * The logger
     */
    protected final Logger LOG = Logger.getLogger(getClass());
    /**
     * The task service.
     */
    @Autowired
    protected TaskService taskService;
    /**
     * The dao.
     */
    @Autowired
    protected Dao dao;
    /**
     * The storage service.
     */
    @Autowired
    protected StorageService storageService;
    /**
     * The cache manager.
     */
    @Autowired
    private CacheManager cacheManager;
    /**
     * Whether to update non existing version to the current version
     * without throwing an exception.
     */
    private boolean silentlyUpdateNonExistingVersions = false;
    /**
     * The default non existing version.
     */
    private String nonExistingVersion = "0";
    /**
     * The entry comparer.
     */
    protected StorageEntryComparer entryComparer;
    /**
     * The version date format.
     */
    private String versionDateFormat = "yyyyMMddHHmmssSSS";
    /**
     * The content release name.
     */
    private String releaseName = "UnipooleClient";

    /**
     * A key used for storage.
     */
    public abstract String getKey();

    /**
     * The task service.
     */
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
    
    /**
     * The dao
     */
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    /**
     * The storage service
     */
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Whether to update non existing version to the current version
     * without throwing an exception.
     */
    public void setSilentlyUpdateNonExistingVersions(boolean silentlyUpdateNonExistingVersions) {
        this.silentlyUpdateNonExistingVersions = silentlyUpdateNonExistingVersions;
    }

    /**
     * Whether to update non existing version to the current version
     * without throwing an exception.
     */
    public boolean silentlyUpdateNonExistingVersions() {
        return silentlyUpdateNonExistingVersions;
    }

    /**
     * The default non existing version.
     */
    public String getNonExistingVersion() {
        return nonExistingVersion;
    }

    /**
     * The default non existing version.
     */
    public void setNonExistingVersion(String nonExistingVersion) {
        this.nonExistingVersion = nonExistingVersion;
    }

    /**
     * The entry comparer.
     */
    public void setEntryComparer(StorageEntryComparer entryComparer) {
        this.entryComparer = entryComparer;
    }

    /**
     * The version date format.
     */
    public void setVersionDateFormat(String versionDateFormat) {
        this.versionDateFormat = versionDateFormat;
    }

    /**
     * The code/content release name.
     */
    public String getReleaseName() {
        return releaseName;
    }

    /**
     * The code/content release name.
     */
    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }
    
    /**
     * Acquire a lock using a cache.
     * @param key Any values that make a good key.
     */
    protected void acquireLock(String... key){
        cacheManager.lock(key);
    }
    
    /**
     * Release a lock using a cache.
     * @param key The same values used to acquire the lock.
     */
    protected void releaseLock(String... key){
        cacheManager.unlock(key);
    }

    /**
     * Build a absolute file name for the values provided.
     * Values can be username, tool name, module, version...
     *
     * @param values The values to use in the name.
     * @return A file name.
     */
    protected String getName(String... values) {
        if(values == null || values.length == 0){
            //let it break
            return null;
        }
        StringBuilder buff = new StringBuilder(values[0]);
        for(int i = 1; i < values.length; i++){
            buff.append("-");
            buff.append(values[i]);
        }
        return buff.toString();
    }
    
    /**
     * Cleanup deleted markers that is not necessary anymore.
     */
    protected void checkDeleted(File directory){
        File[] deletedFiles = directory.listFiles(DeletedFileEntry.getFileFilter());
        for(File deletedFile: deletedFiles){
            File realFile = DeletedFileEntry.getRealFile(deletedFile);
            if(realFile.exists() && realFile.lastModified() > deletedFile.lastModified()){
                deletedFile.delete();
            }
        }
    }
    
    /**
     * Gets a new date based version.
     */
    protected String getNewVersion() {
        return new SimpleDateFormat(versionDateFormat).format(new Date());
    }
    
    /**
     * Get the date from the version.
     */
    protected Date getVersionDate(String version){
        if(StringUtils.isEmpty(version)){
            return null;
        }
        try {
            return new SimpleDateFormat(versionDateFormat).parse(version);
        } catch (ParseException e) {
            LOG.debug("Invalid version: " + version, e);
            return null;
        }
    }
}

package coza.opencollab.unipoole.service.creator.impl;

import coza.opencollab.unipoole.UnipooleException;
import static coza.opencollab.unipoole.service.ErrorCodes.FILE_MANIPULATION;
import coza.opencollab.unipoole.service.creator.StorageService;
import coza.opencollab.unipoole.service.task.TaskService;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.StorageFileHandler;
import coza.opencollab.unipoole.service.util.StorageFileReader;
import coza.opencollab.unipoole.service.util.StorageFileWriter;
import coza.opencollab.unipoole.service.util.StorageMemoryWriter;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

/**
 * The implementation of the storage service.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class DefaultStorageService implements StorageService {
    /**
     * A random for temp directories.
     */
    private static final Random random = new Random(System.currentTimeMillis());
    /**
     * The path separator used.
     */
    private static final String PATH_SEPARATOR = "/";
    /**
     * A indicator that the file name in the download key is the real filename.
     */
    private static final String DOWNLOAD_SUFFIX = "--download";
    /**
     * A indicator that the file name in the download key is the real filename.
     */
    private static final String DOWNLOAD_REALNAME = "--real=true";
    /**
     * The key to use internally for temp locations.
     */
    private static final String TEMP_KEY = ".dss";
    /**
     * The prefix used for temp files and directories.
     */
    private static final String TEMP_PREFIX = ".temp";
    /**
     * The suffix used for temp files and directories.
     */
    private static final String TEMP_SUFFIX = "~";
    /**
     * The path to use for a collection of entries.
     */
    private static final String ENTRY_COLLECTION = "collection";
    /**
     * The task service.
     */
    @Autowired
    private TaskService taskService;
    /**
     * The base directory for the storage.
     */
    private File baseDirectory;
    /**
     * The base temp directory for the storage.
     */
    private File tempDirectory;
    /**
     * The storage file handler.
     */
    private StorageFileHandler storageFileHandler;
    /**
     * A collections of FileHandler's.
     */
    private List<StorageFileHandler> fileHandlers;

    /**
     * The task service.
     */
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * The base directory for the storage.
     */
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
        validateDirectory(baseDirectory);
    }

    /**
     * The base temp directory for the storage.
     */
    public void setTempDirectory(File tempDirectory) {
        this.tempDirectory = tempDirectory;
        validateDirectory(tempDirectory);
    }

    /**
     * The storage file handler.
     */
    public void setStorageFileHandler(StorageFileHandler storageFileHandler) {
        this.storageFileHandler = storageFileHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageFileHandler getFileHandler(File file) {
        for (StorageFileHandler fileHandler : fileHandlers) {
            if (fileHandler.canHandle(file)) {
                return fileHandler;
            }
        }
        return null;
    }

    /**
     * A collections of FileHandler's.
     */
    public void setFileHandlers(List<StorageFileHandler> fileHandlers) {
        this.fileHandlers = fileHandlers;
    }
    
    @PostConstruct
    public void init(){
        taskService.scheduleTaskAtFixedRate("TempCleanup", new TempCleanupTask(tempDirectory));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StorageFileReader getStorageFileReader(File file){
        if(file == null || !file.exists()){
            return null;
        }
        StorageFileHandler fileHandler = getFileHandler(file);
        if(fileHandler == null){
            return null;
        }
        return fileHandler.getStorageFileReader(file);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StorageFileWriter getStorageFileWriter(File file){
        return storageFileHandler.getStorageFileWriter(file);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StorageMemoryWriter getStorageMemoryWriter(){
        return storageFileHandler.getMemoryWriter();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileContents(File root, String fileName) throws UnipooleException{
        StorageFileHandler fileHandler = getFileHandler(root);
        if(fileHandler == null){
            return null;
        }
        try {
            return fileHandler.getFileContents(root, fileName);
        } catch (IOException e) {
            throw new UnipooleException(FILE_MANIPULATION, "Could not retrieve the contents.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getBaseStorageDirectory() {
        return baseDirectory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getStorageDirectory(String key) {
        File f = new File(baseDirectory, key);
        validateDirectory(f);
        return f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTempDirectory(String key) {
        File f = new File(tempDirectory, key);
        validateDirectory(f);
        return f;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public File createTempDirectory(String key){
        File f = getTempDirectory(key);
        do{
            f = new File(f, TEMP_PREFIX + random.nextLong() + TEMP_SUFFIX);
            f.deleteOnExit();
        }while(f.exists());
        f.mkdirs();
        return f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDirectoryPath(String... directories) {
        if (directories == null || directories.length == 0) {
            return "";
        }
        if(directories[0].startsWith(PATH_SEPARATOR)){
            directories[0] = directories[0].substring(1);
        }
        StringBuilder buf = new StringBuilder(directories[0]);
        for (int i = 1; i < directories.length; i++) {
            if(directories[i].startsWith(PATH_SEPARATOR)){
                directories[i] = directories[i].substring(1);
            }
            boolean hasSeparator = directories[i - 1].endsWith(PATH_SEPARATOR);
            if (hasSeparator && directories[i].startsWith(PATH_SEPARATOR)) {
                buf.append(directories[i].substring(1));
            } else if (hasSeparator) {
                buf.append(directories[i]);
            } else {
                buf.append(PATH_SEPARATOR);
                buf.append(directories[i]);
            }
        }
        if (PATH_SEPARATOR.charAt(0) == buf.charAt(buf.length() - 1)) {
            buf.setLength(buf.length() - 1);
        }
        return buf.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilePath(String fileName, String... directories) {
        StringBuilder buf = new StringBuilder(getDirectoryPath(directories));
        buf.append(PATH_SEPARATOR);
        fileName = fileName.replace(PATH_SEPARATOR, "");
        buf.append(fileName);
        return buf.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getStorageDirectory(String key, String directory) {
        File f = new File(getStorageDirectory(key), getDirectoryPath(directory));
        validateDirectory(f);
        return f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTempDirectory(String key, String directory) {
        File f = new File(getTempDirectory(key), getDirectoryPath(String.valueOf(System.currentTimeMillis()), directory));
        validateDirectory(f);
        return f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getStorageFile(String key, String fileName) {
        return new File(getStorageDirectory(key), storageFileHandler.getDestinationName(fileName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getStorageFile(String key, String directory, String fileName) {
        return new File(getStorageDirectory(key, directory), storageFileHandler.getDestinationName(fileName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String store(String key, String directory, String fileName, byte[] data) {
        return store(key, new ByteArrayEntry(fileName, directory, data));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String store(String key, StorageEntry entry) {
        if (entry == null || entry.isDirectory()) {
            return null;
        }
        File f = getStorageFile(key, entry.getRelativeDirectory(), entry.getName());
        String downloadKey = getDownloadKey(key, entry.getRelativeDirectory(), entry.getName());
        //loose the directory
        entry = new ByteArrayEntry(entry.getName(), null, entry.getContents());
        StorageFileWriter writer = storageFileHandler.getStorageFileWriter(f);
        try {
            writer.write(entry);
        } finally {
            writer.close();
        }
        return downloadKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> store(String key, List<StorageEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        List<String> downloadKeys = new ArrayList<String>();
        for (StorageEntry entry : entries) {
            downloadKeys.add(store(key, entry));
        }
        return downloadKeys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String store(String key, String name, List<StorageEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        File f = getStorageFile(key, ENTRY_COLLECTION, name);
        StorageFileWriter writer = storageFileHandler.getStorageFileWriter(f);
        try {
            writer.write(entries);
        } finally {
            writer.close();
        }
        return getDownloadKey(key, ENTRY_COLLECTION, name);
    }

    /**
     * Retrieve a key that can be used to download the file later.
     *
     * @return
     */
    private String getDownloadKey(String key, String directory, String fileName) {
        return encodeDownloadKey(getFilePath(fileName, key, directory)) + DOWNLOAD_SUFFIX;
    }
    
    /**
     * Encodes the key
     */
    private String encodeDownloadKey(String downloadKey){
        return downloadKey.replace("\\", "/").replace("/", "-.-").replace(" ", "_._");
    }
    
    /**
     * Decodes the key
     */
    private String decodeDownloadKey(String downloadKey){
        return downloadKey.replace("-.-", "/").replace("_._", " ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDownloadKey(File file) {
        if(file.getAbsolutePath().startsWith(getBaseStorageDirectory().getAbsolutePath())){
            return encodeDownloadKey(file.getAbsolutePath().substring(getBaseStorageDirectory().getAbsolutePath().length()+1) + DOWNLOAD_REALNAME + DOWNLOAD_SUFFIX);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDownloadFile(String downloadKey, boolean compressed) throws UnipooleException{
        if(downloadKey.endsWith(DOWNLOAD_SUFFIX)){
            downloadKey = downloadKey.substring(0, downloadKey.length() - DOWNLOAD_SUFFIX.length());
        }
        if(downloadKey.endsWith(DOWNLOAD_REALNAME)){
            downloadKey = downloadKey.substring(0, downloadKey.length() - DOWNLOAD_REALNAME.length());
        }else{
            downloadKey = storageFileHandler.getDestinationName(downloadKey);
        }
        downloadKey = decodeDownloadKey(downloadKey);
        File f = new File(getBaseStorageDirectory(), downloadKey);
        if(!f.exists()){
            return null;
        }
        if (!compressed) {
            try {
                File tempDir = createTempDirectory(TEMP_KEY);
                storageFileHandler.writeToDirectory(f, tempDir);
                f = getFileIn(tempDir);
            } catch (IOException e) {
                throw new UnipooleException(FILE_MANIPULATION, "Could not extract the file.", e);
            }
        }
        return f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyToStorage(File source, File destination) throws UnipooleException{
        try {
            if (source.isDirectory()) {
                storageFileHandler.writeFromDirectory(source, destination);
            } else {
                if (storageFileHandler.canHandle(source)) {
                    //copy from the same file type to the same file type
                    FileCopyUtils.copy(source, destination);
                } else {
                    File tempDir = getTempDirectory(TEMP_KEY, TEMP_PREFIX);
                    try {
                        getFileHandler(source).writeToDirectory(source, tempDir);
                        storageFileHandler.writeFromDirectory(tempDir, destination);
                    } finally {
                        FileSystemUtils.deleteRecursively(tempDir);
                    }
                }
            }
        } catch (IOException e) {
            throw new UnipooleException(FILE_MANIPULATION, "Failed to copy the source to the destination.", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void copyToDirectory(File source, File destination) throws UnipooleException{
        if(source == null || !source.exists()){
            return;
        }
        StorageFileHandler fileHandler = getFileHandler(source);
        if(fileHandler == null){
            throw new UnipooleException(FILE_MANIPULATION, "No StorageFileHandler for source.");
        }
        try {
            fileHandler.writeToDirectory(source, destination);
        } catch (IOException e) {
            throw new UnipooleException(FILE_MANIPULATION, "Failed to write to directory.", e);
        }
    }

    /**
     * Validate that the directory exist, and is a directory. Will create it if it doesn't.
     */
    private void validateDirectory(File file) throws UnipooleException {
        if (!file.exists() && !file.mkdirs()) {
            throw new UnipooleException(FILE_MANIPULATION, "Could not create the directory for the storage: " + file.getPath());
        } else if (file.isFile()) {
            throw new UnipooleException(FILE_MANIPULATION, "The directory for the storage is a file, should be a directory: " + file.getPath());
        }
    }

    /**
     * Validate that the file exist, and is a file. Will create it if it doesn't.
     */
    private void validateFile(File file) throws UnipooleException{
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new UnipooleException(FILE_MANIPULATION, "The file for storage is a directory but should be a file: " + file.getPath());
            } else {
                return;
            }
        }
        if (!file.getParentFile().mkdirs()) {
            throw new UnipooleException(FILE_MANIPULATION, "Could not create the parent directory for the file: " + file.getPath());
        }
        try {
            if (file.createNewFile()) {
                throw new UnipooleException(FILE_MANIPULATION, "The storage file could not be created: " + file.getPath());
            }
        } catch (IOException e) {
            throw new UnipooleException(FILE_MANIPULATION, "The storage file could not be created.", e);
        }
    }

    /**
     * Get the first file for the given file object.
     */
    private File getFileIn(File file) {
        if (file == null) {
            return null;
        }
        if (file.exists() && file.isFile()) {
            return file;
        }
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                File returnedFile = getFileIn(f);
                if (returnedFile != null) {
                    return returnedFile;
                }
            }
        }
        return null;
    }
}
/**
 * A cleanup task to delete old directories.
 */
class TempCleanupTask implements Runnable{
    private final File directory;

    public TempCleanupTask(File directory){
        this.directory = directory;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if(directory.exists()){
            //24 hours ago
            long deleteTime = System.currentTimeMillis() - (24*60*60*1000);
            clean(directory, deleteTime, false);
        }
    }
    
    private boolean clean(File dir, long deleteTime, boolean canDeleteThisDirectory){
        if(dir.lastModified() > deleteTime){
            return false;
        }
        boolean canDelete = true;
        File[] files = dir.listFiles();
        for(File f: files){
            if(f.isFile() && f.lastModified() > deleteTime){
                canDelete = false;
            }
            if(f.isDirectory() && !clean(f, deleteTime, true)){
                canDelete = false;
            }
        }
        if(canDelete && canDeleteThisDirectory){
            delete(dir);
        }
        return canDelete;
    }
    
    private boolean delete(File file){
        if(file.isFile()){
            return (file.delete());
        }
        boolean deleted = true;
        if(file.isDirectory()){
            for(File f: file.listFiles()){
                if(!delete(f)){
                    deleted = false;
                }
            }
            if(deleted){
                return file.delete();
            }
        }
        return false;
    }
}

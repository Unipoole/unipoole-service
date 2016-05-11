package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.ErrorCodes;
import coza.opencollab.unipoole.service.creator.StorageService;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.dbo.ManagedModule;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

/**
 * The tool converter for the sakai.home tool.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class StaticHomeHandler implements ToolContentService {

    /**
     * The key for the storage service.
     */
    private static final String STORAGE_KEY = "homeTool";
    /**
     * The file that contains the data
     */
    private static final String STORAGE_FILE = "home.properties";
    /**
     * The property for the count.
     */
    private static final String PROPERTY_COUNT = "iFrame.count";
    /**
     * The property prefixes.
     */
    private static final String PROPERTY_PREFIX = "iFrame";
    /**
     * The property for tool.
     */
    private static final String PROPERTY_TOOL = "tool";
    /**
     * The property for defaultPage.
     */
    private static final String PROPERTY_PAGE = "defaultPage";
    /**
     * The property for heading.
     */
    private static final String PROPERTY_HEADING = "heading";
    /**
     * The storage service.
     */
    @Autowired
    private StorageService storageService;
    /**
     * The dao.
     */
    @Autowired
    private Dao dao;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getContent(String sessionId, String moduleId, Date fromDate) {
        long fromDateMillis = 0L;
        if (fromDate != null) {
            fromDateMillis = fromDate.getTime();
        }
        String contents = getFileContents(STORAGE_FILE, moduleId, fromDateMillis);
        if (contents == null) {
            return null;
        }
        try {
            Map data = new HashMap();
            Properties p = new Properties();
            p.load(new StringReader(contents));
            int count = Integer.parseInt(p.getProperty(PROPERTY_COUNT));
            for (int i = 0; i < count;) {
                String prop = PROPERTY_PREFIX + ++i;
                Map<String, String> frame = new HashMap<String, String>();
                frame.put(PROPERTY_TOOL, p.getProperty(prop + "." + PROPERTY_TOOL).trim());
                frame.put(PROPERTY_PAGE, p.getProperty(prop + "." + PROPERTY_PAGE).trim());
                frame.put(PROPERTY_HEADING, p.getProperty(prop + "." + PROPERTY_HEADING).trim());
                data.put(prop, frame);
            }
            return data;
        } catch (IOException e) {
            throw new UnipooleException(ErrorCodes.FILE_MANIPULATION, "Invalid properties for the home tool.", e);
        }
    }

    /**
     * Get the file contents if the data is newer then the from date.
     */
    private String getFileContents(String fileName, String moduleId, long fromDateMilli) {
        //first check the module
        File directory = storageService.getStorageDirectory(STORAGE_KEY, moduleId);
        File file = new File(directory, fileName);
        if (!file.exists()) {
            //second check the master module
            //second check the master module
            ManagedModule mm = dao.getManagedModule(moduleId);
            if (mm != null && mm.getMasterModuleId() != null) {
                directory = storageService.getStorageDirectory(STORAGE_KEY, mm.getMasterModuleId());
                file = new File(directory, fileName);
            }
            if (!file.exists()) {
                //thirdly check the global
                directory = storageService.getStorageDirectory(STORAGE_KEY);
                file = new File(directory, fileName);
            }
        }
        if (!file.exists()) {
            writeDefault(file);
        }
        if (file.lastModified() > fromDateMilli) {
            return storageService.getFileContents(file.getParentFile(), file.getName());
        } else {
            return null;
        }
    }

    /**
     * If no config file exist for the home tool we write a default.
     */
    private void writeDefault(File file) {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(getClass().getPackage().getName().replace(".", "/") + "/" + STORAGE_FILE);
            byte[] defaultFileBytes = FileCopyUtils.copyToByteArray(in);
            FileCopyUtils.copy(defaultFileBytes, file);
        } catch (IOException e) {
            Logger.getLogger(StaticHomeHandler.class).warn("Could not write the default sakai.home properties file.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUserContent(String sessionId, String username, String moduleId, Date fromDate) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map updateUserContent(String sessionId, String username, String moduleId, Map content, String originalContent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

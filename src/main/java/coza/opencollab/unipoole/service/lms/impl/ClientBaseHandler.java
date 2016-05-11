package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.ErrorCodes;
import coza.opencollab.unipoole.service.creator.StorageService;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.dbo.ManagedModule;
import coza.opencollab.unipoole.service.lms.LMSClient;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import coza.opencollab.unipoole.shared.Tool;
import coza.opencollab.unipoole.util.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

/**
 * This tool service retrieve the data for the base client.
 * <p>
 * The following files are managed; - errorMessages.json - messages.json -
 * settings.json - unipooleData.json
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class ClientBaseHandler implements ToolContentService<Map<String, String>, Object> {

    /**
     * The key for the storage service.
     */
    private static final String STORAGE_KEY = "clientbase";
    /**
     * The errorMessages file.
     */
    private static final String FILE_ERROR_MESSAGES = "errorMessages.json";
    /**
     * The messages file.
     */
    private static final String FILE_MESSAGES = "messages.json";
    /**
     * The settings file.
     */
    private static final String FILE_SETTINGS = "settings.json";
    /**
     * The unipooleData file.
     */
    private static final String FILE_UNIPOOLE_DATA = "unipooleData.json";
    /**
     * The files in a array.
     */
    private static final String[] FILES = {FILE_ERROR_MESSAGES, FILE_MESSAGES, FILE_SETTINGS, FILE_UNIPOOLE_DATA};
    /**
     * The storage service.
     */
    @Autowired
    private StorageService storageService;
    /**
     * The lms client.
     */
    @Autowired
    private LMSClient lMSClient;
    /**
     * The dao.
     */
    @Autowired
    private Dao dao;
    /**
     * Items that should not be on the menu.
     */
    private List<String> notOnMenu = new ArrayList<String>();

    /**
     * Items that should not be on the menu.
     */
    public void setNotOnMenu(List<String> notOnMenu) {
        this.notOnMenu = notOnMenu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getContent(String sessionId, String moduleId, Date fromDate) {
        long fromDateMillis = 0L;
        if (fromDate != null) {
            fromDateMillis = fromDate.getTime();
        }
        Map<String, String> data = new HashMap<String, String>();
        for (String fileName : FILES) {
            String contents = getFileContents(fileName, moduleId, fromDateMillis);
            if (contents != null) {
                data.put(fileName, contents);
            }
        }
        return data;
    }

    /**
     * Get the file contents if the data is newer then the from date.
     */
    private String getFileContents(String fileName, String moduleId, long fromDateMilli) {
        //first check the module
        File directory = storageService.getStorageDirectory(STORAGE_KEY, moduleId);
        File file = new File(directory, fileName);
        if (!file.exists() && !FILE_UNIPOOLE_DATA.equals(fileName)) {
            //second check the master module
            ManagedModule mm = dao.getManagedModule(moduleId);
            if(mm != null && mm.getMasterModuleId() != null){
                directory = storageService.getStorageDirectory(STORAGE_KEY, mm.getMasterModuleId());
                file = new File(directory, fileName);
            }
        }
        String contents = null;
        if (file.exists() && file.lastModified() > fromDateMilli) {
            contents = storageService.getFileContents(file.getParentFile(), file.getName());
        }
        if (FILE_UNIPOOLE_DATA.equals(fileName)) {
            contents = updateMenu(directory, contents, moduleId, fromDateMilli);
        }
        return contents;
    }

    /**
     * This method checks if the tool list for the module changed and then if
     * updates the menu items for the client. It use a file and then check
     * whether the file changed since the last check but also check whether the
     * tools changed since the last file write.
     *
     */
    private String updateMenu(File directory, String contents, String moduleId, long fromDateMilli) {
        boolean updated = false;
        Map unipooleData;
        if (contents == null) {
            unipooleData = new HashMap();
        } else {
            unipooleData = JsonParser.parseJsonToMap(contents);
        }
        Map toolDescriptions = (Map) unipooleData.get("toolDescriptions");
        if (toolDescriptions == null) {
            toolDescriptions = new HashMap();
            unipooleData.put("toolDescriptions", toolDescriptions);
        }
        File file = new File(directory, "menuItems.list");
        List<String> existingMenuItems;
        List<String> updatedMenuItems = new ArrayList<String>();
        List<String> allMenuItems = new ArrayList<String>();
        List<String> removeMenuItems = new ArrayList<String>();
        try {
            if (file.exists()) {
                updated = (file.lastModified() > fromDateMilli);
                String fileContents = FileCopyUtils.copyToString(new FileReader(file));
                existingMenuItems = new ArrayList(Arrays.asList(fileContents.split(",")));
            } else {
                existingMenuItems = new ArrayList<String>();
            }
            List<Tool> tools = lMSClient.getTools(moduleId);
            if (tools != null) {
                for (Tool tool : tools) {
                    allMenuItems.add(tool.getName());
                }
            }
            Iterator<String> existingI = existingMenuItems.iterator();
            while(existingI.hasNext()){
                String toolNameBool = existingI.next();
                String toolName = toolNameBool;
                if(toolNameBool.endsWith(".true") || toolNameBool.endsWith(".false")){
                    toolName = toolNameBool.substring(0, toolNameBool.lastIndexOf("."));
                }else{
                    toolNameBool = toolNameBool + ".true";
                }
                if(allMenuItems.contains(toolName)){
                    allMenuItems.remove(toolName);
                    updatedMenuItems.add(toolNameBool);
                }else{
                    existingI.remove();
                    removeMenuItems.add(toolName);
                }
            }
            if(!allMenuItems.isEmpty()){
                updated = true;
                for(String toolName: allMenuItems){
                    if(notOnMenu.contains(toolName)){
                        updatedMenuItems.add(toolName+".false");
                    }else{
                        updatedMenuItems.add(toolName+".true");
                    }
                }
                StringBuilder buf = new StringBuilder(updatedMenuItems.get(0));
                for (int i = 1; i < updatedMenuItems.size(); i++) {
                    buf.append(",");
                    buf.append(updatedMenuItems.get(i));
                }
                FileCopyUtils.copy(buf.toString(), new FileWriter(file));
            }
        } catch (Exception e) {
            throw new UnipooleException(ErrorCodes.FILE_MANIPULATION, "Could not read or write the menuItems file.", e);
        }
        if (updated) {
            for (String toolNameBool : updatedMenuItems) {
                String toolName = toolNameBool;
                boolean onMenu = true;
                if(toolNameBool.endsWith(".true")){
                    onMenu = true;
                    toolName = toolNameBool.substring(0, toolNameBool.lastIndexOf("."));
                }else if(toolNameBool.endsWith(".false")){
                    onMenu = false;
                    toolName = toolNameBool.substring(0, toolNameBool.lastIndexOf("."));
                }
                
                Map toolDesc = (Map) toolDescriptions.get(toolName);
                if (toolDesc == null) {
                    toolDesc = new HashMap();
                    toolDescriptions.put(toolName, toolDesc);
                }
                toolDesc.put("menu", onMenu);
            }
            for (String toolName: removeMenuItems) {
                Map toolDesc = (Map) toolDescriptions.get(toolName);
                if (toolDesc == null) {
                    toolDesc = new HashMap();
                    toolDescriptions.put(toolName, toolDesc);
                }
                toolDesc.put("menu", false);
            }
            return JsonParser.writeJson(unipooleData);
        } else {
            return contents;
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

package coza.opencollab.unipoole.service.creator;

import coza.opencollab.unipoole.UnipooleException;
import static coza.opencollab.unipoole.util.JsonParser.*;
import coza.opencollab.unipoole.service.BaseController;
import coza.opencollab.unipoole.service.ErrorCodes;
import coza.opencollab.unipoole.service.creator.so.CreatorClient;
import coza.opencollab.unipoole.shared.Module;
import coza.opencollab.unipoole.shared.Tool;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller for Unipoole Creator Services.
 * <p>
 * Retrieves data about available modules that can be used to create clients
 * and initiates creation of clients.
 * <p>
 * This class handle RestFull service calls using JSON.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
@Controller
@RequestMapping("/service-creator")
public class CreatorController extends BaseController{
    /**
     * The Creator Service injected by Spring
     */
    @Autowired
    private CreatorService creatorService;
    /**
     * A mime type map.
     */
    private final ConfigurableMimeFileTypeMap mimeTypeMap = new ConfigurableMimeFileTypeMap();
    
    /**
     * Retrieves all the modules for the given parameters.
     * 
     * @param year The year of the module. A 2 digit number, ex 06 for 2006
     * @param semester The semester code
     * @param moduleCode The module code.
     * @return All the modules for the criteria.
     */
    @RequestMapping(value = "/modules/{year}/{semester}/{moduleCode}", method = RequestMethod.GET)
    public @ResponseBody List<Module> getModules(@PathVariable String year, @PathVariable String semester, @PathVariable String moduleCode){
        return creatorService.getModules(year, semester, moduleCode);
    }
    
    /**
     * Retrieves all the tools for the given module.
     * 
     * @param moduleId The module id.
     * @return All the tools for the module.
     */
    @RequestMapping(value = "/tools/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody List<Tool> getTools(@PathVariable String moduleId){
        return creatorService.getTools(moduleId);
    }
    
    /**
     * Create the client not linked to any module.
     * 
     * @return A map with relevant data about the created client
     */
    @RequestMapping(value = "/createClient", method = RequestMethod.GET)
    public @ResponseBody CreatorClient createClient(){
        return creatorService.createClient(null);
    }
    
    /**
     * Create the client for the given module id.
     * 
     * @param moduleId The module id as retrieved from the get modules function.
     * @return A map with relevant data about the created client
     */
    @RequestMapping(value = "/createClient/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody CreatorClient createClient(@PathVariable String moduleId){
        return creatorService.createClient(moduleId, null);
    }
    
    /**
     * Create the client for the given module id for the tools listed in the body.
     * 
     * @param body The tool list.
     * @param moduleId The module id as retrieved from the get modules function.
     * @return A map with relevant data about the created client
     */
    @RequestMapping(value = "/createClient/{moduleId}", method = RequestMethod.POST)
    public @ResponseBody CreatorClient createClient(@RequestBody String body, @PathVariable String moduleId){
        Map<String, ?> data = parseJsonToMap(body);
        List<String> toolNames = (List<String>) data.remove("tools");
        return creatorService.createClient(moduleId, toolNames, (Map<String, String>)data);
    }
    
    /**
     * Create the client for the given user.
     * <p>
     * The client will contain all the modules for the user.
     * 
     * @param body The request body.
     * @param username The username of the user.
     * @return A map with relevant data about the created client
     */
    @RequestMapping(value = "/myClient/{username}", method = RequestMethod.POST)
    public @ResponseBody CreatorClient createClientUser(@RequestBody String body, @PathVariable String username){
        Map<String, String> data = (Map<String, String>) parseJsonToMap(body);
        return creatorService.createClient(username, data.get("password"), data);
    }
    
    /**
     * Create the client for the given user and module.
     * <p>
     * The client will contain only the module given for the user.
     * 
     * @param body The request body.
     * @param username The username of the user.
     * @param moduleId The module id to create the client for.
     * @return A map with relevant data about the created client
     */
    @RequestMapping(value = "/myClient/{username}/{moduleId}", method = RequestMethod.POST)
    public @ResponseBody CreatorClient createClient(@RequestBody String body, @PathVariable String username, @PathVariable String moduleId){
        Map<String, String> data = (Map<String, String>) parseJsonToMap(body);
        return creatorService.createClient(username, data.get("password"), moduleId, data);
    }
    
    /**
     * Download the file.
     * 
     * @param downloadKey The key to reference the download.
     * @return The file.
     */
    @RequestMapping(value = "/download/{downloadKey:.+}", method = RequestMethod.GET)
    public void download(@PathVariable String downloadKey, HttpServletResponse response) {
        download(downloadKey, response, true);
    }
    
    /**
     * Download the file in its original format.
     * 
     * @param downloadKey The key to reference the download.
     * @return The file.
     */
    @RequestMapping(value = "/download/compressed/{downloadKey:.+}", method = RequestMethod.GET)
    public void downloadCompressed(@PathVariable String downloadKey, HttpServletResponse response) {
        download(downloadKey, response, true);
    }
    
    /**
     * Download the file.
     * 
     * @param downloadKey The key to reference the download.
     * @return The file.
     */
    @RequestMapping(value = "/download/file/{downloadKey:.+}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable String downloadKey, HttpServletResponse response) {
        download(downloadKey, response, false);
    }
    
    /**
     * Retrieves the unipooleData.json content for a module
     * 
     * @param module The module
     */
    @RequestMapping(value = "/clientData/{module}", method = RequestMethod.GET)
    public @ResponseBody String getModuleClientData(@PathVariable String module, HttpServletResponse response) {
        return creatorService.getClientData(module);
    }
    
    /**
     * Write the file to the response stream for download.
     */
    private void download(String downloadKey, HttpServletResponse response, boolean compressed){
        File file = creatorService.getDownloadFile(downloadKey, compressed);
        if(file == null){
            throw new UnipooleException(ErrorCodes.FILE_MANIPULATION, "File not found.");
        }
        response.setContentType(mimeTypeMap.getContentType(file.getName().toLowerCase()));
        OutputStream os = null;
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + file.getName() + "\"");
            os = response.getOutputStream();
            os.write(FileCopyUtils.copyToByteArray(file));
            os.flush();
        } catch (IOException e) {
            Logger.getLogger(CreatorController.class).error("Failed to write file for download.", e);
            throw new UnipooleException(ErrorCodes.FILE_MANIPULATION, "Failed to write file for download.", e);
        } finally {
            try {
                if(os != null){
                    os.close();
                }
            } catch (Exception e) {
            }
        }
    }
}

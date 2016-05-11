package coza.opencollab.unipoole.service.report;

import coza.opencollab.unipoole.service.BaseController;
import static coza.opencollab.unipoole.service.BaseController.DEFAULT_CLIENT_BASE_NAME;
import coza.opencollab.unipoole.service.report.so.Status;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller for retrieving current data, like code or content versions. 
 * This class handle RestFull service calls using JSON.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
@Controller
@RequestMapping("/service-status")
public class StatusController extends BaseController{    
    /**
     * The status service injected by Spring.
     */
    @Autowired
    private StatusService statusService;
    
    /**
     * Retrieve all the usernames.
     * 
     * @return A list of the active usernames
     */
    @RequestMapping(value = "/usernames", method = RequestMethod.GET)
    public @ResponseBody List<String> getUsernames(){
        return statusService.getUsernames();
    }
    
    /**
     * Retrieve all the user's device ids.
     * 
     * @param username The username.
     * @return A list of the active device ids.
     */
    @RequestMapping(value = "/deviceids/{username}", method = RequestMethod.GET)
    public @ResponseBody List<String> getDeviceIds(@PathVariable String username){
        return statusService.getDeviceIds(username);
    }
    
    /**
     * Retrieve all the user's active module ids.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @return A list of the active module ids.
     */
    @RequestMapping(value = "/moduleids/{username}/{deviceId}", method = RequestMethod.GET)
    public @ResponseBody List<String> getModuleIds(@PathVariable String username, @PathVariable String deviceId){
        return statusService.getModuleIds(username, deviceId);
    }
    
    /**
     * This method handles calls to get the current client base code version.
     * 
     * @return The current client version.
     */
    @RequestMapping(value = "/clientCodeVersion", method = RequestMethod.GET)
    public @ResponseBody String clientCodeVersion() {
        return statusService.getCurrentCodeVersion(DEFAULT_CLIENT_BASE_NAME);
    }
    
    /**
     * This method handles calls to get the current client base content version.
     * 
     * @return The current client content version.
     */
    @RequestMapping(value = "/clientContentVersion/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody String clientContentVersion(@PathVariable String moduleId) {
        return statusService.getCurrentContentVersion(moduleId, DEFAULT_CLIENT_BASE_NAME);
    }
    
    /**
     * Retrieve all the tool names.
     * 
     * @return The tool names.
     */
    @RequestMapping(value = "/tools", method = RequestMethod.GET)
    public @ResponseBody List<String> getTools(){
        return statusService.getTools();
    }
    
    /**
     * This method handles calls to get the current tool code version.
     * 
     * @param toolName The tool name.
     * @return The current tool version.
     */
    @RequestMapping(value = "/toolCodeVersion/{toolName:.+}", method = RequestMethod.GET)
    public @ResponseBody String toolCodeVersion(@PathVariable String toolName) {
        return statusService.getCurrentCodeVersion(toolName);
    }
    
    /**
     * This method handles calls to get the current tool content version.
     * 
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @return The current tool content version.
     */
    @RequestMapping(value = "/toolContentVersion/{moduleId}/{toolName:.+}", method = RequestMethod.GET)
    public @ResponseBody String toolContentVersion(@PathVariable String moduleId, @PathVariable String toolName) {
        return statusService.getCurrentContentVersion(moduleId, toolName);
    }
    
    /**
     * This method retrieves all the code versions of all the tools.
     * 
     * @return The current tool versions.
     */
    @RequestMapping(value = "/codeVersions", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> codeVersions() {
        return statusService.getCurrentCodeVersions();
    }
    
    /**
     * This method retrieves all the content versions of all the tools.
     * 
     * @param moduleId The module id.
     * @return The current content versions.
     */
    @RequestMapping(value = "/contentVersions/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> contentVersions(@PathVariable String moduleId) {
        return statusService.getCurrentContentVersions(moduleId);
    }
    
    /**
     * This method handles calls to get the client base code version.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @return The client version.
     */
    @RequestMapping(value = "/clientCodeVersion/{username}/{deviceId}", method = RequestMethod.GET)
    public @ResponseBody String clientCodeVersion(@PathVariable String username, @PathVariable String deviceId) {
        return statusService.getClientCodeVersion(username, deviceId, DEFAULT_CLIENT_BASE_NAME);
    }
    
    /**
     * This method handles calls to get the client base content version.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @return The current client content version.
     */
    @RequestMapping(value = "/clientContentVersion/{username}/{deviceId}/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody String clientContentVersion(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId) {
        return statusService.getClientContentVersion(username, deviceId, moduleId, DEFAULT_CLIENT_BASE_NAME);
    }
    
    /**
     * This method handles calls to get the client tool code version.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @param toolName The tool name.
     * @return The current tool version.
     */
    @RequestMapping(value = "/toolCodeVersion/{username}/{deviceId}/{toolName:.+}", method = RequestMethod.GET)
    public @ResponseBody String toolCodeVersion(@PathVariable String username, @PathVariable String deviceId, @PathVariable String toolName) {
        return statusService.getClientCodeVersion(username, deviceId, toolName);
    }
    
    /**
     * This method handles calls to get the client tool content version.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @return The current tool content version.
     */
    @RequestMapping(value = "/toolContentVersion/{username}/{deviceId}/{moduleId}/{toolName:.+}", method = RequestMethod.GET)
    public @ResponseBody String toolContentVersion(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String toolName) {
        return statusService.getClientContentVersion(username, deviceId, moduleId, toolName);
    }
    
    /**
     * This method retrieves all the client code versions of all the tools.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @return The current tool versions.
     */
    @RequestMapping(value = "/codeVersions/{username}/{deviceId}", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> codeVersions(@PathVariable String username, @PathVariable String deviceId) {
        return statusService.getClientCodeVersions(username, deviceId);
    }
    
    /**
     * This method retrieves all the client content versions of all the tools.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @param moduleId The module id.
     * @return The current content versions.
     */
    @RequestMapping(value = "/contentVersions/{username}/{deviceId}/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> contentVersions(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId) {
        return statusService.getClientContentVersions(username, deviceId, moduleId);
    }
    
    /**
     * Retrieve the status of the client's client base and the current client base. Code and content.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @return The Status.
     */
    @RequestMapping(value = "/clientStatus/{username}/{deviceId}/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody Status getClientStatus(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId){
        return getStatus(username, deviceId, moduleId, DEFAULT_CLIENT_BASE_NAME);
    }
    
    /**
     * Retrieve the status of the client's tool base and the current client base. Code and content.
     * 
     * @param username The user name.
     * @param deviceId the device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @return The Status.
     */
    @RequestMapping(value = "/status/{username}/{deviceId}/{moduleId}/{toolName:.+}", method = RequestMethod.GET)
    public @ResponseBody Status getStatus(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String toolName){
        return statusService.getStatus(username, deviceId, moduleId, toolName);
    }
}

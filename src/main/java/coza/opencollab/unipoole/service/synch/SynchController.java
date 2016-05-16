package coza.opencollab.unipoole.service.synch;

import static coza.opencollab.unipoole.util.JsonParser.*;
import coza.opencollab.unipoole.service.BaseController;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.auth.AuthenticationService;
import coza.opencollab.unipoole.service.synch.so.SyncContentMapping;
import coza.opencollab.unipoole.service.synch.so.SynchStatus;
import coza.opencollab.unipoole.service.synch.so.SynchContent;
import coza.opencollab.unipoole.service.synch.so.UpdateContent;
import coza.opencollab.unipoole.service.synch.so.UpdateStatus;
import coza.opencollab.unipoole.service.util.impl.ZipUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  The controller for content. This handle content checks and synchronization
 * with sakai. This class handle RestFull service calls using JSON.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
@Controller
@RequestMapping("/service-synch")
public class SynchController extends BaseController{
    
    /**
     * The client & tool synch implementation injected by Spring that will do all the work.
     */
    @Autowired
    private SynchService synchService;
    
    /**
     * The auth service
     */
    @Autowired
    private AuthenticationService authenticationService;
    
    /**
     * Retrieve all the file (code) updates for the client from the given version to the current version.
     * So the files returned is only the updated file, not all.
     * 
     * @param username The username
     * @param deviceId The device Id
     * @param clientVersion The client code version
     * @return The updated files for the current client versions
     */
    @RequestMapping(value = "/clientUpdate/{username}/{deviceId}/{clientVersion:.+}", method = RequestMethod.GET)
    public @ResponseBody SynchContent getClientUpdate(@PathVariable String username, @PathVariable String deviceId, @PathVariable String clientVersion) {
        clientVersion = checkVersion(clientVersion);
        return getToolUpdate(username, deviceId, DEFAULT_CLIENT_BASE_NAME, clientVersion);
    }
    
    /**
     * Retrieve all the file (code) updates for the client from the given version to the current version.
     * So the files returned is only the updated file, not all.
     * 
     * @param username The username
     * @param deviceId The device Id
     * @param toolName The tool name
     * @param toolVersion The tool code version
     * @return The updated files for the current tool versions
     */
    @RequestMapping(value = "/toolUpdate/{username}/{deviceId}/{toolName:.+}/{toolVersion:.+}", method = RequestMethod.GET)
    public @ResponseBody SynchContent getToolUpdate(@PathVariable String username, @PathVariable String deviceId, @PathVariable String toolName, @PathVariable String toolVersion) {
        toolVersion = checkVersion(toolVersion);
        return synchService.getUpdate(username, deviceId, toolName, toolVersion);
    }
    
    /**
     * This method handles calls to retrieve tool content.
     * 
     * @param body The body contain the password. {\"password\":\"123\"}
     * @param username The username
     * @param deviceId The device Id
     * @param moduleId The module id
     * @param contentVersion The current tool content version
     * @return The new tool content
     */
    @RequestMapping(value = "/clientContent/{username}/{deviceId}/{moduleId}/{contentVersion:.+}", method = RequestMethod.POST)
    public @ResponseBody SynchContent getClientContent(@RequestBody String body, @PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String contentVersion) {
        contentVersion = checkVersion(contentVersion);
        return getContent(body, username, deviceId, moduleId, DEFAULT_CLIENT_BASE_NAME, contentVersion);
    }
    
    /**
     * This method handles calls to retrieve tool content.
     * 
     * @param body The body contain the password. {\"password\":\"123\"}
     * @param username The username
     * @param deviceId The device Id
     * @param moduleId The module id
     * @param toolName The tool name
     * @param contentVersion The current tool content version
     * @return The new tool content
     */
    @RequestMapping(value = "/contentUpdate/{username}/{deviceId}/{moduleId}/{toolName:.+}/{contentVersion:.+}", method = RequestMethod.POST)
    public @ResponseBody SynchContent getContent(@RequestBody String body, @PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String toolName, @PathVariable String contentVersion) {
        contentVersion = checkVersion(contentVersion);
        String password = getPassword(body);
        return synchService.getContent(username, password, deviceId, moduleId, toolName, contentVersion);
    }
    
        /**
     * This method handles calls to retrieve tool content.
     * 
     * @param body The body contain the password. {\"password\":\"123\"}
     * @param username The username
     * @param deviceId The device Id
     * @param moduleId The module id
     * @param toolName The tool name
     * @param contentVersion The current tool content version
     * @return The new tool content
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/contentUpdateString/{username}/{deviceId}/{moduleId}/{toolName:.+}/{contentVersion:.+}", method = RequestMethod.POST)
    public @ResponseBody SynchContent getContentString(@RequestBody String body, @PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String toolName, @PathVariable String contentVersion) {
        contentVersion = checkVersion(contentVersion);
        String password = getPassword(body);
        SynchContent synchContent= synchService.getContent(username, password, deviceId, moduleId, toolName, contentVersion);
        try {
            Map contentDetail = ZipUtil.getUnZippedBinary(synchContent.getContent());
            synchContent.setContent(new String((byte[]) contentDetail.get("data"), "UTF-8").getBytes("UTF-8"));
            synchContent.setContentString((String) contentDetail.get("text"));
            synchContent.setContentName((String) contentDetail.get("name"));
            synchContent.setMimeType("text/plain");
        }catch(IOException ex) {
            synchContent.setStatus(ServiceCallStatus.ERROR);
            synchContent.setMessage("Could not extract zipped content."); 
            synchContent.setContent(null);
        }        
        return synchContent;
    }
    
    /**
     * This method handles calls to check the synch status of the client, tools and content.
     * 
     * @param body The body should contain json for the <source>SynchStatus</source> class. 
     * Json: {\"deviceId\":\"987\",\"deviceVersion\":\"20131010124513\",\"toolVersions\":{\"assign\":\"99151\",\"announce\":\"134548\",\"faq\":\"514984984\"}}
     * @param username The username
     * @param deviceId The device Id
     * @param moduleId The module id
     * @return The updated <source>SynchStatus</source> containing the out of synch data
     */
    @RequestMapping(value = "/synchStatus/{username}/{deviceId}/{moduleId}", method = RequestMethod.GET)
    public @ResponseBody SynchStatus getSynchStatus(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId) {
    	/*
    	 * This method should be removed, we shouldn't use a stored version that we think the client is at
    	 * The client should do a sync status be sending its current versions, then we check what is required to get to the latest
    	 */
        SynchStatus synchStatus = new SynchStatus();
        synchStatus.setModuleId(moduleId);
        synchStatus.setDeviceId(deviceId);
        return synchService.getSynchStatus(username, synchStatus);
    }
    
    /**
     * This method handles calls to check the synch status of the client, tools and content.
     * 
     * @param body The body should contain json for the <source>SynchStatus</source> class. 
     * Json: {\"deviceId\":\"987\",\"deviceVersion\":\"20131010124513\",\"toolVersions\":{\"assign\":\"99151\",\"announce\":\"134548\",\"faq\":\"514984984\"}}
     * @param username The username
     * @param moduleId The module id
     * @return The updated <source>SynchStatus</source> containing the out of synch data
     */
    @RequestMapping(value = "/synchStatus/{username}/{moduleId}", method = RequestMethod.POST)
    public @ResponseBody SynchStatus getSynchStatusB(@RequestBody String body, @PathVariable String username, @PathVariable String moduleId) {
        SynchStatus synchStatus = parseJson(body, SynchStatus.class);
        synchStatus.setModuleId(moduleId);
        return synchService.getSynchStatus(username, synchStatus);
    }
    
    /**
     * This method handles calls to check the synch status of the client, tools and content.
     * 
     * @param body The body should contain json for the <source>SynchStatus</source> class. 
     * Json: {\"deviceId\":\"987\",\"deviceVersion\":\"20131010124513\",\"toolVersions\":{\"assign\":\"99151\",\"announce\":\"134548\",\"faq\":\"514984984\"}}
     * @param username The username
     * @return The updated <source>SynchStatus</source> containing the out of synch data
     */
    @RequestMapping(value = "/synchStatus/{username}", method = RequestMethod.POST)
    public @ResponseBody SynchStatus getSynchStatus(@RequestBody String body, @PathVariable String username) {
        SynchStatus synchStatus = parseJson(body, SynchStatus.class);
        return synchService.getSynchStatus(username, synchStatus);
    }
    
    /**
     * Update the client's client code version on the server
     * 
     * @param username The username
     * @param deviceId The device Id
     * @param version The current tool version
     */
    @RequestMapping(value = "/clientCodeVersion/{username}/{deviceId}/{version:.+}", method = RequestMethod.PUT)
    public @ResponseBody UpdateStatus updateClientCodeVersion(@PathVariable String username, @PathVariable String deviceId, @PathVariable String version){
        version = checkVersion(version);
        return updateCodeVersion(username, deviceId, DEFAULT_CLIENT_BASE_NAME, version);
    }
    
    /**
     * Update the client's tool version on the server
     * 
     * @param username The username
     * @param deviceId The device Id
     * @param toolName The tool name
     * @param toolVersion The current tool version
     */
    @RequestMapping(value = "/codeVersion/{username}/{deviceId}/{toolName:.+}/{toolVersion:.+}", method = RequestMethod.PUT)
    public @ResponseBody UpdateStatus updateCodeVersion(@PathVariable String username, @PathVariable String deviceId, @PathVariable String toolName, @PathVariable String toolVersion){
        toolVersion = checkVersion(toolVersion);
        return synchService.updateCodeVersion(username, deviceId, toolName, toolVersion);
    }
    
//    /**
//     * Update the client's client content version on the server
//     * 
//     * @param username The username
//     * @param deviceId The device Id
//     * @param version The current tool version
//     */
//    @RequestMapping(value = "/clientContentVersion/{username}/{deviceId}/{moduleId}/{version:.+}", method = RequestMethod.PUT)
//    public @ResponseBody UpdateStatus updateClientContentVersion(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String version){
//        version = checkVersion(version);
//        return updateContentVersion(username, deviceId, moduleId, DEFAULT_CLIENT_BASE_NAME, version);
//    }
//    
//    /**
//     * Update the client's tool content version on the server
//     * 
//     * @param username The username
//     * @param deviceId The device Id
//     * @param moduleId The module id.
//     * @param toolName The tool name
//     * @param toolVersion The current tool version
//     */
//    @RequestMapping(value = "/contentVersion/{username}/{deviceId}/{moduleId}/{toolName:.+}/{toolVersion:.+}", method = RequestMethod.PUT)
//    public @ResponseBody UpdateStatus updateContentVersion(@PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String toolName, @PathVariable String toolVersion){
//        toolVersion = checkVersion(toolVersion);
//        return synchService.updateContentVersion(username, deviceId, moduleId, toolName, toolVersion);
//    }
    
    /**
     * Update/upload the content for the client tool from the client to the server.
     * 
     * @param username The username
     * @param deviceId The device Id
     * @param moduleId The module id
     * @param toolName The tool name
     * @return The updated files for the current tool versions
     */
    @RequestMapping(value = "/content/{username}/{deviceId}/{moduleId}/{toolName:.+}", method = RequestMethod.PUT)
    public @ResponseBody UpdateContent updateContent(@RequestBody String body, @PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId, @PathVariable String toolName) {
        Map<String, ?> content = parseJsonToMap(body);
        String password = getPassword(content);
        return synchService.updateContent(username, password, deviceId, moduleId, toolName, content, body);
    }
    
    
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/contentMappings/{toSite}", method = RequestMethod.POST)
    public @ResponseBody SyncContentMapping getSyncContentMapping(@PathVariable String toSite, @RequestBody String body){
    	List<String> toolNames = (List<String>)parseJsonToList(body);
    	return synchService.getContentMapping(toSite, toolNames);
    }

    /**
     * Get the password from a request. The password may be in the form of a password string, or an encrypted
     * auth token which will be decrypted by the authentication service
     * @param body Body of a request
     * @return The password (possibly decrypted from an auth token)
     */
    private String getPassword(String body){
    	return getPassword(parseJsonToMap(body));
    }
    
    /**
     * Get the password from a request map. The password may be in the form of a password string, or an encrypted
     * auth token which will be decrypted by the authentication service
     * @param content Content map of a request
     * @return The password (possibly decrypted from an auth token)
     */
    private String getPassword(Map<String, ?> content){
    	String authToken = (String)content.get("authToken");
    	if(authToken != null){
    		return authenticationService.decryptPassword(authToken);
    	}
    	else{
    		return (String) content.get("password");
    	}
    }
    /**
     * A helper method to fix versions from javascript
     */
    private String checkVersion(String version){
        if(version == null || version.length() == 0 || "undefined".equals(version)){
            return "0";
        }else{
            return version;
        }
    }
}

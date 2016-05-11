package coza.opencollab.unipoole.service.synch;

import coza.opencollab.unipoole.service.report.so.Status;
import coza.opencollab.unipoole.service.synch.so.SyncContentMapping;
import coza.opencollab.unipoole.service.synch.so.SynchContent;
import coza.opencollab.unipoole.service.synch.so.SynchStatus;
import coza.opencollab.unipoole.service.synch.so.UpdateContent;
import coza.opencollab.unipoole.service.synch.so.UpdateStatus;

import java.util.Map;

/**
 * The api for code and content synchronization handling. Implementing classes will handle all
 * code and content synch checking and synchronization for clients and tools.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface SynchService {

    /**
     * Retrieve all the file (code) updates for the client/tool from the given version to the current version.
     * So the files returned is only the updated file, not all.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param toolName The tool name.
     * @param toolVersion The tool code version.
     * @return The updated files for the current tool versions.
     */
    public SynchContent getUpdate(String username, String deviceId, String toolName, String toolVersion);
    /**
     * Retrieves the tool content. All new content from the current tool content version
     * will be retrieved and the tool content version will be updated.
     * 
     * @param username The username.
     * @param password The user password.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param contentVersion The tool current content version.
     * @return The new tool content. 
     */
    public SynchContent getContent(String username, String password, String deviceId, String moduleId, String toolName, String contentVersion);
    /**
     * Get the status of a tool. This includes code and content for the users version and
     * the current versions.
     * 
     * @param username The client username
     * @param deviceId The users device id
     * @param moduleId The module id.
     * @param toolName The tool name
     * @return The status of the tool
     */
    public Status getStatus(String username, String deviceId, String moduleId, String toolName);
    /**
     * Checks if the client, any of the tools or any content for this user and module is out of synch
     * 
     * @param username The username
     * @param synchStatus The input. This must at least contain the username, moduleId
     * @return The synchStatus containing the statuses for the client and all tools.
     */
    public SynchStatus getSynchStatus(String username, SynchStatus synchStatus);
    /**
     * Update the client's tool code version on the server.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param toolName The tool name.
     * @param toolVersion The current tool version.
     */
    public UpdateStatus updateCodeVersion(String username, String deviceId, String toolName, String toolVersion);
    /**
     * Update the client's tool content version on the server.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param contentVersion The current tool content version.
     */
    public UpdateStatus updateContentVersion(String username, String deviceId, String moduleId, String toolName, String contentVersion);
    /**
     * Update/upload the content for the client tool from the client to the server.
     * 
     * @param username The username.
     * @param password The user password.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param content The content to update.
     * @return The updated files for the current tool versions.
     */
    public UpdateContent updateContent(String username, String password, String deviceId, String moduleId, String toolName, Map<String, ?> content, String originalContent);
    
    /**
     * Gets the content ID mapping from and to a new site for toolId
     * @param toSiteId Id of the site this content was copied to.
     * @param toolId Id of this content on the site this content was copies to. I.e. the group site.
     * @return A list of content mappings
     */
    public SyncContentMapping getContentMapping(String toSiteId, String toolId);

}

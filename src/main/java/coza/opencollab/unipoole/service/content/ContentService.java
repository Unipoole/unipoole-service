package coza.opencollab.unipoole.service.content;

import coza.opencollab.unipoole.service.synch.so.SynchContent;
import coza.opencollab.unipoole.service.synch.so.UpdateContent;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * The service to manage client content.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface ContentService {
    /**
     * Load the module to the content management.
     * 
     * @param moduleId The module id.
     */
    public void loadContent(String moduleId);
    /**
     * This will create a synch package for the user with the updates to the tool
     * from the users version to the current version.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param fromContentVersion The client's current tool content version.
     * @return The synch data with the tool update.
     */
    public SynchContent getContentSynch(String username, String deviceId, String moduleId, String toolName, String fromContentVersion);
    /**
     * Get the size of the update for the tool from the given version to the current.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param fromContentVersion The from version, excluding.
     * @return The approximation size in bytes.
     */
    public long getContentSynchSize(String username, String deviceId, String moduleId, String toolName, String fromContentVersion);
    /**
     * Updated the content for the tool for the user.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param content The user's content
     * @return The status of the update. Could also return extra data.
     */
    public UpdateContent updateUserContent(String username, String deviceId, String moduleId, String toolName, Map<String, ?> content, String originalContent );
    /**
     * Retrieves the client content release.
     * 
     * @param moduleId The module id.
     * @param toolNames The tool names.
     * @param codeReleaseFile The code release file to use.
     * @return The client content release.
     */
    public File getNewContentReleaseFile(String moduleId, List<String> toolNames, File codeReleaseFile);
    /**
     * Retrieves the latest client content release.
     * 
     * @param moduleId The module id.
     * @return The client content release.
     */
    public File getLatestContentReleaseFile(String moduleId);
   
}

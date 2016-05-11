package coza.opencollab.unipoole.service.report;

import coza.opencollab.unipoole.service.report.so.Status;
import java.util.List;
import java.util.Map;

/**
 * This define a service that reports on statuses. This includes client and current code and content
 * statuses
 * 
 * @author OpenCollab
 */
public interface StatusService {
    /**
     * Retrieve all the usernames.
     * 
     * @return A list of the active usernames
     */
    public List<String> getUsernames();
    /**
     * Retrieve all the user's active device ids.
     * 
     * @param username The username.
     * @return A list of the active device ids.
     */
    public List<String> getDeviceIds(String username);
    /**
     * Retrieve all the user's active module ids.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @return A list of the active module ids.
     */
    public List<String> getModuleIds(String username, String deviceId);
    /**
     * Retrieve all the tool names
     * 
     * @return The tool names
     */
    public List<String> getTools();
    /**
     * Get the current (latest) version for the tool.
     * 
     * @param toolName The tool name
     * @return The version.
     */
    public String getCurrentCodeVersion(String toolName);
    /**
     * Get all the current tool versions. The client base version
     * will also be in the list.
     * 
     * @return The versions in a map with the tool names as keys.
     */
    public Map<String, String> getCurrentCodeVersions();
    /**
     * Get the current (latest) version for the tool.
     * 
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @return The version.
     */
    public String getCurrentContentVersion(String moduleId, String toolName);
    /**
     * Get all the current tool content versions. The client base content version
     * will also be in the list.
     * 
     * @param moduleId The module id.
     * @return The versions in a map with the tool names as keys.
     */
    public Map<String, String> getCurrentContentVersions(String moduleId);
    /**
     * Return the last known client code version.
     * 
     * @param username The client username
     * @param deviceId The users device id
     * @param toolName The tool name
     * @return the last known code version for the client tool
     */
    public String getClientCodeVersion(String username, String deviceId, String toolName);
    /**
     * Get all the client last known tool versions. The client base version
     * will also be in the list.
     * 
     * @param username The client username
     * @param deviceId The users device id
     * @return The versions in a map with the tool names as keys.
     */
    public Map<String, String> getClientCodeVersions(String username, String deviceId);
    /**
     * Return the last known client content version.
     * 
     * @param username The client username.
     * @param deviceId The users device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @return the last known content version for the client tool.
     */
    public String getClientContentVersion(String username, String deviceId, String moduleId, String toolName);
    /**
     * Get all the client last known tool content versions. The client base content version
     * will also be in the list.
     * 
     * @param username The client username
     * @param deviceId The users device id
     * @param moduleId The module id.
     * @return The versions in a map with the tool names as keys.
     */
    public Map<String, String> getClientContentVersions(String username, String deviceId, String moduleId);
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
}

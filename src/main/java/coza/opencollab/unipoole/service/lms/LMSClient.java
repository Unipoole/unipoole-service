package coza.opencollab.unipoole.service.lms;

import coza.opencollab.unipoole.shared.Module;
import coza.opencollab.unipoole.shared.Tool;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The api for lms interaction.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface LMSClient {
    /**
     * Log the user in to the lms.
     * 
     * @param username The username
     * @param password The password
     * @return The session id for the valid login
     */
    public String login(String username, String password);
    /**
     * Log a user out of the lms.
     * 
     * @param username The username.
     * @return true if the user was logged out, meaning the session was still valid, false otherwise.
     */
    public boolean logout(String username);
    /**
     * Retrieve extra user details from the lms.
     * 
     * @param username The username.
     * @return Any extra data of the user.
     */
    public Map<String, String> getUserDetails(String username);
    /**
     * Retrieves all the modules for the given parameters.
     * 
     * @param year The year of the module. A 2 digit number, ex 06 for 2006
     * @param semester The semester code
     * @param moduleCode The module code, or part thereof.
     * @return All the modules for the criteria.
     */
    public List<Module> getModules(String year, String semester, String moduleCode);
    /**
     * Retrieves all the master modules for the given criteria.
     * 
     * @param criteria The criteria.
     * @return All the modules for the criteria.
     */
    public List<Module> getMasterModules(String criteria);
    /**
     * Retrieves all the group modules for the given criteria.
     * 
     * @param criteria The criteria.
     * @return All the modules for the criteria.
     */
    public List<Module> getGroupModules(String criteria);
    /**
     * Retrieves all the tools for the given module.
     * @param moduleId The module id.
     * @return All the tools for the module.
     */
    public List<Tool> getTools(String moduleId);
    /**
     * Test whether the user is registered for the module.
     * 
     * @param username The username
     * @param moduleId The module id.
     * @return true if the user is registered for the module on the lms.
     */
    public boolean isRegistered(String username, String moduleId);
    /**
     * Returns the real moduleId if the user is registered for the module.
     * 
     * @param username The username
     * @param moduleId The module id.
     * @return The actual moduleId if the user is registered for the module on the lms, otherwise null.
     */
    public String getRegisteredModuleId(String username, String moduleId);
    /**
     * Retrieves the tool content from the lms.
     * 
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param fromDate The date that the data needs to be retrieved from.
     * @return The data or null if there is no new data.
     */
    public Object getToolContent(String moduleId, String toolName, Date fromDate);
    /**
     * Retrieves the user content from the lms.
     * 
     * @param username The username.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param fromDate The date that the data needs to be retrieved from.
     * @return The data or null if there is no new data.
     */
    public Object getUserContent(String username, String moduleId, String toolName, Date fromDate);
    /**
     * Updates the user content on the lms.
     * 
     * @param username The username.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param content The data to send to the lms.
     * @return Any response from the lms.
     */
    public Map<String, ?> updateUserContent(String username, String moduleId, String toolName, Map<String, ?> content, String originalContent);
    
    /**
     * Get a list of modules a user can access
     * 
     * @param username
     * @return List of modules
     */
    public List<Module> getSitesUserCanAccess(String username);
}

package coza.opencollab.unipoole.service.creator;

import coza.opencollab.unipoole.service.creator.so.CreatorClient;
import coza.opencollab.unipoole.shared.Module;
import coza.opencollab.unipoole.shared.Tool;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * This is the api for all the Unipoole Creator services.
 * <p>
 * This will include retrieving of data and the creation of clients.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface CreatorService {
    /**
     * Retrieves all the modules for the given parameters.
     * 
     * @param year The year of the module. A 2 digit number, ex 06 for 2006
     * @param semester The semester code
     * @param moduleCode The module code.
     * @return All the modules for the criteria.
     */
    public List<Module> getModules(String year, String semester, String moduleCode);
    /**
     * Retrieves all the tools for the given module.
     * 
     * @param moduleId The module id.
     * @return All the tools for the module.
     */
    public List<Tool> getTools(String moduleId);
    /**
     * Create the client not linked to any module.
     * 
     * @param properties A map with properties. Can contain user data, name email ... Can be null.
     * @return The result information about the created client.
     */
    public CreatorClient createClient(Map<String, String> properties);
    /**
     * Create the client for the given module id with all the tools.
     * 
     * @param moduleId The module id as retrieved from the get modules function.
     * @param properties A map with properties. Can contain user data, name email ... Can be null.
     * @return The result information about the created client.
     */
    public CreatorClient createClient(String moduleId, Map<String, String> properties);
    /**
     * Create the client for the given module id.
     * 
     * @param moduleId The module id as retrieved from the get modules function.
     * @param toolNames A list of the tools that must be in the client.
     * @param properties A map with properties. Can contain user data, name email ... Can be null.
     * @return The result information about the created client.
     */
    public CreatorClient createClient(String moduleId, List<String> toolNames, Map<String, String> properties);
    /**
     * Create the client for the given user.
     * <p>
     * The client will contain all the modules for the user.
     * 
     * @param body The request body.
     * @param username The username of the user.
     * @param properties A map with properties. Can contain user data, name email ... Can be null.
     * @return The result information about the created client.
     */
    public CreatorClient createClient(String username, String password, Map<String, String> properties);
    /**
     * Create the client for the given user and module.
     * <p>
     * The client will contain only the module given for the user.
     * 
     * @param body The request body.
     * @param username The username of the user.
     * @param moduleId The module id to create the client for.
     * @param properties A map with properties. Can contain user data, name email ... Can be null.
     * @return The result information about the created client.
     */
    public CreatorClient createClient(String username, String password, String moduleId, Map<String, String> properties);
    /**
     * Returns the file for the given download key.
     * 
     * @param downloadKey The the download key.
     * @return The file object. Will be a valid file object but may not point to a valid file.
     */
    public File getDownloadFile(String downloadKey);
    /**
     * Returns the file for the given download key.
     * 
     * @param downloadKey The the download key.
     * @param compressed Whether to compress the download file or not.
     * @return The file object. Could be null if the file does not exist.
     */
    public File getDownloadFile(String downloadKey, boolean compressed);
    /**
     * Returns the client data config file for a module
     * 
     * @param module
     * @return The string content of the client data config
     */
    public String getClientData(String module);
}

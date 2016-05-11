package coza.opencollab.unipoole.service.content;

import coza.opencollab.unipoole.service.synch.so.SynchContent;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * The implementations must load module content in the Unipoole
 * service.
 * <p>
 * This may be a ongoing task, so content will have to be loaded periodically.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface ContentLoader {
    /**
     * Load the module to the content management.
     * 
     * @param moduleId The module id.
     */
    public void load(String moduleId);
    /**
     * Whether to update non existing version to the current version
     * without throwing an exception.
     */
    public boolean silentlyUpdateNonExistingVersions();
    /**
     * The default non existing version.
     */
    public String getNonExistingVersion();
    /**
     * Retrieves the synch content for the tool. This is content that is valid for 
     * all users.
     * 
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param fromContentVersion The version before the synch package start.
     * @param toContentVersion The end version.
     * @return The synch package.
     */
    public SynchContent getSynchContent(String moduleId, String toolName, String fromContentVersion, String toContentVersion);
    /**
     * Retrieves the synch content for the user on this tool. This is content that is valid for 
     * a specific user.
     * 
     * @param username The username.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @param fromContentVersion The version before the synch package start.
     * @return The synch package.
     */
    public SynchContent getUserContent(String username, String moduleId, String toolName, String fromContentVersion);
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
     * Merge two contents together. These must be valid contents.
     * 
     * @param contentA
     * @param contentB
     * @return The merged content.
     */
    public SynchContent merge(SynchContent contentA, SynchContent contentB);
    /**
     * Retrieves the client content release.
     * 
     * @param moduleId The module id.
     * @param toolNames The tool names.
     * @param codeReleaseFile The client release file to use.
     * @return The client content release.
     */
    public File createContentRelease(String moduleId, List<String> toolNames, File codeReleaseFile);
}

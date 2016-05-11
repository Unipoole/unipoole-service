package coza.opencollab.unipoole.service.code;

import coza.opencollab.unipoole.service.creator.so.CreatorResponse;
import coza.opencollab.unipoole.service.synch.so.SynchContent;
import java.io.File;

/**
 * The service to manage client code.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface CodeService {
    /**
     * Register a new tool or version of the tool.
     * 
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @param location The location of the tool code.
     * @return The result of the registration
     */
    public CreatorResponse loadTool(String toolName, String toolVersion, String location);
    /**
     * This will create a synch package for the user with the updates to the tool
     * from the users version to the current version.
     * 
     * @param toolName The tool name.
     * @param fromContentVersion The client's current tool version.
     * @return The synch data with the tool update.
     */
    public SynchContent getToolSynch(String toolName, String fromContentVersion);
    /**
     * Get the size of the update for the tool from the given version to the current.
     * 
     * @param toolName The tool name.
     * @param fromToolVersion The from version, excluding.
     * @return The approximation size in bytes.
     */
    public long getToolSynchSize(String toolName, String fromToolVersion);
    /**
     * Build, if needed, and retrieve a file pointing to a full client.
     * This is only code, no content.
     * 
     * @return The file pointing to the client.
     */
    public File getNewCodeReleaseFile();
}

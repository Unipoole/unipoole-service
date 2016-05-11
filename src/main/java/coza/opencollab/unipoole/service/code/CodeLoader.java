package coza.opencollab.unipoole.service.code;

import coza.opencollab.unipoole.service.synch.so.SynchContent;
import java.io.File;

/**
 * The implementations must load new tools in the Unipoole
 * service.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface CodeLoader {
    /**
     * Loads the new tool into the Unipoole system for use.
     * 
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @param location The location of the tool code.
     */
    public void load(String toolName, String toolVersion, File source);
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
     * Builds and returns a synch package for the tool.
     * 
     * @param toolName The tool name.
     * @param fromToolVersion The version before the synch package start.
     * @param toToolVersion The end version.
     * @return The synch package.
     */
    public SynchContent getSynchContent(String toolName, String fromToolVersion, String toToolVersion);
    /**
     * Create a new code, only code.
     * <p>
     * This method may return already created clients if it does not need to create a new one.
     * 
     * @return The file pointer to the client.
     */
    public File createCodeRelease();
}

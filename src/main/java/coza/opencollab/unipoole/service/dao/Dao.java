package coza.opencollab.unipoole.service.dao;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.dbo.CodeRelease;
import coza.opencollab.unipoole.service.dbo.CodeReleaseVersion;
import coza.opencollab.unipoole.service.dbo.ContentMapping;
import coza.opencollab.unipoole.service.dbo.ContentRelease;
import coza.opencollab.unipoole.service.dbo.ContentReleaseVersion;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.dbo.ToolVersion;
import coza.opencollab.unipoole.service.dbo.ModuleRegistration;
import coza.opencollab.unipoole.service.dbo.ContentVersion;
import coza.opencollab.unipoole.service.dbo.ManagedModule;

import java.io.Serializable;
import java.util.List;

/**
 * This dao manages the registration process.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface Dao {
    //----- Mics -----//
    /**
     * Retrieve all the usernames.
     * 
     * @return A list of the active usernames.
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
    public List<String> getModuleIds(String username, String deviceId) throws UnipooleException;
    /**
     * Retrieve all the user device tool names.
     * <p>
     * That is all the tool names that is registered for the user in a device.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @return The user's device tool names.
     */
    public List<String> getToolNames(String username, String deviceId) throws UnipooleException;
    /**
     * Retrieve all the master tool names.
     * <p>
     * That is all the tool names that is registered under the master registration.
     * 
     * @return The master tool names.
     */
    public List<String> getMasterToolNames();
    /**
     * Updates/merges the entity to the persistence.
     * 
     * @param entity The entity to update.
     * @return The updated entity
     */
    public <E extends Serializable> E update(E entity);
    //----- ManagedModule -----//
    /**
     * Create the manage module record for the module id.
     * 
     * @param moduleId The module id.
     * @return The manage module.
     */
    public ManagedModule createManagedModule(String moduleId);
    /**
     * Create the manage module record for the module id.
     * 
     * @param moduleId The module id.
     * @param masterModuleId The master module id.
     * @return The manage module.
     */
    public ManagedModule createManagedModule(String moduleId, String masterModuleId);
    /**
     * Retrieve all the active managed modules.
     * 
     * @return All active managed modules.
     */
    public List<ManagedModule> getManagedModules();
    /**
     * Retrieve a existing managed module if it exist.
     * Note this may be active or inactive.
     * 
     * @param moduleId The module id.
     * @return The manage module or null if it does not exist.
     */
    public ManagedModule getManagedModule(String moduleId);
    //----- DeviceRegistration -----//
    /**
     * Create the device registration records for the user and device.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @return The device registration.
     */
    public DeviceRegistration createDeviceRegistration(String username, String deviceId);
    /**
     * Retrieve a existing device registration if it exist.
     * Note this may be active or inactive.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @return The device registration or null if it does not exist.
     */
    public DeviceRegistration getDeviceRegistration(String username, String deviceId);
    /**
     * Retrieves the device registration. 
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @return The device registration.
     * @throws UnipooleException if the device registration cannot be found.
     */
    public DeviceRegistration getDeviceRegistrationWithNullCheck(String username, String deviceId) throws UnipooleException;
    /**
     * Special method that know how to retrieve the master device registration.
     * 
     * @return The master device registration.
     * @throws UnipooleException if the device registration cannot be found.
     */
    public DeviceRegistration getMasterDeviceRegistration() throws UnipooleException;
    //----- ModuleRegistration -----//
    /**
     * Create the module registration records for the device registration and module.
     * 
     * @param deviceRegistration The device registration.
     * @param moduleId The module id.
     * @return The module registration.
     */
    public ModuleRegistration createModuleRegistration(DeviceRegistration deviceRegistration, String moduleId);
    /**
     * Retrieve a existing module registration if it exist.
     * Note this may be active or inactive.
     * 
     * @param deviceRegistration The device registration.
     * @param moduleId The module id.
     * @return The module registration or null if it does not exist.
     */
    public ModuleRegistration getModuleRegistration(DeviceRegistration deviceRegistration, String moduleId);
    /**
     * Retrieves the module registration for the module id.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @return The module registration.
     * @throws UnipooleException if the device registration or the module registration cannot be found.
     */
    public ModuleRegistration getModuleRegistrationWithNullCheck(String username, String deviceId, String moduleId) throws UnipooleException;
    /**
     * Special method that know how to retrieve the master module registration.
     * Note: This is not connected to any real module.
     * 
     * @return The master module registration.
     * @throws UnipooleException if the device registration or the module registration cannot be found.
     */
    public ModuleRegistration getMasterModuleRegistration() throws UnipooleException;
    /**
     * Retrieves the master module registration for the module id.
     * 
     * @param moduleId The module id.
     * @return The module registration.
     * @throws UnipooleException if the device registration or the module registration cannot be found.
     */
    public ModuleRegistration getMasterModuleRegistration(String moduleId) throws UnipooleException;
    /**
     * Set the module registration as inactive.
     * @param moduleRegistration The module registration to deactivate, could be null.
     */
    public void deactivate(ModuleRegistration moduleRegistration);
    //----- ToolVersion -----//
    /**
     * Create the tool version record for the registration and tool
     * 
     * @param deviceRegistration The device registration.
     * @param toolName The tool name.
     * @param version The new version of the tool.
     * @return The tool version.
     */
    public ToolVersion createToolVersion(DeviceRegistration deviceRegistration, String toolName, String version);
    /**
     * Retrieve a existing active tool version if it exist.
     * 
     * @param deviceRegistration The device registration.
     * @param toolName The tool name.
     * @return The tool version or null if it does not exist.
     */
    public ToolVersion getToolVersion(DeviceRegistration deviceRegistration, String toolName);
    /**
     * Retrieve a specific tool version if it exist.
     * Note this may be active or inactive.
     * 
     * @param deviceRegistration The device registration.
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @return The tool version or null if it does not exist.
     */
    public ToolVersion getToolVersion(DeviceRegistration deviceRegistration, String toolName, String toolVersion);
    /**
     * Retrieve the tool version for the user's device.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param toolName The tool name.
     * @return The tool version.
     * @throws UnipooleException if the device registration, module registration or tool version cannot be found.
     */
    public ToolVersion getToolVersionWithNullCheck(String username, String deviceId, String toolName) throws UnipooleException;
    /**
     * Retrieve the master tool version.
     * 
     * @param toolName The tool name.
     * @return the version.
     */
    public ToolVersion getMasterToolVersion(String toolName) throws UnipooleException;
    /**
     * Retrieve the active tool versions for the device registration.
     * 
     * @param deviceRegistration The device registration.
     * @return The tool versions.
     */
    public List<ToolVersion> getToolVersions(DeviceRegistration deviceRegistration);
    /**
     * Retrieve all the user's device tool versions.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @return A list of tool versions.
     * @throws UnipooleException if the device registration cannot be found.
     */
    public List<ToolVersion> getToolVersionsWithNullCheck(String username, String deviceId) throws UnipooleException;
    /**
     * Retrieve all the master tool versions.
     * 
     * @return A list of tool versions.
     * @throws UnipooleException if the device registration cannot be found.
     */
    public List<ToolVersion> getMasterToolVersions() throws UnipooleException;
    /**
     * Retrieve all the tool versions for the tool from the <code>fromToolVersion</code> exclusive to the <code>toToolVersion</code> inclusive.
     * 
     * @param deviceRegistration The device registration.
     * @param toolName The tool name.
     * @param fromToolVersion The from tool version, exclusive.
     * @param toToolVersion The tool version, inclusive.
     * @return The list of tool versions in order from oldest to newest.
     */
    public List<ToolVersion> getToolVersions(DeviceRegistration deviceRegistration, String toolName, String fromToolVersion, String toToolVersion);
    /**
     * Deactivate all tool versions for the device and tool name.
     * 
     * @param deviceRegistrationId The device registration id.
     * @param toolName The tool name.
     * @return The number of tool versions updated.
     */
    public int deactivateAllToolVersions(Long deviceRegistrationId, String toolName);
    /**
     * Deactivate all tool versions for the device and tool name other then this tool version.
     * 
     * @param toolVersion The tool version not to deactivate.
     * @return The number of tool versions updated.
     * @deprecated This creates conflicts with a database unique index
     */
    @Deprecated
    public int deactivateAllOther(ToolVersion toolVersion);
    /**
     * Set the tool version as inactive.
     * @param toolVersion The tool version to deactivate, could be null.
     */
    public void deactivate(ToolVersion toolVersion);
    //----- ContentVersion -----//
    /**
     * Create the content version record for the registration.
     * 
     * @param moduleRegistration The module registration.
     * @param toolName The tool name.
     * @param version The new version of the content.
     * @return The content version.
     */
    public ContentVersion createContentVersion(ModuleRegistration moduleRegistration, String toolName, String version);
    /**
     * Retrieve a existing active content version if it exist.
     * 
     * @param moduleRegistration The module registration.
     * @param toolName The tool name.
     * @return The content version or null if it does not exist.
     */
    public ContentVersion getContentVersion(ModuleRegistration moduleRegistration, String toolName);
    /**
     * Retrieve a specific content version if it exist.
     * Note this may be active or inactive.
     * 
     * @param moduleRegistration The module registration.
     * @param toolName The tool name.
     * @param contentVersion The content version.
     * @return The content version or null if it does not exist.
     */
    public ContentVersion getContentVersion(ModuleRegistration moduleRegistration, String toolName, String contentVersion);
    /**
     * Retrieve a content version that is either the specified version or the closest one before this, if it exist.
     * Note this may be active or inactive.
     * 
     * @param moduleRegistration The module registration.
     * @param toolName The tool name.
     * @param contentVersion The content version.
     * @return The content version or null if it does not exist.
     */
    public ContentVersion getContentVersionCloseTo(ModuleRegistration moduleRegistration, String toolName, String contentVersion);
    /**
     * Retrieve the content version for the user's device.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @return The content version.
     * @throws UnipooleException if the device registration, module registration or content version cannot be found.
     */
    public ContentVersion getContentVersionWithNullCheck(String username, String deviceId, String moduleId, String toolName) throws UnipooleException;
    /**
     * Retrieve the master content version.
     * 
     * @param moduleId The module id.
     * @param toolName The tool name.
     * @return the content version.
     * @throws UnipooleException if the device registration, module registration or content version cannot be found.
     */
    public ContentVersion getMasterContentVersion(String moduleId, String toolName) throws UnipooleException;
    /**
     * Retrieve the active content versions for the module registration.
     * 
     * @param moduleRegistration The module registration.
     * @return The content versions.
     */
    public List<ContentVersion> getContentVersions(ModuleRegistration moduleRegistration);
    /**
     * Retrieve all the user's device content versions.
     * 
     * @param username The username.
     * @param deviceId The device id.
     * @param moduleId The module id.
     * @return A list of content versions.
     * @throws UnipooleException if the device registration or module registration cannot be found.
     */
    public List<ContentVersion> getContentVersionsWithNullCheck(String username, String deviceId, String moduleId) throws UnipooleException;
    /**
     * Retrieve all the master content versions.
     * 
     * @param moduleId The module id.
     * @return A list of content versions.
     * @throws UnipooleException if the device registration or module registration cannot be found.
     */
    public List<ContentVersion> getMasterContentVersions(String moduleId) throws UnipooleException;
    /**
     * Retrieve all the content versions for the tool and module.
     * 
     * @param moduleRegistration The module registration.
     * @param toolName The tool name.
     * @return The list of content versions in order from oldest to newest.
     */
    public List<ContentVersion> getContentVersions(ModuleRegistration moduleRegistration, String toolName);
    /**
     * Retrieve all the content versions for the tool and module from the <code>fromContentVersion</code> exclusive to the <code>toContentVersion</code> inclusive.
     * 
     * @param moduleRegistration The module registration.
     * @param toolName The tool name.
     * @param fromContentVersion The from content version, exclusive.
     * @param toContentVersion The content version, inclusive.
     * @return The list of content versions in order from oldest to newest.
     */
    public List<ContentVersion> getContentVersions(ModuleRegistration moduleRegistration, String toolName, String fromContentVersion, String toContentVersion);
    /**
     * Deactivate all content versions for the device, module and tool name.
     * 
     * @param moduleRegistrationId The module registration id.
     * @param toolName The tool name.
     * @return The number of content versions updated.
     */
    public int deactivateAllContentVersions(Long moduleRegistrationId, String toolName);
    /**
     * Deactivate all content versions for the device, module and tool name other then this content version.
     * 
     * @param contentVersion The content version not to deactivate.
     * @return The number of content versions updated.
     * @deprecated This creates conflicts with a database unique index
     */
    @Deprecated
    public int deactivateAllOther(ContentVersion contentVersion);
    /**
     * Set the content version as inactive.
     * @param contentVersion The content version to deactivate, could be null.
     */
    public void deactivate(ContentVersion contentVersion);
    //----- CodeRelease -----//
    /**
     * Create the code release record.
     * 
     * @param releaseName The release name.
     * @param releaseVersion The release version.
     * @return The newly create record.
     */
    public CodeRelease createCodeRelease(String releaseName, String releaseVersion);
    /**
     * Get the active code release record if it exist.
     * 
     * @return The active record.
     */
    public CodeRelease getCodeRelease();
    /**
     * Get the code release that match the name and version.
     * 
     * @param releaseName The release name.
     * @param releaseVersion The release version.
     * @return The record if it exist.
     */
    public CodeRelease getCodeRelease(String releaseName, String releaseVersion);
    /**
     * Returns all the code releases that has that name.
     * 
     * @param releaseName The release name.
     * @return The list of releases with the given name, if any.
     */
    public List<CodeRelease> getCodeReleases(String releaseName);
    /**
     * Set the code release as inactive.
     * @param codeRelease The code release to deactivate, could be null.
     */
    public void deactivate(CodeRelease codeRelease);
    //----- CodeReleaseVersion -----//
    /**
     * Create the code release version record.
     * 
     * @param codeRelease The code release.
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @return The newly create record.
     */
    public CodeReleaseVersion createCodeReleaseVersion(CodeRelease codeRelease, String toolName, String toolVersion);
    /**
     * Returns all the code releases versions linked to the code release.
     * 
     * @param codeRelease The code release.
     * @return The list of release versions for the given release, if any.
     */
    public List<CodeReleaseVersion> getCodeReleaseVersions(CodeRelease codeRelease);
    //----- ContentRelease -----//
    /**
     * Create the content release record.
     * 
     * @param moduleId The module id.
     * @param releaseName The release name.
     * @param releaseVersion The release version.
     * @return The newly create record.
     */
    public ContentRelease createContentRelease(String moduleId, String releaseName, String releaseVersion);
    /**
     * Get the active content release record if it exist.
     * 
     * @param moduleId The module id.
     * @return The active record.
     */
    public ContentRelease getContentRelease(String moduleId);
    /**
     * Get the content release that match the module id, name and version.
     * 
     * @param moduleId The module id.
     * @param releaseName The release name.
     * @param releaseVersion The release version.
     * @return The record if it exist.
     */
    public ContentRelease getContentRelease(String moduleId, String releaseName, String releaseVersion);
    /**
     * Returns all the content releases that has that module id and name.
     * 
     * @param moduleId The module id.
     * @param releaseName The release name.
     * @return The list of releases with the given name, if any.
     */
    public List<ContentRelease> getContentReleases(String moduleId, String releaseName);
    /**
     * Set the content release as inactive.
     * @param contentRelease The content release to deactivate, could be null.
     */
    public void deactivate(ContentRelease contentRelease);
    //----- ContentReleaseVersion -----//
    /**
     * Create the content release version record.
     * 
     * @param contentRelease The content release.
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @return The newly create record.
     */
    public ContentReleaseVersion createContentReleaseVersion(ContentRelease contentRelease, String toolName, String toolVersion);
    /**
     * Returns all the content releases versions linked to the content release.
     * 
     * @param contentRelease The content release.
     * @return The list of release versions for the given release, if any.
     */
    public List<ContentReleaseVersion> getContentReleaseVersions(ContentRelease contentRelease);
    
    
    /**
     * Gets the content ID mapping from and to a new site for toolId
     * @param toSiteId Id of the site this content was copied to.
     * @param toolId Id of this content on the site this content was copies to. I.e. the group site.
     * @return A list of content mappings
     */
    public List<ContentMapping> getContentMapping(String toSiteId, String toolId);
    
    /**
     * Get the first content version for a tool in a module
     * @param moduleId Id of the Module
     * @param toolId Id of the Tool
     * @return The first content version for the tool.
     */
    public ContentVersion getFirstContentVersion(ModuleRegistration moduleRegistration, String toolId);
}

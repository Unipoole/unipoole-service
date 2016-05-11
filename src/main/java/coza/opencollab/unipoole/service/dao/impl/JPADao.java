package coza.opencollab.unipoole.service.dao.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.Defaults;
import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.service.dbo.ContentVersion;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.dbo.CodeRelease;
import coza.opencollab.unipoole.service.dbo.CodeReleaseVersion;
import coza.opencollab.unipoole.service.dbo.ContentMapping;
import coza.opencollab.unipoole.service.dbo.ContentRelease;
import coza.opencollab.unipoole.service.dbo.ContentReleaseVersion;
import coza.opencollab.unipoole.service.dbo.ManagedModule;
import coza.opencollab.unipoole.service.dbo.ModuleRegistration;
import coza.opencollab.unipoole.service.dbo.ToolVersion;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The JPA implementation of the RegistrationsDao.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
@Transactional(propagation = Propagation.REQUIRED)
@Repository
public class JPADao implements Dao {

    /**
     * The thread save entity manager
     */
    @PersistenceContext
    private EntityManager em;
    
    //----- Mics -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUsernames(){
        try {
            Query query = em.createNamedQuery("usernames");
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDeviceIds(String username) {
        try {
            Query query = em.createNamedQuery("deviceIds");
            query.setParameter(DeviceRegistration.PARAMETER_USERNAME, username);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getModuleIds(String username, String deviceId) throws UnipooleException{
        DeviceRegistration deviceRegistration = getDeviceRegistrationWithNullCheck(username, deviceId);
        try {
            Query query = em.createNamedQuery("moduleIds");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistration.getId());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getToolNames(String username, String deviceId) throws UnipooleException{
        return getToolNames(getDeviceRegistrationWithNullCheck(username, deviceId));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMasterToolNames(){
        return getToolNames(getMasterDeviceRegistration());
    }

    /**
     * Retrieve the toolnames for the device.
     * 
     * @param deviceRegistration The device registration.
     * @return The tool names.
     */
    public List<String> getToolNames(DeviceRegistration deviceRegistration) {
        try {
            Query query = em.createNamedQuery("toolNames");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistration.getId());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Serializable> E update(E entity){
        return em.merge(entity);
    }

    //----- ManagedModule -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public ManagedModule createManagedModule(String moduleId) {
        return createManagedModule(moduleId, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ManagedModule createManagedModule(String moduleId, String masterModuleId){
        ManagedModule managedModule = new ManagedModule();
        managedModule.setModuleId(moduleId);
        managedModule.setMasterModuleId(masterModuleId);
        managedModule.setActive(true);
        try {
            em.persist(managedModule);
            return managedModule;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ManagedModule> getManagedModules(){
        try {
            Query query = em.createNamedQuery("managedModules");
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ManagedModule getManagedModule(String moduleId) {
        try {
            Query query = em.createNamedQuery("managedModule");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_ID, moduleId);
            return (ManagedModule) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    //----- DeviceRegistration -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public DeviceRegistration createDeviceRegistration(String username, String deviceId) {
        //register user
        DeviceRegistration registration = new DeviceRegistration();
        registration.setUsername(username);
        registration.setDeviceId(deviceId);
        registration.setActive(true);
        try {
            em.persist(registration);
            return registration;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeviceRegistration getDeviceRegistration(String username, String deviceId) {
        try {
            Query query = em.createNamedQuery("deviceRegistration");
            query.setParameter(DeviceRegistration.PARAMETER_USERNAME, username);
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_ID, deviceId);
            return (DeviceRegistration) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeviceRegistration getDeviceRegistrationWithNullCheck(String username, String deviceId) throws UnipooleException{
        DeviceRegistration deviceRegistration = getDeviceRegistration(username, deviceId);
        if(deviceRegistration == null){
            throw new UnipooleException(DEVICE_REGISTRATION, "Could not find the device registration for '" + username + ":" + deviceId + "'.");
        }
        return deviceRegistration;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeviceRegistration getMasterDeviceRegistration() throws UnipooleException{
        DeviceRegistration deviceRegistration = getDeviceRegistration(Defaults.MASTER_USERNAME, Defaults.MASTER_DEVICE_ID);
        if(deviceRegistration == null){
            throw new UnipooleException(DEVICE_REGISTRATION, "Could not find the device registration for master.");
        }
        return deviceRegistration;
    }

    //----- ModuleRegistration -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleRegistration createModuleRegistration(DeviceRegistration deviceRegistration, String moduleId) {
        //register user
        ModuleRegistration registration = new ModuleRegistration();
        registration.setDeviceRegistrationId(deviceRegistration.getId());
        registration.setModuleId(moduleId);
        registration.setActive(true);
        try {
            em.persist(registration);
            return registration;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleRegistration getModuleRegistration(DeviceRegistration deviceRegistration, String moduleId) {
        try {
            Query query = em.createNamedQuery("moduleRegistration");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistration.getId());
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_ID, moduleId);
            return (ModuleRegistration) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleRegistration getModuleRegistrationWithNullCheck(String username, String deviceId, String moduleId){
        DeviceRegistration deviceRegistration = getDeviceRegistrationWithNullCheck(username, deviceId);
        ModuleRegistration moduleRegistration = getModuleRegistration(deviceRegistration, moduleId);
        if(moduleRegistration == null){
            throw new UnipooleException(MODULE_REGISTRATION, "Could not find the module registration for '"
                    + deviceRegistration.getUsername() + ":"
                    + deviceRegistration.getDeviceId() + ":"
                    + moduleId + "'.");
        }
        return moduleRegistration;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleRegistration getMasterModuleRegistration() throws UnipooleException{
        ModuleRegistration moduleRegistration = getModuleRegistration(getMasterDeviceRegistration(), Defaults.MASTER_MODULE_ID);
        if(moduleRegistration == null){
            throw new UnipooleException(MODULE_REGISTRATION, "The Master module registration does not exist.");
        }
        return moduleRegistration;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModuleRegistration getMasterModuleRegistration(String moduleId) throws UnipooleException{
        DeviceRegistration deviceRegistration = getMasterDeviceRegistration();
        ModuleRegistration moduleRegistration = getModuleRegistration(deviceRegistration, moduleId);
        if(moduleRegistration == null){
            throw new UnipooleException(MODULE_REGISTRATION, "Could not find the master module registration for module '"
                    + moduleId + "'.");
        }
        return moduleRegistration;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate(ModuleRegistration moduleRegistration){
        if(moduleRegistration != null){
            moduleRegistration.setActive(false);
            em.merge(moduleRegistration);
        }
    }
    
    //----- ToolVersion -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public ToolVersion createToolVersion(DeviceRegistration deviceRegistration, String toolName, String version) {
        ToolVersion toolVersion = new ToolVersion();
        toolVersion.setDeviceRegistrationId(deviceRegistration.getId());
        toolVersion.setToolName(toolName);
        toolVersion.setToolVersion(version);
        toolVersion.setActive(true);
        try {
            em.persist(toolVersion);
            return toolVersion;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ToolVersion getToolVersion(DeviceRegistration deviceRegistration, String toolName) {
        try {
            Query query = em.createNamedQuery("toolVersionActive");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            return (ToolVersion) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ToolVersion getToolVersion(DeviceRegistration deviceRegistration, String toolName, String toolVersion) {
        try {
            Query query = em.createNamedQuery("toolVersion");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            query.setParameter(ToolVersion.PARAMETER_TOOL_VERSION, toolVersion);
            return (ToolVersion) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ToolVersion getToolVersionWithNullCheck(String username, String deviceId, String toolName) throws UnipooleException{
        return getToolVersionWithNullCheck(getDeviceRegistrationWithNullCheck(username, deviceId), toolName);
    }
    
    /**
     * Retrieve the tool version for the device registration.
     * 
     * @param deviceRegistration The device registration.
     * @param toolName The tool name.
     * @return The tool version.
     */
    private ToolVersion getToolVersionWithNullCheck(DeviceRegistration deviceRegistration, String toolName){
        ToolVersion toolVersion = getToolVersion(deviceRegistration, toolName);
        if(toolVersion == null){
            throw new UnipooleException(TOOL_VERSION, "No tool registration found for '"
                    + deviceRegistration.getUsername() + ":"
                    + deviceRegistration.getDeviceId() + ":" + toolName + "'.");
        }
        return toolVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ToolVersion getMasterToolVersion(String toolName) throws UnipooleException{
        ToolVersion toolVersion = getToolVersion(getMasterDeviceRegistration(), toolName);
        if(toolVersion == null){
            throw new UnipooleException(TOOL_VERSION, "No master tool registration found for tool '"
                    + toolName + "'.");
        }
        return toolVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ToolVersion> getToolVersions(DeviceRegistration deviceRegistration){
        try {
            Query query = em.createNamedQuery("toolVersions");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistration.getId());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ToolVersion> getToolVersionsWithNullCheck(String username, String deviceId){
        return getToolVersions(getDeviceRegistrationWithNullCheck(username, deviceId));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ToolVersion> getMasterToolVersions() throws UnipooleException{
        return getToolVersions(getMasterDeviceRegistration());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ToolVersion> getToolVersions(DeviceRegistration deviceRegistration, String toolName, String fromToolVersion, String toToolVersion) {
        ToolVersion from = getToolVersion(deviceRegistration, toolName, fromToolVersion);
        ToolVersion to = getToolVersion(deviceRegistration, toolName, toToolVersion);
        try {
            Query query = em.createNamedQuery("toolVersionRange");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            //TODO This might not be the best method.
            query.setParameter("idFrom", from.getId());
            query.setParameter("idTo", to.getId());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int deactivateAllToolVersions(Long deviceRegistrationId, String toolName){
        try{
            Query query = em.createNamedQuery("toolVersionDeactivateAll");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, deviceRegistrationId);
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            return query.executeUpdate();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int deactivateAllOther(ToolVersion toolVersion){
        try{
            Query query = em.createNamedQuery("toolVersionDeactivate");
            query.setParameter(DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID, toolVersion.getDeviceRegistrationId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolVersion.getToolName());
            query.setParameter(ToolVersion.PARAMETER_TOOL_VERSION, toolVersion.getToolVersion());
            return query.executeUpdate();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate(ToolVersion toolVersion) {
        if(toolVersion != null){
            toolVersion.setActive(false);
            em.merge(toolVersion);
        }
    }

    //----- ContentVersion -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public ContentVersion createContentVersion(ModuleRegistration moduleRegistration, String toolName, String version) {
        ContentVersion contentVersion = new ContentVersion();
        contentVersion.setModuleRegistrationId(moduleRegistration.getId());
        contentVersion.setToolName(toolName);
        contentVersion.setContentVersion(version);
        contentVersion.setActive(true);
        try {
            em.persist(contentVersion);
            return contentVersion;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ContentVersion getContentVersion(ModuleRegistration moduleRegistration, String toolName) {
        try {
            Query query = em.createNamedQuery("contentVersionActive");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, moduleRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            return (ContentVersion) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentVersion getContentVersion(ModuleRegistration moduleRegistration, String toolName, String contentVersion) {
        try {
            Query query = em.createNamedQuery("contentVersion");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, moduleRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            query.setParameter(ContentVersion.PARAMETER_CONTENT_VERSION, contentVersion);
            return (ContentVersion) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentVersion getContentVersionCloseTo(ModuleRegistration moduleRegistration, String toolName, String contentVersion) {
        try {
            Query query = em.createNamedQuery("contentVersionCloseTo");
            query.setMaxResults(1);
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, moduleRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            query.setParameter(ContentVersion.PARAMETER_CONTENT_VERSION, contentVersion);
            return (ContentVersion) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentVersion getContentVersionWithNullCheck(String username, String deviceId, String moduleId, String toolName) throws UnipooleException{
        ContentVersion contentVersion = getContentVersion(getModuleRegistrationWithNullCheck(username, deviceId, moduleId), toolName);
        if(contentVersion == null){
            throw new UnipooleException(CONTENT_VERSION, "No content registration found for '"
                    + username + ":"
                    + deviceId + ":" + moduleId + ":" + toolName + "'.");
        }
        return contentVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentVersion getMasterContentVersion(String moduleId, String toolName) throws UnipooleException{
        ContentVersion contentVersion = getContentVersion(getMasterModuleRegistration(moduleId), toolName);
        if(contentVersion == null){
            throw new UnipooleException(CONTENT_VERSION, "No master content registration found for module '" + moduleId + "' and tool '"
                    + toolName + "'.");
        }
        return contentVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentVersion> getContentVersions(ModuleRegistration moduleRegistration){
        try {
            Query query = em.createNamedQuery("contentVersions");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, moduleRegistration.getId());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentVersion> getContentVersionsWithNullCheck(String username, String deviceId, String moduleId) {
        return getContentVersions(getModuleRegistrationWithNullCheck(username, deviceId, moduleId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentVersion> getMasterContentVersions(String moduleId) {
        return getContentVersions(getMasterModuleRegistration(moduleId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentVersion> getContentVersions(ModuleRegistration moduleRegistration, String toolName){
        try {
            Query query = em.createNamedQuery("contentVersionsForTool");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, moduleRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentVersion> getContentVersions(ModuleRegistration moduleRegistration, String toolName, String fromContentVersion, String toContentVersion) {
        ContentVersion from = getContentVersion(moduleRegistration, toolName, fromContentVersion);
        ContentVersion to = getContentVersion(moduleRegistration, toolName, toContentVersion);
        try {
            Query query = em.createNamedQuery("contentVersionRange");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, moduleRegistration.getId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            query.setParameter("versionFrom", from==null?"0":from.getContentVersion());
            query.setParameter("versionTo", to.getContentVersion());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int deactivateAllContentVersions(Long moduleRegistrationId, String toolName){
        try{
            Query query = em.createNamedQuery("contentVersionDeactivateAll");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, moduleRegistrationId);
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, toolName);
            return query.executeUpdate();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int deactivateAllOther(ContentVersion contentVersion){
        try{
            Query query = em.createNamedQuery("contentVersionDeactivate");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_REGISTRATION_ID, contentVersion.getModuleRegistrationId());
            query.setParameter(ToolVersion.PARAMETER_TOOL_NAME, contentVersion.getToolName());
            query.setParameter(ContentVersion.PARAMETER_CONTENT_VERSION, contentVersion.getContentVersion());
            return query.executeUpdate();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate(ContentVersion contentVersion) {
        if(contentVersion != null){
            contentVersion.setActive(false);
            em.merge(contentVersion);
        }
    }
    
    //----- CodeRelease -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRelease createCodeRelease(String releaseName, String releaseVersion){
        CodeRelease codeRelease = new CodeRelease();
        codeRelease.setReleaseName(releaseName);
        codeRelease.setReleaseVersion(releaseVersion);
        codeRelease.setReleased();
        codeRelease.setActive(true);
        try {
            em.persist(codeRelease);
            return codeRelease;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRelease getCodeRelease() {
        try {
            Query query = em.createNamedQuery("codeReleaseActive");
            return (CodeRelease) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CodeRelease getCodeRelease(String releaseName, String releaseVersion) {
        try {
            Query query = em.createNamedQuery("codeRelease");
            query.setParameter(CodeRelease.PARAMETER_RELEASE_NAME, releaseName);
            query.setParameter(CodeRelease.PARAMETER_RELEASE_VERSION, releaseVersion);
            return (CodeRelease)query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<CodeRelease> getCodeReleases(String releaseName) {
        try {
            Query query = em.createNamedQuery("codeReleases");
            query.setParameter(CodeRelease.PARAMETER_RELEASE_NAME, releaseName);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate(CodeRelease codeRelease) {
        if(codeRelease != null){
            codeRelease.setActive(false);
            em.merge(codeRelease);
        }
    }

    //----- CodeReleaseVersion -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public CodeReleaseVersion createCodeReleaseVersion(CodeRelease codeRelease, String toolName, String toolVersion) {
        CodeReleaseVersion codeReleaseVersion = new CodeReleaseVersion();
        codeReleaseVersion.setCodeReleaseId(codeRelease.getId());
        codeReleaseVersion.setToolName(toolName);
        codeReleaseVersion.setToolVersion(toolVersion);
        try {
            em.persist(codeReleaseVersion);
            return codeReleaseVersion;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CodeReleaseVersion> getCodeReleaseVersions(CodeRelease codeRelease) {
        try {
            Query query = em.createNamedQuery("codeReleaseVersions");
            query.setParameter(CodeRelease.PARAMETER_CODE_RELEASE_ID, codeRelease.getId());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    //----- ContentRelease -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public ContentRelease createContentRelease(String moduleId, String releaseName, String releaseVersion) {
        ContentRelease contentRelease = new ContentRelease();
        contentRelease.setModuleId(moduleId);
        contentRelease.setReleaseName(releaseName);
        contentRelease.setReleaseVersion(releaseVersion);
        contentRelease.setReleased();
        contentRelease.setActive(true);
        try {
            em.persist(contentRelease);
            return contentRelease;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentRelease getContentRelease(String moduleId) {
        try {
            Query query = em.createNamedQuery("contentReleaseActive");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_ID, moduleId);
            return (ContentRelease) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentRelease getContentRelease(String moduleId, String releaseName, String releaseVersion) {
        try {
            Query query = em.createNamedQuery("contentRelease");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_ID, moduleId);
            query.setParameter(CodeRelease.PARAMETER_RELEASE_NAME, releaseName);
            query.setParameter(CodeRelease.PARAMETER_RELEASE_VERSION, releaseVersion);
            return (ContentRelease)query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentRelease> getContentReleases(String moduleId, String releaseName) {
        try {
            Query query = em.createNamedQuery("contentReleases");
            query.setParameter(ModuleRegistration.PARAMETER_MODULE_ID, moduleId);
            query.setParameter(CodeRelease.PARAMETER_RELEASE_NAME, releaseName);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivate(ContentRelease contentRelease) {
        if(contentRelease != null){
            contentRelease.setActive(false);
            em.merge(contentRelease);
        }
    }

    //----- ContentReleaseVersion -----//
    /**
     * {@inheritDoc}
     */
    @Override
    public ContentReleaseVersion createContentReleaseVersion(ContentRelease contentRelease, String toolName, String toolVersion) {
        ContentReleaseVersion contentReleaseVersion = new ContentReleaseVersion();
        contentReleaseVersion.setContentReleaseId(contentRelease.getId());
        contentReleaseVersion.setToolName(toolName);
        contentReleaseVersion.setContentVersion(toolVersion);
        try {
            em.persist(contentReleaseVersion);
            return contentReleaseVersion;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentReleaseVersion> getContentReleaseVersions(ContentRelease contentRelease) {
        try {
            Query query = em.createNamedQuery("contentReleaseVersions");
            query.setParameter("contentReleaseId", contentRelease.getId());
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<ContentMapping> getContentMapping(String toSiteId, String toolId) {
	 try {
            Query query = em.createNamedQuery("ContentMapping.getMapping");
            query.setParameter(ContentMapping.PARAMETER_SITE_TO_ID, toSiteId);
            query.setParameter(ContentMapping.PARAMETER_TOOL_NAME, toolId);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}
}

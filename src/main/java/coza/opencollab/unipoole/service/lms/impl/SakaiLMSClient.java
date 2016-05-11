package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.service.lms.filter.impl.UnisaModuleFilter;
import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.ModuleService;
import coza.opencollab.unipoole.service.lms.LMSClient;
import coza.opencollab.unipoole.service.lms.LMSParser;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import coza.opencollab.unipoole.service.lms.filter.ModuleFilter;
import coza.opencollab.unipoole.service.util.SessionManager;
import coza.opencollab.unipoole.shared.Module;
import coza.opencollab.unipoole.shared.Tool;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Sakai CLE implementation of a lms client for Unipoole.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class SakaiLMSClient implements LMSClient {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(SakaiLMSClient.class);
    /**
     * The parser for the lms data.
     */
    @Autowired
    private LMSParser parser;
    /*
     * The session manager.
     */
    @Autowired
    private SessionManager sessionManager;
    /*
     * The Sakai module service
     */
    @Autowired
    private ModuleService moduleService;
    /**
     * The Sakai site type for the master site.
     */
    private String sakaiSiteTypeMaster;
    /**
     * The Sakai site type for the group site.
     */
    private String sakaiSiteTypeGroup;
    /**
     * The Sakai site suffix used to specify the new Master site name structure.
     */
    private String masterSuffix;
    /**
     * A filter to remove modules that cannot be handled for master sites
     */
    private ModuleFilter masterModuleFilter;
    /**
     * A filter to remove modules that cannot be handled for group sites
     */
    private ModuleFilter groupModuleFilter;
    /**
     * The converters for tool data.
     */
    private Map<String, ToolContentService> toolDataConverters;

    /**
     * The Sakai site type for the master site.
     */
    public void setSakaiSiteTypeMaster(String sakaiSiteTypeMaster) {
        this.sakaiSiteTypeMaster = sakaiSiteTypeMaster;
    }

    /**
     * The Sakai site type for the group site.
     */
    public void setSakaiSiteTypeGroup(String sakaiSiteTypeGroup) {
        this.sakaiSiteTypeGroup = sakaiSiteTypeGroup;
    }

    /**
     * A filter to remove modules that cannot be handled for master sites
     */
    public void setMasterModuleFilter(ModuleFilter masterModuleFilter) {
        this.masterModuleFilter = masterModuleFilter;
    }

    /**
     * A filter to remove modules that cannot be handled for group sites
     */
    public void setGroupModuleFilter(ModuleFilter groupModuleFilter) {
        this.groupModuleFilter = groupModuleFilter;
    }

    /**
     * The converters for tool data.
     */
    public void setToolDataConverters(Map<String, ToolContentService> toolDataConverters) {
        this.toolDataConverters = toolDataConverters;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String login(String username, String password) {
        return sessionManager.login(username, password);
    }
    
    /**
     * A helper method to login as the administrator.
     * <p>
     * This method might log in to the lms again or just
     * return a valid login.
     */
    private String loginAsAdmin(){
        return sessionManager.loginAsAdmin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean logout(String username) {
        return sessionManager.logout(username);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getUserDetails(String username){
        return sessionManager.getUserDetails(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> getModules(String year, String semester, String moduleCode) {
        return parser.parseModules(getModulesForSiteType(sakaiSiteTypeMaster, moduleCode), new UnisaModuleFilter(year, semester, moduleCode, true));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> getMasterModules(String criteria){
        return parser.parseModules(getModulesForSiteType(sakaiSiteTypeMaster, criteria), masterModuleFilter);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> getGroupModules(String criteria){
        return parser.parseModules(getModulesForSiteType(sakaiSiteTypeGroup, criteria), groupModuleFilter);
    }
    
    /**
     * Get the modules for the criteria.
     */
    private String getModulesForSiteType(String siteTypes, String criteria){
        String sessionId = loginAsAdmin();
        try {
            return moduleService.getSitesForCriteria(sessionId, siteTypes, criteria);
        } catch (Exception e) {
            throw new UnipooleException(LMS_MODULES, "Could not retrieve the modules from Sakai (" + siteTypes + ":" + criteria + ").", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tool> getTools(String moduleId) {
        String sessionId = loginAsAdmin();
        String xml = null;
        try {
            return  parser.parseTools(moduleService.getSitePagesAndTools(sessionId, moduleId));
        } catch (Exception e) {
            throw new UnipooleException(LMS_TOOLS, "Could not retrieve the pages and tool from Sakai (" + moduleId + ").", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegistered(String username, String moduleId) {
        return getRegisteredModuleId(username, moduleId) != null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRegisteredModuleId(String username, String moduleId){
        String sessionId = loginAsAdmin();
        String xml = null;
        try {
            xml = moduleService.getSitesUserCanAccess(sessionId, username, sakaiSiteTypeGroup);
        } catch (Exception e) {
            throw new UnipooleException(LMS_MODULES, "Could not retrieve the modules from Sakai for user (" + username + ").", e);
        }
        List<Module> modules = parser.parseModules(xml);
        for(Module module: modules){
            if(module.getId().startsWith(moduleId.replace(masterSuffix, ""))){
                return module.getId();
            }
        }
        return null;
    }
    
        /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> getSitesUserCanAccess(String username){
        String sessionId = loginAsAdmin();
        String xml = null;
        try {
            xml = moduleService.getSitesUserCanAccess(sessionId, username, sakaiSiteTypeGroup);
        } catch (Exception e) {
            throw new UnipooleException(LMS_MODULES, "Could not retrieve the modules from Sakai for user (" + username + ").", e);
        }
        return parser.parseModules(xml);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getToolContent(String moduleId, String toolName, Date fromDate) {
        //get tool content converter
        ToolContentService converter = toolDataConverters.get(toolName);
        if(converter == null){
            LOG.warn("There is no converter for the tool (" + toolName + ").");
            return null;
        }
        return converter.getContent(loginAsAdmin(), moduleId, fromDate);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUserContent(String username, String moduleId, String toolName, Date fromDate){
        //get tool content converter
        ToolContentService converter = toolDataConverters.get(toolName);
        if(converter == null){
            LOG.warn("No converter for tool (" + toolName + ").");
            return null;
        }
        return converter.getUserContent(loginAsAdmin(), username, moduleId, fromDate);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ?> updateUserContent(String username, String moduleId, String toolName, Map<String, ?> content, String originalContent){
        //get tool content converter
        ToolContentService converter = toolDataConverters.get(toolName);
        if(converter == null){
            LOG.warn("No converter for tool (" + toolName + ").");
            return null;
        }
        return converter.updateUserContent(loginAsAdmin(), username, moduleId, content, originalContent);
    }

    public String getMasterSuffix() {
        return masterSuffix;
    }

    public void setMasterSuffix(String masterSuffix) {
        this.masterSuffix = masterSuffix;
    }
}

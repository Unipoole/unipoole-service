package coza.opencollab.unipoole.service.util.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.LoginService;
import static coza.opencollab.unipoole.service.ErrorCodes.LMS_CONTENT;
import static coza.opencollab.unipoole.service.ErrorCodes.LMS_LOGIN;
import coza.opencollab.unipoole.service.lms.LMSParser;
import coza.opencollab.unipoole.service.util.CacheManager;
import coza.opencollab.unipoole.service.util.SessionManager;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of session manager that caches session ids
 * so that the login call to the service is not called unnecessarily.
 * 
 * The cache manager is used so it that is cluster aware then this is.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class ClusterSessionManager implements SessionManager{
    /**
     * The CacheManager.
     */
    @Autowired
    private CacheManager cacheManager;
    /**
     * The cache name to use for the sessions.
     */
    private String cacheName;
    /*
     * The Sakai login service
     */
    @Autowired
    private LoginService loginService;
    /**
     * The parser for the lms data.
     */
    @Autowired
    private LMSParser parser;
    /**
     * The admin username.
     */
    private String adminUsername;
    /**
     * The admin password.
     */
    private String adminPassword;

    /**
     * The CacheManager.
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * The cache name to use for the sessions.
     */
    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    /*
     * The Sakai login service
     */
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * The parser for the lms data.
     */
    public void setParser(LMSParser parser) {
        this.parser = parser;
    }

    /**
     * The admin username.
     */
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    /**
     * The admin password.
     */
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
    
    /**
     * Get a session.
     */
    private String getSession(String username, String password) {
        String sessionId = cacheManager.get(username);
        if (sessionId == null) {
            return null;
        }
        String cachePassword = cacheManager.get(username+"-password");
        if(cachePassword == null){
            return null;
        }
        if (cachePassword.equals(password)) {
            return sessionId;
        } else {
            throw new UnipooleException(LMS_LOGIN, "Could not login the user with the given credentials.");
        }
    }
    
    /**
     * Adds a session. 
     */
    private void addSession(String username, String password, String sessionId) {
        cacheManager.put(username, sessionId);
        cacheManager.put(username+"-password", password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String login(String username, String password) {
        try {
            cacheManager.lock(username);
            String sessionId = getSession(username, password);
            if (sessionId != null) {
                return sessionId;
            }
            sessionId = parser.parseSessionId(loginService.login(username, password));
            if (sessionId != null) {
                addSession(username, password, sessionId);
            }
            return sessionId;
        } catch (Exception e) {
            throw new UnipooleException(LMS_LOGIN, "Could not login to myUnisa for user " + username + ".", e);
        }finally{
            cacheManager.unlock(username);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String loginAsAdmin() {
        return login(adminUsername, adminPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean logout(String username) {
        if(username == null){
            return true;
        }
        cacheManager.lock(username);
        try {
            String sessionId = cacheManager.remove(username);
            if(sessionId == null){
                return true;
            }else{
                return loginService.logout(sessionId);
            }
        } catch (Exception e) {
            return false;
        }finally{
            cacheManager.unlock(username);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getUserDetails(String username) {
        String sessionId = loginAsAdmin();
        try {
            return parser.parseUserDetails(loginService.getUserDetails(sessionId, username));
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve the user details from myUnisa for " + username + ".", e);
        }
    }
}

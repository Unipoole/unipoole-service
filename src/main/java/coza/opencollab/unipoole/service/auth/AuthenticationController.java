package coza.opencollab.unipoole.service.auth;

import static coza.opencollab.unipoole.util.JsonParser.*;
import coza.opencollab.unipoole.service.BaseController;
import coza.opencollab.unipoole.service.auth.so.AllowedSites;
import coza.opencollab.unipoole.service.auth.so.Login;
import coza.opencollab.unipoole.service.auth.so.Register;
import coza.opencollab.unipoole.service.lms.LMSClient;
import coza.opencollab.unipoole.service.synch.so.SynchStatus;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller for authentication. This handle logins (to sakai) and
 * registrations. This class handle RestFull service calls using JSON.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
@Controller
@RequestMapping("/service-auth")
public class AuthenticationController extends BaseController{

    /**
     * The implementation is injected by Spring
     */
    @Autowired
    private AuthenticationService authenticationService;
    
        /**
     * The implementation is injected by Spring
     */
    @Autowired
    private LMSClient lmsClient;

    /**
     * Login the given user to sakai. Please is loginPost, better since
     * the password is not passed in the url.
     *
     * @param username The username
     * @param password The password
     * @return The Login object given a status and a sessionId of successful.
     * @deprecated This method must not be used and will be removed
     */
    @RequestMapping(value = "/login/{username}/{password:.+}", method = RequestMethod.GET)
    public @ResponseBody Login loginGet(@PathVariable String username, @PathVariable String password) {
        Login login = authenticationService.login(username, password);
        addDeprecateMessage("Authentication.loginGet", login);
        return login;
    }

    /**
     * Login the given user to sakai. The password is passed in the body under a
     * 'password' value.
     *
     * @param body The content of the call
     * @param username The username
     * @return The Login object given a status and a sessionId of successful.
     */
    @RequestMapping(value = "/login/{username}", method = RequestMethod.POST)
    public @ResponseBody Login loginPost(@RequestBody String body, @PathVariable String username) {
        String password = getPassword(body);
        return authenticationService.login(username, password);
    }

    /**
     * Register the given user for the module. The user will first be logged
     * in to sakai and then registered.
     * The password is passed in the body under a 'password' value.
     * The deviceId is passed in the body under a 'deviceId' value.
     * 
     * @param body The call's json body
     * @param username The username
     * @param moduleId The module id
     * @param clientVersion The client version
     * @return The Register object given a status.
     */
    @RequestMapping(value = "/register/{username}", method = RequestMethod.POST)
    public @ResponseBody Register registerA(@RequestBody String body, @PathVariable String username) {
        String password = getPassword(body);
        SynchStatus synchStatus = parseJson(body, SynchStatus.class);
        return authenticationService.register(username, password, synchStatus);
    }
    
    /**
     * Returns the List of sites that the user is registered on
     * 
     * @param username The username
     * @return The List of allowed sites
     */
    @RequestMapping(value = "/allowedSites/{username}", method = RequestMethod.GET)
    public @ResponseBody AllowedSites allowedSites(@PathVariable String username) {
        AllowedSites allowedSites = new AllowedSites();
        allowedSites.setModules(lmsClient.getSitesUserCanAccess(username));
        return allowedSites;
    }
    
    
    /**
      * Get the password from a request. The password may be in the form of a password string, or an encrypted
     * auth token which will be decrypted by the authentication service
     * @param content Content map of a request
     * @return The password (possibly decrypted from an auth token)
     */
    private String getPassword(String body){
    	String authToken = parseJson(body, "authToken");
    	if(authToken != null){
    		return authenticationService.decryptPassword(authToken);
    	}
    	else{
    		return (String) parseJson(body, "password");
    	}
    }
}

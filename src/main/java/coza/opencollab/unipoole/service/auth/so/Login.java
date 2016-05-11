package coza.opencollab.unipoole.service.auth.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import java.io.Serializable;

/**
 * The service object for login.
 * This may be used by clients and other services
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class Login extends ServiceObject implements Serializable{
    /**
     * The username.
     */
    private String username;
    /**
     * The session id of the sakai login
     */
    private String sessionId;
    
    /**
     * Authentication token that can be used later
     * instead of using a password.
     */
    private String authToken;
    
    /**
     * Default Constructor.
     */
    public Login(){}
    
    /**
     * Constructor setting the Status Code.
     */
    public Login(ServiceCallStatus statusCode){
        super(statusCode);
    }
    
    /**
     * Copy constructor.
     */
    public Login(ServiceObject other){
        super(other);
    }

    /**
     * The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * The username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The session id of the sakai login
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * The session id of the sakai login
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

	/**
	 * Get the authentication token that can be used later
     * instead of using a password.
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * Set the authentication token that can be used later
     * instead of using a password.
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
    
    
}

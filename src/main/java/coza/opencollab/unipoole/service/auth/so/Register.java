package coza.opencollab.unipoole.service.auth.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import java.io.Serializable;
import java.util.Map;

/**
 * The service object for client registrations
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class Register extends ServiceObject implements Serializable{
    /**
     * The registered module id
     */
    private String moduleId;
    /**
     * Extra user details.
     */
    private Map<String, String> userDetails;
    
    /**
     * Default constructor
     */
    public Register(){}
    
    /**
     * Constructor setting the status.
     */
    public Register(ServiceCallStatus statusCode){
        super(statusCode);
    }
    
    /**
     * Copy constructor
     */
    public Register(ServiceObject other){
        super(other);
    }

    /**
     * The registered module id
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * The registered module id
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * Extra user details.
     */
    public Map<String, String> getUserDetails() {
        return userDetails;
    }

    /**
     * Extra user details.
     */
    public void setUserDetails(Map<String, String> userDetails) {
        this.userDetails = userDetails;
    }
}

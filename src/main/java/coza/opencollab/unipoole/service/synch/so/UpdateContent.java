package coza.opencollab.unipoole.service.synch.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import java.util.Map;

/**
 * The status after a client update content.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class UpdateContent extends ServiceObject{
    /**
     * A content data object for return content to the client
     * after a user content update.
     */
    private Map responseContent;
    
    /**
     * Default constructor
     */
    public UpdateContent(){}
    
    /**
     * Constructor setting the status.
     */
    public UpdateContent(ServiceCallStatus statusCode){
        super(statusCode);
    }
    
    /**
     * Copy constructor
     */
    public UpdateContent(ServiceObject other){
        super(other);
    }

    /**
     * A content data object for return content to the client
     * after a user content update.
     */
    public Map getResponseContent() {
        return responseContent;
    }

    /**
     * A content data object for return content to the client
     * after a user content update.
     */
    public void setResponseContent(Map responseContent) {
        this.responseContent = responseContent;
    }
}

package coza.opencollab.unipoole.service.creator.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import java.io.Serializable;

/**
 * The result of code and creator actions.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class CreatorResponse extends ServiceObject implements Serializable{
    /**
     * The description of the action taken.
     */
    private String description;

    /**
     * Default constructor
     */
    public CreatorResponse(){}
    
    /**
     * Constructor setting the status.
     */
    public CreatorResponse(ServiceCallStatus status){
        super(status);
    }
    
    /**
     * Copy constructor
     */
    public CreatorResponse(ServiceObject other){
        super(other);
    }
    
    /**
     * The description of the action taken.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The description of the action taken.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}

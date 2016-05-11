package coza.opencollab.unipoole.service.synch.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;

/**
 * The status after a client update.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class UpdateStatus extends ServiceObject{
    
    /**
     * Default constructor
     */
    public UpdateStatus(){}
    
    /**
     * Constructor setting the status.
     */
    public UpdateStatus(ServiceCallStatus statusCode){
        super(statusCode);
    }
    
    /**
     * Copy constructor
     */
    public UpdateStatus(ServiceObject other){
        super(other);
    }
}

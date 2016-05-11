package coza.opencollab.unipoole.service.auth.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import coza.opencollab.unipoole.shared.Module;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The service object for client allowed sites
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class AllowedSites extends ServiceObject implements Serializable{
    
    /**
     * The list of sites that is allowed 
     */
    private List<Module> modules;
    
    /**
     * Default constructor
     */
    public AllowedSites(){}
    
    /**
     * Constructor setting the status.
     */
    public AllowedSites(ServiceCallStatus statusCode){
        super(statusCode);
    }
    
    /**
     * Copy constructor
     */
    public AllowedSites(ServiceObject other){
        super(other);
    }
    
    /**
     * Adds a single Module to the list of allowed modules
     * 
     * @param module 
     */
    public void addModule(Module module) {
        if (modules == null) {
            modules = new ArrayList();
        }
        modules.add(module);
    }

    /**
     * @return the modules
     */
    public List<Module> getModules() {
        return modules;
    }

    /**
     * @param modules the modules to set
     */
    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

}

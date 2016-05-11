package coza.opencollab.unipoole.service.lms.converter;

import coza.opencollab.unipoole.shared.Module;

/**
 * This is a class to handle the different ways lms's work with 
 * module and convert the lms data.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface ModuleConverter {
    /**
     * This method should return a well formed module
     * from the data given.
     * 
     * @param moduleData The data from the lms.
     * @return The module as described in Unipoole.
     */
    public Module getModule(Object moduleData);
    
}

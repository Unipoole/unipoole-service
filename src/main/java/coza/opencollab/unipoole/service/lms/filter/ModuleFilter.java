package coza.opencollab.unipoole.service.lms.filter;

import coza.opencollab.unipoole.shared.Module;
import java.util.List;

/**
 * A filter class for <code>Module</code>.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface ModuleFilter {
    /**
     * Checks whether a module is allowed.
     * 
     * @param module The module to check
     * @return true if the module is allowed, otherwise false.
     */
    public boolean allow(Module module);
    /**
     * Filter the list, removing all the modules that is not allowed.
     * 
     * @param modules List of modules to filter.
     */
    public void filter(List<Module> modules);
}

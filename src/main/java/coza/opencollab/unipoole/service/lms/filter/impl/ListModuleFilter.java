package coza.opencollab.unipoole.service.lms.filter.impl;

import coza.opencollab.unipoole.service.lms.filter.ModuleFilter;
import coza.opencollab.unipoole.shared.Module;
import java.util.List;

/**
 * A module filter that contains a list of allowed modules.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class ListModuleFilter extends AllowAllModuleFilter implements ModuleFilter{
    /**
     * The list of module IDs.
     */
    private List<String> moduleIds;

    /**
     * The list of module Ids.
     */
    public void setModuleIds(List<String> moduleIds) {
        this.moduleIds = moduleIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allow(Module module) {
        return moduleIds.contains(module.getId());
    }
}

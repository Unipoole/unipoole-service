package coza.opencollab.unipoole.service.lms.filter.impl;

import coza.opencollab.unipoole.service.lms.filter.ToolFilter;
import coza.opencollab.unipoole.shared.Tool;
import java.util.List;

/**
 * A tool filter that contains a list of allowed tools.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class ListToolFilter extends AllowAllToolFilter implements ToolFilter{
    /**
     * The list of tool names.
     */
    private List<String> toolNames;

    /**
     * The list of tool names.
     */
    public void setToolNames(List<String> toolNames) {
        this.toolNames = toolNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allow(Tool tool) {
        return toolNames.contains(tool.getName());
    }
}

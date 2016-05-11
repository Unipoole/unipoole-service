package coza.opencollab.unipoole.service.lms.filter.impl;

import coza.opencollab.unipoole.service.lms.filter.ToolFilter;
import coza.opencollab.unipoole.shared.Tool;
import java.util.Map;

/**
 * A tool filter that contains a map of allowed tool names and titles.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class NameAndTitleToolFilter extends AllowAllToolFilter implements ToolFilter{
    /**
     * The map of tool names and titles.
     */
    private Map<String, String> toolNameAndTitles;

    /**
     * The map of tool names and titles.
     */
    public void setToolNameAndTitles(Map<String, String> toolNameAndTitles) {
        this.toolNameAndTitles = toolNameAndTitles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allow(Tool tool) {
        if(!toolNameAndTitles.containsKey(tool.getName())){
            return false;
        }
        return toolNameAndTitles.get(tool.getName()).equals(tool.getTitle());
    }

}

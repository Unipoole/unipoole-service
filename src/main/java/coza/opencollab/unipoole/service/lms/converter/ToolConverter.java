package coza.opencollab.unipoole.service.lms.converter;

import coza.opencollab.unipoole.shared.Tool;
import java.util.List;

/**
 * This is a class to handle the different ways lms's work with 
 * tools and convert the data to a tool.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface ToolConverter {
    /**
     * This method should return a well formed tool
     * from the data given.
     * <p>
     * If is possible to return multiple tools.
     * 
     * @param toolData The data from the lms.
     * @return The tool(s) as described in Unipoole.
     */
    public List<Tool> getTool(Object toolData);
}

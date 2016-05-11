package coza.opencollab.unipoole.service.lms.converter.impl;

import static coza.opencollab.unipoole.service.ErrorCodes.XML_PARSING;
import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.lms.converter.ToolConverter;
import coza.opencollab.unipoole.shared.Tool;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.Element;

/**
 * The Sakai implementation of the tool converter.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class SakaiToolConverter implements ToolConverter {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(SakaiToolConverter.class);

    /**
     * {@inheritDoc}
     * This method except a 'page' Element as parameter.
     */
    @Override
    public List<Tool> getTool(Object toolData) {
        Element page = (Element)toolData;
        List<Tool> tools = new ArrayList<Tool>();
        Tool tool = new Tool();
        tool.setTitle(page.elementTextTrim("page-title"));
        Element toolsElement = page.element("tools");
        if (toolsElement == null) {
            throw new UnipooleException(XML_PARSING, "No tools on a Sakai Pages (" + tool.getTitle() + ").");
        }
        List<Element> toolElements = toolsElement.elements("tool");
        if (toolElements == null || toolElements.isEmpty()) {
            throw new UnipooleException(XML_PARSING, "No tools on a Sakai Pages (" + tool.getTitle() + ").");
        }
        if(toolElements.size() == 1){
            Element toolElement = (Element) toolElements.get(0);
            tool.setId(toolElement.attributeValue("id"));
            tool.setName(toolElement.elementTextTrim("tool-id"));
        }else if (toolElements.size() > 1 && "Home".equals(tool.getTitle())) {
            tool.setId(page.attributeValue("id"));
            tool.setName("sakai.home");
            for(Element toolElement: toolElements){
                Tool t = new Tool();
                t.setId(toolElement.attributeValue("id"));
                t.setTitle(null);
                t.setName(toolElement.elementTextTrim("tool-id"));
                t.setOnMenu(false);
                tools.add(t);
            }
        } else {
            LOG.info("There is a multi-tool page we don't know about: " + tool.getTitle());
        }
        tools.add(tool);
        return tools;
    }
}

package coza.opencollab.unipoole.service.lms.impl;

import static coza.opencollab.unipoole.service.util.impl.DomHelper.*;
import coza.opencollab.unipoole.service.lms.LMSParser;
import coza.opencollab.unipoole.service.lms.converter.ModuleConverter;
import coza.opencollab.unipoole.service.lms.converter.ToolConverter;
import coza.opencollab.unipoole.service.lms.converter.impl.SakaiModuleConverter;
import coza.opencollab.unipoole.service.lms.converter.impl.SakaiToolConverter;
import coza.opencollab.unipoole.service.lms.filter.ModuleFilter;
import coza.opencollab.unipoole.service.lms.filter.ToolFilter;
import coza.opencollab.unipoole.service.lms.filter.impl.AllowAllModuleFilter;
import coza.opencollab.unipoole.service.lms.filter.impl.AllowAllToolFilter;
import coza.opencollab.unipoole.shared.Module;
import coza.opencollab.unipoole.shared.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * The Sakai implementation of the parser.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class SakaiLMSParser implements LMSParser{
    /**
     * The module converter that will retrieve the modules from the data.
     */
    private ModuleConverter moduleConverter = new SakaiModuleConverter();
    /**
     * The tool converter that will retrieve the tools from the data.
     */
    private ToolConverter toolConverter = new SakaiToolConverter();
    /**
     * A filter to remove modules that cannot be handled
     */
    private ModuleFilter moduleFilter = new AllowAllModuleFilter();
    /**
     * A filter to remove tools that cannot be handled
     */
    private ToolFilter toolFilter = new AllowAllToolFilter();

    /**
     * The module handler that will retrieve the modules from the data.
     */
    public void setModuleConverter(ModuleConverter moduleConverter) {
        this.moduleConverter = moduleConverter;
    }

    /**
     * The tool handler that will retrieve the tools from the data.
     */
    public void setToolConverter(ToolConverter toolConverter) {
        this.toolConverter = toolConverter;
    }

    /**
     * A filter to remove modules that cannot be handled
     */
    public void setModuleFilter(ModuleFilter moduleFilter) {
        this.moduleFilter = moduleFilter;
    }

    /**
     * A filter to remove tools that cannot be handled
     */
    public void setToolFilter(ToolFilter toolFilter) {
        this.toolFilter = toolFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parseSessionId(String data) {
        return data;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> parseUserDetails(String data){
        Map<String, String> map = new HashMap<String, String>();
        Document doc = createDocument(data);
        Element user = doc.getRootElement();
        List<Element> elements = user.elements();
        for(Element element: elements){
            map.put(element.getName(), element.getTextTrim());
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> parseModules(String data) {
        return parseModules(data, moduleFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Module> parseModules(String data, ModuleFilter moduleFilter) {
        Document doc = createDocument(data);
        List<Element> sites = getElements(doc, "/sites/site");
        List<Module> modules = new ArrayList<Module>();
        if (sites != null) {
            for (Element site: sites) {
                Module module = moduleConverter.getModule(site);
                if (moduleFilter == null || moduleFilter.allow(module)) {
                    modules.add(module);
                }
            }
        }
        return modules;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tool> parseTools(String data) {
        Document doc = createDocument(data);
        List<Element> pages = getElements(doc, "/site/pages/page");
        List<Tool> tools = new ArrayList<Tool>();
        if (pages != null) {
            for (Element page: pages) {
                List<Tool> toolList = toolConverter.getTool(page);
                for(Tool tool: toolList){
                    if (toolFilter.allow(tool)) {
                        tools.add(tool);
                    }
                }
            }
        }
        return tools;
    }
}

package coza.opencollab.unipoole.service.lms.converter.impl;

import coza.opencollab.unipoole.service.lms.converter.ModuleConverter;
import coza.opencollab.unipoole.shared.Module;
import org.dom4j.Element;

/**
 * The Sakai implementation of the module converter.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class SakaiModuleConverter implements ModuleConverter {

    /**
     * {@inheritDoc} This method except a 'site' Element as parameter.
     */
    @Override
    public Module getModule(Object moduleData) {
        Element site = (Element) moduleData;
        Module module = new Module();
        module.setId(site.elementTextTrim("id"));
        module.setName(site.elementTextTrim("title"));
        module.setDescription(site.elementTextTrim("shortDescription"));
        module.setCreatedDate(site.elementTextTrim("createdDate"));
        return module;
    }
}

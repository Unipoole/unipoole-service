package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.MeleteService;
import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.*;
import coza.opencollab.unipoole.shared.MeletePart;
import coza.opencollab.unipoole.shared.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author OpenCollab
 */
public class SakaiMeleteHandler implements ToolContentService {
    /*
     * The Sakai melete  content for a  module
     */
    @Autowired
    private MeleteService meleteService;
    /*
     * The Sakai resources for a module
     */
    @Autowired
    private SakaiResourceHandler resourcesHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getContent(String sessionId, String moduleId, Date fromDate) {
        try {
            Document document = createDocument(meleteService.getLearningModuleForSite(sessionId, moduleId, fromDate));
            return getMeleteParts(sessionId, document);
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve the content.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUserContent(String sessionId, String username, String moduleId, Date fromDate) {
        return null;
    }

    /**
     * When given a String containing XML, create the XML document and parse it
     *
     * @return a map of MeleteModules
     */
    public Map<String, MeletePart> getMeleteParts(String sessionId, Document document) {
        Map<String, MeletePart> meleteParts = new HashMap<String, MeletePart>();
        //first sections then modules since the modules need to update the parent ids of the sections
        meleteParts.putAll(getSections(sessionId, document));
        meleteParts.putAll(getModules(document, meleteParts));
        return meleteParts;
    }

    private Map<String, MeletePart> getModules(Document document, Map<String, MeletePart> meleteSections) {
        Map<String, MeletePart> meleteModules = new HashMap<String, MeletePart>();
        List<Element> modules = getElements(document, "/list/module");
        if (modules == null) {
            return meleteModules;
        }
        int moduleIndex = 1;
        for (Element module : modules) {
            MeletePart part = parseMeleteModule(module, meleteSections, moduleIndex++);
            meleteModules.put(part.getId(), part);
        }
        return meleteModules;
    }

    private Map<String, MeletePart> getSections(String sessionId, Document document) {
        Map<String, MeletePart> meleteSections = new HashMap<String, MeletePart>();
        List<Element> sections = getElements(document, "/list/section");
        if (sections == null) {
            return meleteSections;
        }
        for (Element section : sections) {
            MeletePart part = parseSection(sessionId, section);
            meleteSections.put(part.getId(), part);
        }
        return meleteSections;
    }

    /**
     * Receives a node containing the melete module data. Generate the java objects ior the Modules and sections
     *
     * @param nod
     * @return
     */
    public MeletePart parseMeleteModule(Element element, Map<String, MeletePart> meleteSections, int moduleIndex) {
        MeletePart module = new MeletePart();
        module.setId(element.attributeValue("id"));
        module.setTitle(element.attributeValue("title"));
        module.setDescription(element.attributeValue("description"));
        module.setVersion(element.attributeValue("version"));
        module.setKeywords(element.attributeValue("keywords"));
        module.setCreationDate(getAttributeAsDate(element, "creation-date"));
        module.setUserId(element.attributeValue("user-id"));
        module.setModifyUserId(element.attributeValue("modify-user-id"));
        module.setModificationDate(getAttributeAsDate(element, "modification-date"));
        module.setOpen(getAttributeAsDate(element, "open"));
        module.setDue(getAttributeAsDate(element, "due"));
        module.setAllowUntil(getAttributeAsDate(element, "allow-until"));
        module.setWhatsNext(element.attributeValue("whats-next"));
        module.setSortedId(String.valueOf(moduleIndex));
        Element seq = getElement(element, "seq-xml");
        String seqText = seq.getText();
        Document seqDoc = createDocument(seqText);
        List<Element> sections = getElements(seqDoc, "//section/section");
        List<String> seqList = new ArrayList<String>();
        for (Element section : sections) {
            MeletePart part = meleteSections.get(section.attributeValue("id"));
            part.setParentId(section.getParent().attributeValue("id"));
            seqList.add(section.attributeValue("id"));
        }
        
        // Adding the sorted hierarchy id's according to the seqXML
        List<Element> seqSections = getElements(seqDoc, "//section");
        int sectionIndex = 1;
        for (Element section : seqSections) {
            String pId = section.getParent().attributeValue("id");
            MeletePart part = meleteSections.get(section.attributeValue("id"));
            if (pId == null) {
                part.setSortedParentId(String.valueOf(moduleIndex));
                part.setSortedId(moduleIndex + "." + sectionIndex);
                sectionIndex++;
            }else{
                MeletePart parentPart = meleteSections.get(pId);
                part.setSortedParentId(parentPart.getSortedId());
                part.setSortedId(parentPart.getSortedId() + "." + parentPart.getNextChildId());
            }                      
        }
        module.setSeqList(seqList);
        return module;
    }

    /**
     *
     */
    public MeletePart parseSection(String sessionId, Element element) {
        MeletePart mSection = new MeletePart();
        mSection.setId(element.attributeValue("id"));
        mSection.setTitle(element.attributeValue("title"));
        mSection.setAllowUntil(getAttributeAsDate(element, "allow-until"));
        mSection.setContentType(element.attributeValue("content-type"));
        mSection.setVersion(element.attributeValue("version"));
        mSection.setInstructor(element.attributeValue("instructor"));
        mSection.setCreationDate(getAttributeAsDate(element, "creation-date"));
        mSection.setModificationDate(getAttributeAsDate(element, "modification-date"));
        mSection.setUserId(element.attributeValue("user-id"));
        mSection.setModifyUserId(element.attributeValue("modify-user-id"));
        mSection.setParentId(element.attributeValue("parent-module-id"));
        List<Element> resources = getElements(element, "resource");
        for (Element res : resources) {
            Resource resource = new Resource();
            resource.setId(getAttribute(res, "id"));
            resource.setParentId(getAttribute(res, "parent-id"));
            resource.setName(getAttribute(res, "name"));
            resource.setDescription(getAttribute(res, "description"));
            resource.setMimeType(getAttribute(res, "type"));
            resource.setCollection(getAttributeAsBoolean(res, "is-collection"));
            resource.setCreated(getAttributeAsDate(res, "created"));
            resource.setCreatedBy(getAttribute(res, "created-by"));
            resource.setLastChanged(getAttributeAsDate(res, "last-changed"));
            resource.setLastChangedBy(getAttribute(res, "last-changed-by"));
            resource.setSize(getAttributeAsInt(res, "size"));
            resource.setUrl(getAttribute(res, "url"));
            if("typeEditor".equals(mSection.getContentType())){
                Element content = getElement(res, "content");
                resource.setContent(Base64.decodeBase64(content.getText()));
            }else if("typeUpload".equals(mSection.getContentType())){
                resource.setContent(resourcesHandler.getResourceData(sessionId, resource.getId()));
            }
            mSection.setResource(resource);
        }
        return mSection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map updateUserContent(String sessionId, String username, String moduleId, Map content, String originalContent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

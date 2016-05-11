package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.FaqService;
import static coza.opencollab.unipoole.service.ErrorCodes.LMS_CONTENT;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.*;
import coza.opencollab.unipoole.shared.Faq;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author OpenCollab
 */
public class SakaiFaqHandler implements ToolContentService {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(SakaiFaqHandler.class);
    /*
     * The Sakai faq for a  module
     */

    @Autowired
    private FaqService faqService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getContent(String sessionId, String moduleId, Date fromDate) {
        try {
            Document document = createDocument(faqService.getFaqForSite(moduleId, fromDate));
            if(LOG.isDebugEnabled()){
                LOG.debug("Faq from Sakai: " + document.asXML());
            }
            return getFaqs(document);
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve the faq data from Sakai (" + moduleId + ").", e);
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
     * Retrieve the Faq data from the xml retrieved from Sakai.
     *
     * @return List of faqs
     */
    public List<Faq> getFaqs(Document document) {
        List<Faq> faqs = new ArrayList<Faq>();
        List<Element> elements = getElements(document, "/list/faq");
        for (Element element : elements) {
            faqs.add(getFaq(element));
        }
        return faqs;
    }

    /**
     * getFaqCategories receives a XML element which contains a faq category. It then parses the element and returns an
     * faq object.
     *
     * @param faqData XML node
     * @return List of Faq's
     */
    private Faq getFaq(Element faqElement) {
        Faq faq = new Faq();
        faq.setId(getAttributeAsInt(faqElement, "id"));
        faq.setDescription(getAttribute(faqElement, "description"));
        faq.setModifiedOn(getAttributeAsDate(faqElement, "modified-on"));
        faq.setParentId(getAttributeAsInt(faqElement, "parentId"));
        faq.setQuestion(getAttribute(faqElement, "question"));
        faq.setAnswer(getAttribute(faqElement, "answer"));
        return faq;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map updateUserContent(String sessionId, String username, String moduleId, Map content, String originalContent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

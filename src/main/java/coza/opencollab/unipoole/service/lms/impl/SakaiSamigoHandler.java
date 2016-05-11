package coza.opencollab.unipoole.service.lms.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.SamigoService;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import static coza.opencollab.unipoole.service.ErrorCodes.LMS_CONTENT;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.*;
import coza.opencollab.unipoole.shared.Samigo;
import coza.opencollab.unipoole.shared.SamigoAvailable;
import coza.opencollab.unipoole.shared.SamigoScored;
import coza.opencollab.unipoole.shared.SamigoScores;
import org.apache.log4j.Logger;
import org.dom4j.Element;

public class SakaiSamigoHandler implements ToolContentService {

    private String sakaiContentBasePortlToolUrl;
    private String pageTitle;

    public String getSakaiContentBasePortlToolUrl() {
        return sakaiContentBasePortlToolUrl;
    }

    public void setSakaiContentBasePortlToolUrl(String sakaiContentBasePortlToolUrl) {
        this.sakaiContentBasePortlToolUrl = sakaiContentBasePortlToolUrl;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
    private static final Logger LOG = Logger.getLogger(SakaiSamigoHandler.class);
    @Autowired
    private SamigoService samigoService;

    @Override
    public Object getContent(String sessionId, String moduleId, Date fromDate) {
        return null;
    }

    @Override
    public Object getUserContent(String sessionId, String username, String moduleId, Date fromDate) {
        try {
            Document document = createDocument(samigoService.getAvailableAssignments(sessionId, username, moduleId, fromDate, pageTitle));
            return getAssignments(document, moduleId);
        } catch (Exception e) {
            LOG.error(LMS_CONTENT + " : " + "Could not retrieve the Samigo data from Sakai (" + moduleId + ").", e);
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve the Samigo data from Sakai (" + moduleId + ").", e);
        }
    }

    /**
     * Not used yet. This would handle all data which needs to be sent back to sakai
     * @param sessionId
     * @param username
     * @param moduleId
     * @param content
     * @param originalContent
     * @return 
     */
    @Override
    public Map updateUserContent(String sessionId, String username, String moduleId, Map content, String originalContent) {
        return null;
    }

    /**
     * Get assignments,  receives a document and the moduleId, it then parses the XML document and retrieve 
     * values which it inserts into our shared objects. which will be formatted to our structured json.
     * @param document
     * @param moduleId
     * @return 
     */
    public Samigo getAssignments(Document document, String moduleId) {
        Samigo samigo = new Samigo();
        List<SamigoAvailable> availableList = new ArrayList<SamigoAvailable>();
        List<SamigoScored> scoredList = new ArrayList<SamigoScored>();
        String toolId = "";
        String expression = "/list/assessment_tool_url";

        Element ele = getElement(document, expression);
        toolId = ele.getText();
        samigo.setTestUrl(this.sakaiContentBasePortlToolUrl + toolId + "/jsf/index/mainIndex#");
        // Available assignements
        List<Element> xmlAssignments = getElements(document, "/list/available_assesments/assignment");
        for (Element xmlAssignment : xmlAssignments) {
            SamigoAvailable available = new SamigoAvailable();
            available.setId(getAttribute(xmlAssignment, "id"));
            available.setTitle(getAttribute(xmlAssignment, "title"));
            available.setTimeLimit(getAttribute(xmlAssignment, "time-limit"));
            String dueDate = null;
            try{
                dueDate = getAttribute(xmlAssignment, "due-date");
                available.setDateDue(new Date(Long.parseLong(dueDate)));
            }catch(Exception ex){
                available.setDateDue(null);
            }
            availableList.add(available);
        }
        samigo.setAvailable(availableList);
        
        // Scored assignments
        List<Element> xmlSubmittedAssignments = getElements(document, "/list/scored/assignment");
        for (Element xmlsubmittedAssignment : xmlSubmittedAssignments) {
            SamigoScored scored = new SamigoScored();
            scored.setId(getAttribute(xmlsubmittedAssignment, "id"));
            scored.setTitle(getAttribute(xmlsubmittedAssignment, "title"));
            scored.setFeedbackAvailable(getAttribute(xmlsubmittedAssignment, "feedback_available"));
            scored.setRecordedScore(getAttribute(xmlsubmittedAssignment, "recorded_score"));
            scored.setStats(getAttribute(xmlsubmittedAssignment, "stats"));
            List<Element> scoresElements = xmlsubmittedAssignment.elements();
            for(Element scoreObj : scoresElements){
                List<Element> scoresList = scoreObj.elements();
                for(Element scoreList : scoresList){
                    SamigoScores score = new SamigoScores();
                    score.setIndividualScore(getAttribute(scoreList, "individual_score"));
                    score.setSubmitted(getAttribute(scoreList, "submitted"));
                    score.setTime(getAttribute(scoreList, "time"));
                    scored.addScore(score);
                }
            } 
            scoredList.add(scored);
        }
        samigo.setScored(scoredList);
        
        if(samigo.getAvailable().isEmpty() && samigo.getScored().isEmpty() ){
            return null;
        }
        return samigo;
    }
}

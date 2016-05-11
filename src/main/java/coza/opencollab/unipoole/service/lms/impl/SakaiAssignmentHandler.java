package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.AssignmentService;
import static coza.opencollab.unipoole.service.ErrorCodes.LMS_CONTENT;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.createDocument;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.getAttribute;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.getElement;
import static coza.opencollab.unipoole.service.util.impl.DomHelper.getElements;
import coza.opencollab.unipoole.shared.Assignment;
import coza.opencollab.unipoole.shared.AssignmentSubmission;
import coza.opencollab.unipoole.shared.Attachment;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Sakai implementation of the assignment handler.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class SakaiAssignmentHandler implements ToolContentService<List<Assignment>, List<AssignmentSubmission>> {

    /*
     * The Sakai assignments for a module
     */
    @Autowired
    private AssignmentService assignmentService;
    /*
     * The Sakai resources for a module
     */
    @Autowired
    private SakaiResourceHandler resourcesHandler;

    /*
     * The Sakai resources for a module
     */
    public void setResourcesHandler(SakaiResourceHandler resourcesHandler) {
        this.resourcesHandler = resourcesHandler;
    }
    
    public void setAssignmentService(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Assignment> getContent(String sessionId, String moduleId, Date fromDate) {
        try {
            Document document = createDocument(assignmentService.getAssignmentsForContext(sessionId, moduleId, fromDate));
            return getAssigments(sessionId, moduleId, document);
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve assignments from Sakai (" + moduleId + ").", e);
        }
    }

    @Override
    public List<AssignmentSubmission> getUserContent(String sessionId, String username, String moduleId, Date fromDate) {
        try {
            Document document = createDocument(assignmentService.getUserSubmissionsForContextAssignments(sessionId, username, moduleId, fromDate));
            return getAssigmentSubmissions(sessionId, moduleId, document);
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve assignment submissions from Sakai (" + moduleId + ").", e);
        }
    }

    @Override
    public Map updateUserContent(String sessionId, String username, String moduleId, Map content, String originalContent) {
        
        String assignmentId = (String) content.get("assignmentId");
        String submittedText = (String) content.get("submittedText");
        String[] attachmentNames = (String[]) content.get("attachmentNames");
        byte[][] attachmentContent =  (byte[][]) content.get("attachmentContent");
        String[] attachmentMimeTypes = (String[]) content.get("attachmentMimeTypes");
        
        try {
            Document document = createDocument(assignmentService.editAssignmentSubmission(sessionId, moduleId, assignmentId, username, submittedText, attachmentNames, attachmentMimeTypes, attachmentContent));
            
            Map<String, String> map = new HashMap<String, String>();
            
            Element submissionEditElement = getElement(document, "submission_edit");
            
            map.put("id", getAttribute(submissionEditElement, "id"));
            map.put("assignmentId", getAttribute(submissionEditElement, "assignemnt_id"));
            map.put("gradeDisplay", getAttribute(submissionEditElement, "grade_display"));
            map.put("resubmissionCount", getAttribute(submissionEditElement, "resubmission_count"));
            map.put("status", getAttribute(submissionEditElement, "status"));
            
            return map;
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not edit assignment submissions from Sakai (" + moduleId + ").", e);
        }
    }
    
    private List<AssignmentSubmission> getAssigmentSubmissions(String sessionId, String moduleId, Document document) {

        List<Element> submissionElements = getElements(document, "/submissions/submission");

        if (submissionElements == null) {
            return null;
        }

        List<AssignmentSubmission> assignmentSubmissions = new ArrayList<AssignmentSubmission>();

        for (Element submission : submissionElements) {
            AssignmentSubmission assignmentSubmission = new AssignmentSubmission();

            assignmentSubmission.setContext(getAttribute(submission, "context"));
            assignmentSubmission.setFeedbackComment(getAttribute(submission, "feedback_comment"));
            assignmentSubmission.setFeedbackText(getAttribute(submission, "feedback_text"));
            assignmentSubmission.setGrade(getAttribute(submission, "grade"));
            assignmentSubmission.setGradeReleased(getAttribute(submission, "grade_released"));
            assignmentSubmission.setGraded(getAttribute(submission, "graded"));
            assignmentSubmission.setId(getAttribute(submission, "id"));
            assignmentSubmission.setSubmittedText(getAttribute(submission, "submitted_text"));

            List<Element> attachmentElements = submission.selectNodes("attachment");

            for (Element attachmentElement : attachmentElements) {
                assignmentSubmission.addToAttachments(getAttachment(attachmentElement, moduleId, sessionId));
            }
            assignmentSubmissions.add(assignmentSubmission);

        }
        return assignmentSubmissions;
    }

    private List<Assignment> getAssigments(String sessionId, String moduleId, Document document) {

        List<Element> assignmentElements = getElements(document, "/assignments/assignment");

        if (assignmentElements == null) {
            return null;
        }

        List<Assignment> assignments = new ArrayList<Assignment>();

        for (Element assignmentElement : assignmentElements) {
            Assignment assignment = new Assignment();
            assignment.setAuthorLastModified(getAttribute(assignmentElement, "author_last_modified"));
            assignment.setContext(getAttribute(assignmentElement, "context"));
            assignment.setCreator(getAttribute(assignmentElement, "creator"));
            assignment.setDropDeadTime(getAttribute(assignmentElement, "drop_dead_time"));
            assignment.setDueTime(getAttribute(assignmentElement, "due_time"));
            assignment.setGroupProject(getAttribute(assignmentElement, "group_project"));
            assignment.setId(getAttribute(assignmentElement, "id"));
            assignment.setInstructions(getAttribute(assignmentElement, "instructions"));
            assignment.setMaxGradePointDisplay(getAttribute(assignmentElement, "max_grade_point_display"));
            assignment.setOpenTime(getAttribute(assignmentElement, "open_time"));
            assignment.setTitle(getAttribute(assignmentElement, "title"));

            List<Element> attachmentElements = assignmentElement.selectNodes("attachment");

            for (Element attachmentElement : attachmentElements) {
                assignment.addToAttachments(getAttachment(attachmentElement, moduleId, sessionId));
            }
            assignments.add(assignment);
        }
        return assignments;
    }

    private Attachment getAttachment(Element attachmentElement, String moduleId, String sessionId) {
        Attachment attachment = new Attachment();

        attachment.setId(getAttribute(attachmentElement, "id"));
        attachment.setName(getAttribute(attachmentElement, "name"));
        attachment.setSize(Integer.parseInt(getAttribute(attachmentElement, "size")));
        attachment.setMimeType(getAttribute(attachmentElement, "type"));
        String url = getAttribute(attachmentElement, "url");
        attachment.setUrl(url);
        attachment.setPath(url.substring(url.indexOf(moduleId)));
        attachment.setContent(resourcesHandler.getResourceData(sessionId, attachment.getId()));

        return attachment;
    }
}

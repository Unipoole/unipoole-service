package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractResourceContentConverter;
import coza.opencollab.unipoole.service.content.StoredFileConsumer;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.shared.Assignment;
import coza.opencollab.unipoole.shared.AssignmentSubmission;
import coza.opencollab.unipoole.shared.Attachment;
import coza.opencollab.unipoole.util.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author OpenCollab
 */
public class AssignmentConverter extends AbstractResourceContentConverter<List<Assignment>, List<AssignmentSubmission>> {

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(AssignmentConverter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageKey() {
        return "assignment";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, List<Assignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return null;
        }

        Map<String, Assignment> data = new HashMap<String, Assignment>();
        List<StorageEntry> entries = new ArrayList<StorageEntry>();

        for (Assignment assignment : assignments) {
            updateLinks(moduleId, assignment, entries);
            data.put(assignment.getId(), assignment);
            loadAttachments(moduleId, assignment.getAttachments(), entries);
        }
        if (data.size() > 0) {
            StorageEntry entry = new ByteArrayEntry("assignments.json", "data", JsonParser.writeJsonBytes(data));
            entries.add(entry);
        }

        LOG.info("Assignments Converter executed");
        return entries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convertUserData(String moduleId, List<AssignmentSubmission> assignmentSubmissions) {
        if (assignmentSubmissions == null || assignmentSubmissions.isEmpty()) {
            return null;
        }
        
        Map<String, AssignmentSubmission> data = new HashMap<String, AssignmentSubmission>();
        List<StorageEntry> entries = new ArrayList<StorageEntry>();

        for (AssignmentSubmission assignmentSubmission : assignmentSubmissions) {
            updateUserContentLinks(moduleId, assignmentSubmission, entries);
            data.put(assignmentSubmission.getId(), assignmentSubmission);
            loadAttachments(moduleId, assignmentSubmission.getAttachments(), entries);
        }
        if (data.size() > 0) {
            StorageEntry entry = new ByteArrayEntry("assignments.json", "data", JsonParser.writeJsonBytes(data));
            entries.add(entry);
        }

        LOG.info("Assignments Converter executed");
        return entries;
    }
    
    /**
     * Update the links to point to downloaded files.
     */
    private void updateLinks(String moduleId, final Assignment assignment, final List<StorageEntry> entries) {
        assignment.setInstructions(updateLinks(moduleId, entries, new StoredFileConsumer() {

            @Override
            public void stored(String name, String directory, String mimeType, String originalUrl, int size, String downloadKey) {
                Attachment attachment = new Attachment();
                attachment.setId(originalUrl);
                attachment.setName(name);
                attachment.setSize(size);
                attachment.setPath(directory);
                attachment.setUrl(originalUrl);
                attachment.setDownloadKey(downloadKey);
                attachment.setLink(true);
                attachment.setMimeType(mimeType);
                assignment.addToAttachments(attachment);
            }
        }, assignment.getId(), assignment.getInstructions()));
    }
    
    /**
     * Update the links to point to downloaded files.
     */
    private void updateUserContentLinks(String moduleId, final AssignmentSubmission assignmentSubmission, final List<StorageEntry> entries) {
        assignmentSubmission.setSubmittedText(updateLinks(moduleId, entries, new StoredFileConsumer() {

            @Override
            public void stored(String name, String directory, String mimeType, String originalUrl, int size, String downloadKey) {
                Attachment attachment = new Attachment();
                attachment.setId(originalUrl);
                attachment.setName(name);
                attachment.setSize(size);
                attachment.setPath(directory);
                attachment.setUrl(originalUrl);
                attachment.setDownloadKey(downloadKey);
                attachment.setLink(true);
                attachment.setMimeType(mimeType);
                assignmentSubmission.addToAttachments(attachment);
            }
        }, assignmentSubmission.getId(), assignmentSubmission.getSubmittedText()));
    }
    
    /**
     * loads all the attachment files depending on whether it must be in the
     * data and/or in storage.
     */
    private void loadAttachments(String moduleId, List<Attachment> attachments, List<StorageEntry> entries) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        for (Attachment attachment : attachments) {
            if (attachment.getContent() != null) {
                String name = attachment.getName();
                attachment.setDownloadKey(addExtraResource(moduleId, entries, name, getDirectory(attachment.getId(), name), attachment.getContent()));
            }
            attachment.setContent(null);
        }
    }
    
    private String getDirectory(String id, String name) {
        return id.substring(0, id.length() - name.length() - 1);
    }

}

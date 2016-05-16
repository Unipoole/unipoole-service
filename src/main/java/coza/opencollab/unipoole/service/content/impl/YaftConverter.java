package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractResourceContentConverter;
import coza.opencollab.unipoole.service.content.StoredFileConsumer;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.shared.Attachment;
import coza.opencollab.unipoole.shared.Discussion;
import coza.opencollab.unipoole.shared.Forum;
import coza.opencollab.unipoole.shared.Message;
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
public class YaftConverter extends AbstractResourceContentConverter<List<Forum>, Object> {

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(YaftConverter.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageKey() {
        return "yaft";
    }
    
    /*
     * (non-Javadoc)
     * @see coza.opencollab.unipoole.service.content.ContentConverter#convert(java.lang.Object)
     */
    @Override
    public List<StorageEntry> convert(String moduleId, List<Forum> forums) {
        if (forums == null || forums.isEmpty()) {
            return null;
        }

        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        Map<String, Forum> data = new HashMap<String, Forum>();
        for (Forum forum : forums) {
            data.put(forum.getId(), forum);
            if (null != forum.getDiscussions()) {
                for (Discussion discussion : forum.getDiscussions()) {
                	
                	// Update the links for the discussion too
                	try {
                		updateDiscussionLinks(moduleId, discussion, entries);
                        loadAttachments(moduleId, discussion.getAttachments(), entries);
                    } catch (Exception ex) {
                        LOG.warn("Unable to replace content links to online content with offline content and add an attachment.", ex);
                    }
                	
                    if (null != discussion.getMessages()) {
                        for (Message message : discussion.getMessages()) {
                            try {
                                updateMessageLinks(moduleId, message, entries);
                                loadAttachments(moduleId, message.getAttachments(), entries);
                            } catch (Exception ex) {
                                LOG.warn("Unable to replace content links to online content with offline content and add an attachment.", ex);
                            }
                        }
                    }
                }
            }
        }
        if (data.size() > 0) {
            ByteArrayEntry entry = new ByteArrayEntry("yaft.json", "data", JsonParser.writeJsonBytes(data));
            entries.add(entry);
        }
        LOG.info("Yaft Converter executed");
        return entries;
    }

    /**
     * Update the links to point to downloaded files.
     */
    private void updateMessageLinks(String moduleId, final Message message, final List<StorageEntry> entries) {
        message.setContent(updateLinks(moduleId, entries, new StoredFileConsumer() {

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
                message.addToAttachments(attachment);
            }
        }, message.getId(), message.getContent()));
    }
    
    /**
     * Update the links to point to downloaded files.
     */
    private void updateDiscussionLinks(String moduleId, final Discussion discussion, final List<StorageEntry> entries) {
        discussion.setContent(updateLinks(moduleId, entries, new StoredFileConsumer() {

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
                discussion.addToAttachments(attachment);
            }
        }, discussion.getId(), discussion.getContent()));
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
                attachment.setContent(null);
            }
        }
    }

    private String getDirectory(String id, String name) {
        return id.substring(0, id.length() - name.length() - 1);
    }
}
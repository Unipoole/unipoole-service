package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractResourceContentConverter;
import coza.opencollab.unipoole.service.content.StoredFileConsumer;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.shared.Attachment;
import coza.opencollab.unipoole.shared.Welcome;
import coza.opencollab.unipoole.util.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author OpenCollab
 */
public class WelcomeConverter extends AbstractResourceContentConverter<Welcome, Object>{

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(WelcomeConverter.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageKey() {
        return "welcome";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, Welcome data) {
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        updateLinks(moduleId, data, entries);
        ByteArrayEntry entry = new ByteArrayEntry("welcome.json", "data", JsonParser.writeJsonBytes(data));
        entries.add(entry);
        LOG.info("Welcome Converter executed");
        return entries;
    }

    /**
     * Update the links to point to downloaded files.
     */
    private void updateLinks(String moduleId, final Welcome welcome, final List<StorageEntry> entries) {
        welcome.setContent(updateLinks(moduleId, entries, new StoredFileConsumer() {

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
                welcome.addToAttachments(attachment);
            }
        }, "", welcome.getContent()));
    }
}

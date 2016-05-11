package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractContentConverter;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.shared.Samigo;
import coza.opencollab.unipoole.util.JsonParser;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author OpenCollab
 */
public class SamigoConverter extends AbstractContentConverter<Samigo, Object> {

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(SamigoConverter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, Samigo data) {
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        if (data == null) {
            return null;
        } else {
            ByteArrayEntry entry = new ByteArrayEntry("Samigo.json", "data", JsonParser.writeJsonBytes(data));
            entries.add(entry);
        }
        LOG.info("Samigo Converter executed");
        return entries;
    }

    /**
     * Data specific to a user is converted into the samigo format.
     * @param moduleId
     * @param data
     * @return
     */
    @Override
    public List<StorageEntry> convertUserData(String moduleId, Object data) {
        Samigo sam = (Samigo) data;
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        if (data != null) {
            ByteArrayEntry entry = new ByteArrayEntry("Samigo.json", "data", JsonParser.writeJsonBytes(data));
            entries.add(entry);
        }else{
            return null;
        }
        LOG.info("Samigo Converter executed");
        return entries;
    }
}

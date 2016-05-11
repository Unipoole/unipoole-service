package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.Defaults;
import coza.opencollab.unipoole.service.content.AbstractContentConverter;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The convertor for the client base.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class ClientBaseConverter extends AbstractContentConverter<Map<String, String>, Object>{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, Map<String, String> data) {
        if(data == null || data.isEmpty()){
            return null;
        }
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        for(Map.Entry<String, String> mapEntry: data.entrySet()){
            ByteArrayEntry entry = new ByteArrayEntry(mapEntry.getKey(), "data", mapEntry.getValue().getBytes(Defaults.UTF8));
            entries.add(entry);
        }
        return entries;
    }
}

package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractContentConverter;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.util.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A converter for the home tool.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class HomeConverter extends AbstractContentConverter<Map, Object>{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, Map data) {
        if(data == null || data.isEmpty()){
            return null;
        }
        Map fullData = new HashMap();
        fullData.put("home", data);
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        ByteArrayEntry entry = new ByteArrayEntry("home.json", "data", JsonParser.writeJsonBytes(fullData));
        entries.add(entry);
        return entries;
    }
}

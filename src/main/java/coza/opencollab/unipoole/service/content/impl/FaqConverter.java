package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractContentConverter;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.shared.Faq;
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
public class FaqConverter extends AbstractContentConverter<List<Faq>, Object> {

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(FaqConverter.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, List<Faq> faqs) {
        if(faqs == null || faqs.isEmpty()){
            return null;
        }
        Map<String, Faq> data = new HashMap<String, Faq>();
        for(Faq faq: faqs){
            data.put(String.valueOf(faq.getId()), faq);
        }
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        if(data.size() > 0){
            ByteArrayEntry entry = new ByteArrayEntry("faq.json", "data", JsonParser.writeJsonBytes(data));
            entries.add(entry);
        }
        LOG.info("Faq Converter executed");
        return entries;
    }
}

package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractResourceContentConverter;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.shared.MeletePart;
import coza.opencollab.unipoole.shared.Resource;
import coza.opencollab.unipoole.util.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author OpenCollab
 */
public class MeleteConverter extends AbstractResourceContentConverter<Map<String, MeletePart>, Object> {

    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(MeleteConverter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageKey() {
        return "melete";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, Map<String, MeletePart> parts) {
        if(parts == null || parts.isEmpty()){
            return null;
        }
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        for (Map.Entry<String, MeletePart> entry : parts.entrySet()) {
            if(entry.getValue().getResource() != null){
                setResource(moduleId, entry.getValue(), entries);
            }
        }
        if(parts.size() > 0){
            ByteArrayEntry entry = new ByteArrayEntry("melete.json", "data", JsonParser.writeJsonBytes(parts));
            entries.add(entry);
        }
        LOG.info("Melete Converter executed");
        return entries;
    }
    
    private void setResource(String moduleId, MeletePart meletePart, List<StorageEntry> entries){
        if("typeEditor".equals(meletePart.getContentType())){
            meletePart.setContent(getResourceContent(meletePart.getResource()));
            return;
        }
        if(!"typeUpload".equals(meletePart.getContentType())){
            return;
        }
        //now we do typeUpload
        Resource resource = meletePart.getResource();
        meletePart.setResource(null);
        meletePart.setUpload(resource);
        resource.setDownloadKey(addExtraResource(moduleId, entries, resource.getName(), meletePart.getId(), resource.getContent()));
        resource.setTreeId(moduleId + "/" + meletePart.getId() + "/" + resource.getName());
    }

    /**
     * When given a resource ensure the resource is suitable for Melete and if
     * so return the content
     *
     * @param resource
     * @return
     */
    private String getResourceContent(Resource resource) {
        String content = "";
        if (resource != null) {
            byte[] resourceContent = resource.getContent();
            content = StringEscapeUtils.unescapeHtml(new String(resourceContent, 0 , resourceContent.length));
            content = content.replaceAll("(\r\n|\n)", "<br/>");
        }
        return content;
    }
}

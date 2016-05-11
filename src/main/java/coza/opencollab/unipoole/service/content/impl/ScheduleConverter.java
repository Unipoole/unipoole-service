package coza.opencollab.unipoole.service.content.impl;

import coza.opencollab.unipoole.service.content.AbstractResourceContentConverter;
import coza.opencollab.unipoole.service.util.StorageEntry;
import coza.opencollab.unipoole.service.util.impl.ByteArrayEntry;
import coza.opencollab.unipoole.shared.Attachment;
import coza.opencollab.unipoole.shared.Schedule;
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
public class ScheduleConverter extends AbstractResourceContentConverter<List<Schedule>, Object> {
    /**
     * The logger
     */
    private static final Logger LOG = Logger.getLogger(ScheduleConverter.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageKey(){
        return "schedule";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<StorageEntry> convert(String moduleId, List<Schedule> events) {
        if(events == null || events.isEmpty()){
            return null;
        }
        List<StorageEntry> entries = new ArrayList<StorageEntry>();
        Map<String, Schedule> data = new HashMap<String, Schedule>();
        for(Schedule schedule: events){
            data.put(schedule.getId(), schedule);
            loadAttachments(moduleId, schedule.getAttachments(), entries);
        }
        if(data.size() > 0){
            ByteArrayEntry entry = new ByteArrayEntry("schedule.json", "data", JsonParser.writeJsonBytes(data));
            entries.add(entry);
        }
        LOG.info("Schedule Converter executed");
        return entries;
    }

    /**
     * loads all the attachment files depending on whether it must be in the 
     * data and/or in storage.
     */
    private void loadAttachments(String moduleId, List<Attachment> attachments, List<StorageEntry> entries) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        for(Attachment attachment: attachments){
            if (attachment.getContent() != null) {
                String name = attachment.getName();
                attachment.setDownloadKey(addExtraResource(moduleId, entries, name, getDirectory(attachment.getId(), name), attachment.getContent()));
            }
            attachment.setContent(null);
        }
    }
    
    private String getDirectory(String id, String name){
        return id.substring(0, id.length()-name.length()-1);
    }
}

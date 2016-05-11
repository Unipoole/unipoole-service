package coza.opencollab.unipoole.service.synch.so;

import coza.opencollab.unipoole.service.report.so.Status;
import coza.opencollab.unipoole.service.ServiceObject;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * The service object for synch status checks.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class SynchStatus extends ServiceObject{
    /**
     * The module code
     */
    private String moduleId;
    /**
     * The device id
     */
    private String deviceId;
    /**
     * The tool statuses.
     */
    private Map<String, Status> toolStatuses;

    /**
     * The module code
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * The module code
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * The device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * The device id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * The tool statuses.
     */
    public Map<String, Status> getTools() {
        return toolStatuses;
    }

    /**
     * The tool statuses.
     */
    public void setTools(Map<String, Status> toolStatuses) {
        this.toolStatuses = toolStatuses;
    }
    
    /**
     * Add a tool status.
     */
    @JsonIgnore
    public void addToolStatus(String toolName, Status toolStatus){
        if(toolStatuses == null){
            toolStatuses = new HashMap<String, Status>();
        }
        toolStatuses.put(toolName, toolStatus);
    }
}

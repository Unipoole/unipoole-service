package coza.opencollab.unipoole.service.dbo;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * This is a representation of table UNI_TOOL_VERSION
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name = "UNI_TOOL_VERSION")
@NamedQueries({
    @NamedQuery(
        name="toolNames",
        query="SELECT t.toolName FROM ToolVersion t WHERE t.active = TRUE AND t.deviceRegistrationId = :" + DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID
    ),
    @NamedQuery(
        name="toolVersionActive",
        query="SELECT t FROM ToolVersion t WHERE t.active = TRUE AND t.deviceRegistrationId = :" +
        DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID + " AND t.toolName = :" + ToolVersion.PARAMETER_TOOL_NAME
    ),
    @NamedQuery(
        name="toolVersion",
        query="SELECT t FROM ToolVersion t WHERE t.deviceRegistrationId = :" +
        DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID + " AND t.toolName = :" + 
        ToolVersion.PARAMETER_TOOL_NAME + " AND t.toolVersion = :" + ToolVersion.PARAMETER_TOOL_VERSION
    ),
    @NamedQuery(
        name="toolVersions",
        query="SELECT t FROM ToolVersion t WHERE t.active = TRUE AND t.deviceRegistrationId = :" + DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID
    ),
    @NamedQuery(
        name="toolVersionRange",
        query="SELECT t FROM ToolVersion t WHERE t.deviceRegistrationId = :" +
        DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID + " AND t.toolName = :" + ToolVersion.PARAMETER_TOOL_NAME +
        " AND t.id > :idFrom AND t.id <= :idTo ORDER BY t.id ASC"
    ),
    @NamedQuery(
        name="toolVersionDeactivate",
        query="UPDATE ToolVersion t SET t.active = FALSE WHERE t.deviceRegistrationId = :" +
        DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID + " AND t.toolName = :" + 
        ToolVersion.PARAMETER_TOOL_NAME + " AND t.toolVersion <> :" + ToolVersion.PARAMETER_TOOL_VERSION
    ),
    @NamedQuery(
        name="toolVersionDeactivateAll",
        query="UPDATE ToolVersion t SET t.active = FALSE WHERE t.deviceRegistrationId = :" +
        DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID + " AND t.toolName = :" + 
        ToolVersion.PARAMETER_TOOL_NAME
    )
})
public class ToolVersion implements Serializable{
    private static final long serialVersionUID = -8712872385957386182L;
    /**
     * The query parameter.
     */
    public static final String PARAMETER_TOOL_NAME = "toolName";
    /**
     * The query parameter.
     */
    public static final String PARAMETER_TOOL_VERSION = "toolVersion";
    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="TOOL_VERSION_SEC")
    @SequenceGenerator(name="TOOL_VERSION_SEC", sequenceName="UNI_TOOL_VERSION_SEC")
    private Long id;
    /**
     * The device registration id
     */
    private Long deviceRegistrationId;
    /**
     * The tool name
     */
    private String toolName;
    /**
     * The code version
     */
    private String toolVersion;
    /**
     * Active indicator.
     */
    private boolean active;
    /**
     * The last updated date and time.
     * Used as a optimistic lock by jpa
     */
    @Version
    private Timestamp lastUpdated;

    /**
     * The id of the record
     */
    public Long getId() {
        return id;
    }

    /**
     * The id of the record
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * The reference to the device registration table
     */
    public Long getDeviceRegistrationId() {
        return deviceRegistrationId;
    }

    /**
     * The reference to the device registration table
     */
    public void setDeviceRegistrationId(Long deviceRegistrationId) {
        this.deviceRegistrationId = deviceRegistrationId;
    }

    /**
     * The tool name
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * The tool name
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * The code version
     */
    public String getToolVersion() {
        return toolVersion;
    }

    /**
     * The code version
     */
    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    /**
     * Active indicator.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Active indicator.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * The last updated date and time.
     * Used as a optimistic lock by jpa
     */
    public Timestamp getLastUpdated() {
        return lastUpdated==null?null:new Timestamp(lastUpdated.getTime());
    }

    /**
     * The last updated date and time.
     * Used as a optimistic lock by jpa
     */
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated==null?null:new Timestamp(lastUpdated.getTime());
    }
    
    /**
     * Update the lastUpdated value.
     * This method is called by jpa before persist and update
     */
    @PrePersist
    @PreUpdate
    public void setLastUpdated(){
        lastUpdated = new Timestamp(System.currentTimeMillis());
    }
}

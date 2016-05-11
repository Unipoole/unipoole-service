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
 * This dbo represent the UNI_MODULE_REG table.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name = "UNI_MODULE_REG")
@NamedQueries({
    @NamedQuery(
        name = "moduleIds",
        query = "SELECT m.moduleId FROM ModuleRegistration m WHERE m.active = TRUE AND m.deviceRegistrationId = :" + DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID
    ),
    @NamedQuery(
        name = "moduleRegistration",
        query = "SELECT mr FROM ModuleRegistration mr WHERE mr.deviceRegistrationId = :" + 
        DeviceRegistration.PARAMETER_DEVICE_REGISTRATION_ID + " AND mr.moduleId = :" + ModuleRegistration.PARAMETER_MODULE_ID
    )
})
public class ModuleRegistration implements Serializable{
    private static final long serialVersionUID = -843116544346546L;
    /**
     * The query parameter.
     */
    public static final String PARAMETER_MODULE_ID = "moduleId";
    /**
     * The query parameter.
     */
    public static final String PARAMETER_MODULE_REGISTRATION_ID = "moduleRegistrationId";
    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="MODULE_REG_SEC")
    @SequenceGenerator(name="MODULE_REG_SEC", sequenceName="UNI_MODULE_REG_SEC")
    private Long id;
    /**
     * The device registration id
     */
    private Long deviceRegistrationId;
    /**
     * The module id
     */
    private String moduleId;
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
     * The device registration id
     */
    public Long getDeviceRegistrationId() {
        return deviceRegistrationId;
    }

    /**
     * The device id
     */
    public void setDeviceRegistrationId(Long deviceRegistrationId) {
        this.deviceRegistrationId = deviceRegistrationId;
    }

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

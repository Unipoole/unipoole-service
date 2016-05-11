package coza.opencollab.unipoole.service.dbo;

import coza.opencollab.unipoole.service.Defaults;
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
 * This dbo represent the UNI_DEVICE_REG table.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name = "UNI_DEVICE_REG")
@NamedQueries({
    @NamedQuery(
        name="usernames",
        query="SELECT DISTINCT d.username FROM DeviceRegistration d WHERE d.active = TRUE AND d.username <> '" + Defaults.MASTER_USERNAME + "'"
    ),
    @NamedQuery(
        name="deviceIds",
        query="SELECT d.deviceId FROM DeviceRegistration d WHERE d.active = TRUE AND d.username = :" + DeviceRegistration.PARAMETER_USERNAME
    ),
    @NamedQuery(
        name="deviceRegistration",
        query="SELECT dr FROM DeviceRegistration dr WHERE dr.username = :" + 
        DeviceRegistration.PARAMETER_USERNAME + " AND dr.deviceId = :" + DeviceRegistration.PARAMETER_DEVICE_ID
    )
})
public class DeviceRegistration implements Serializable{
    private static final long serialVersionUID = -8434323454346546L;
    /**
     * The query parameter.
     */
    public static final String PARAMETER_USERNAME = "username";
    /**
     * The query parameter.
     */
    public static final String PARAMETER_DEVICE_REGISTRATION_ID = "deviceRegistrationId";
    /**
     * The query parameter.
     */
    public static final String PARAMETER_DEVICE_ID = "deviceId";
    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="DEVICE_REG_SEC")
    @SequenceGenerator(name="DEVICE_REG_SEC", sequenceName="UNI_DEVICE_REG_SEC")
    private Long id;
    /**
     * The username
     */
    private String username;
    /**
     * The device id
     */
    private String deviceId;
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
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
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

    /**
     * Just for exception logging.
     */
    @Override
    public String toString() {
        return "[Username=" + username
                + ",DeviceId=" + deviceId
                + "]";
    }
}

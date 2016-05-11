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
 * This dbo represent the UNI_MANAGED_MODULE table.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name = "UNI_MANAGED_MODULE")
@NamedQueries({
    @NamedQuery(
        name="managedModules",
        query="SELECT mm FROM ManagedModule mm WHERE mm.active = TRUE"
    ),
    @NamedQuery(
        name="managedModule",
        query="SELECT mm FROM ManagedModule mm WHERE mm.moduleId = :" + ModuleRegistration.PARAMETER_MODULE_ID
    )
})
public class ManagedModule implements Serializable{
    private static final long serialVersionUID = 245678987654L;
    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="MANAGED_MODULE_SEC")
    @SequenceGenerator(name="MANAGED_MODULE_SEC", sequenceName="UNI_MANAGED_MODULE_SEC")
    private Long id;
    /**
     * The module id.
     */
    private String moduleId;
    /**
     * The master module id, is this is a group module.
     */
    private String masterModuleId;
    /**
     * The last sync time.
     */
    private Timestamp lastSync;
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
     * The module id.
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * The module id.
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * The master module id, is this is a group module.
     */
    public String getMasterModuleId() {
        return masterModuleId;
    }

    /**
     * The master module id, is this is a group module.
     */
    public void setMasterModuleId(String masterModuleId) {
        this.masterModuleId = masterModuleId;
    }

    /**
     * The last sync time.
     */
    public Timestamp getLastSync() {
        return lastSync==null?null:new Timestamp(lastSync.getTime());
    }

    /**
     * The last sync time.
     */
    public void setLastSync(Timestamp lastSync) {
        this.lastSync = lastSync==null?null:new Timestamp(lastSync.getTime());
    }
    
    /**
     * Update the last sync time.
     */
    public void setLastSync(){
        lastSync = new Timestamp(System.currentTimeMillis());
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

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
 * This is a representation of table UNI_CONTENT_RELEASE
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name = "UNI_CONTENT_RELEASE")
@NamedQueries({
    @NamedQuery(
        name = "contentReleaseActive",
        query = "SELECT cr FROM ContentRelease cr WHERE cr.active = TRUE AND cr.moduleId = :" + ModuleRegistration.PARAMETER_MODULE_ID
    ),
    @NamedQuery(
        name = "contentRelease",
        query = "SELECT cr FROM ContentRelease cr WHERE cr.moduleId = :" + 
        ModuleRegistration.PARAMETER_MODULE_ID + " AND cr.releaseName = :" +
        CodeRelease.PARAMETER_RELEASE_NAME + " AND cr.releaseVersion = :" + CodeRelease.PARAMETER_RELEASE_VERSION
    ),
    @NamedQuery(
        name = "contentReleases",
        query = "SELECT cr FROM ContentRelease cr WHERE cr.moduleId = :" +
        ModuleRegistration.PARAMETER_MODULE_ID + " AND cr.releaseName = :" + CodeRelease.PARAMETER_RELEASE_NAME
    )
})
public class ContentRelease implements Serializable{
    private static final long serialVersionUID = -65444564448L;
    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="CONTENT_RELEASE_SEC")
    @SequenceGenerator(name="CONTENT_RELEASE_SEC", sequenceName="UNI_CONTENT_RELEASE_SEC")
    private Long id;
    /**
     * The module id.
     */
    private String moduleId;
    /**
     * The release name.
     */
    private String releaseName;
    /**
     * The release version.
     */
    private String releaseVersion;
    /**
     * The release date and time.
     */
    private Timestamp released;
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
     * The release name.
     */
    public String getReleaseName() {
        return releaseName;
    }

    /**
     * The release name.
     */
    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    /**
     * The release version.
     */
    public String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * The release version.
     */
    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    /**
     * The release date and time.
     */
    public Timestamp getReleased() {
        return released==null?null:new Timestamp(released.getTime());
    }

    /**
     * The release date and time.
     */
    public void setReleased(Timestamp released) {
        this.released = released==null?null:new Timestamp(released.getTime());
    }
    
    /**
     * Update the released value.
     */
    public void setReleased(){
        released = new Timestamp(System.currentTimeMillis());
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

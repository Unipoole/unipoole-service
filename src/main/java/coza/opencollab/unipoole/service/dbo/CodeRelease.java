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
 * This is a representation of table UNI_CODE_RELEASE
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name = "UNI_CODE_RELEASE")
@NamedQueries({
    @NamedQuery(
        name = "codeReleaseActive",
        query = "SELECT cr FROM CodeRelease cr WHERE cr.active = TRUE"
    ),
    @NamedQuery(
        name = "codeRelease",
        query = "SELECT cr FROM CodeRelease cr WHERE cr.releaseName = :" + 
        CodeRelease.PARAMETER_RELEASE_NAME + " AND cr.releaseVersion = :" + CodeRelease.PARAMETER_RELEASE_VERSION
    ),
    @NamedQuery(
        name = "codeReleases",
        query = "SELECT cr FROM CodeRelease cr WHERE cr.releaseName = :" + CodeRelease.PARAMETER_RELEASE_NAME
    )
})
public class CodeRelease implements Serializable{
    private static final long serialVersionUID = -65444564448L;
    /**
     * The query parameter.
     */
    public static final String PARAMETER_CODE_RELEASE_ID = "codeReleaseId";
    /**
     * The query parameter.
     */
    public static final String PARAMETER_RELEASE_NAME = "releaseName";
    /**
     * The query parameter.
     */
    public static final String PARAMETER_RELEASE_VERSION = "releaseVersion";
    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="CODE_RELEASE_SEC")
    @SequenceGenerator(name="CODE_RELEASE_SEC", sequenceName="UNI_CODE_RELEASE_SEC")
    private Long id;
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

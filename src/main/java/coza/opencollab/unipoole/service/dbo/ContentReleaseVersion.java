package coza.opencollab.unipoole.service.dbo;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * This is a representation of table UNI_CONTENT_RELEASE_VERSION
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name = "UNI_CONTENT_RELEASE_VERSION")
@NamedQuery(
        name = "contentReleaseVersions",
        query = "SELECT crv FROM ContentReleaseVersion crv WHERE crv.contentReleaseId = :contentReleaseId"
)
public class ContentReleaseVersion implements Serializable{
    private static final long serialVersionUID = -65444564448L;
    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="CONTENT_RELEASE_VERSION_SEC")
    @SequenceGenerator(name="CONTENT_RELEASE_VERSION_SEC", sequenceName="UNI_CONTENT_REL_VERSION_SEC")
    private Long id;
    /**
     * The content release id.
     */
    private Long contentReleaseId;
    /**
     * The tool name.
     */
    private String toolName;
    /**
     * The content version.
     */
    private String contentVersion;
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
     * The content release id.
     */
    public Long getContentReleaseId() {
        return contentReleaseId;
    }

    /**
     * The content release id.
     */
    public void setContentReleaseId(Long contentReleaseId) {
        this.contentReleaseId = contentReleaseId;
    }

    /**
     * The tool name.
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * The tool name.
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * The content version.
     */
    public String getContentVersion() {
        return contentVersion;
    }

    /**
     * The content version.
     */
    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
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

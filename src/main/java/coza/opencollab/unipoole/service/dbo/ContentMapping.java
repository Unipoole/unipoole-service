package coza.opencollab.unipoole.service.dbo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


/**
 * This is a representation of table UNI_CONTENT_MAPING
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@Entity
@Table(name="UNI_CONTENT_MAPPING",
		uniqueConstraints={
			@UniqueConstraint(columnNames={"SITE_FROM_ID","SITE_TO_ID","TOOL_NAME","TOOL_FROM_ID"})
		}
)
@NamedQueries({
    @NamedQuery(
        name = "ContentMapping.getMapping",
        query = "SELECT cm FROM ContentMapping cm WHERE cm.siteToId = :" + ContentMapping.PARAMETER_SITE_TO_ID + " AND cm.toolName = :" + ContentMapping.PARAMETER_TOOL_NAME
    )
})
public class ContentMapping implements Serializable{
	
	public static final String PARAMETER_SITE_FROM_ID = "siteFromId";
	public static final String PARAMETER_SITE_TO_ID = "siteToId";
	public static final String PARAMETER_TOOL_NAME = "toolName";

	/**
	 * 
	 */
	private static final long serialVersionUID = -9189413108930433009L;

    /**
     * The id of the record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="CONTENT_MAPPING_SEC")
    @SequenceGenerator(name="CONTENT_MAPPING_SEC", sequenceName="UNI_CONTENT_MAPPING_SEC")
    private Long id;

	/**
	 * Id of the site this content was copied from i.e the master site
	 */
	@Column(name="SITE_FROM_ID")
	private String siteFromId;
	
	
	/**
	 * Id of the site this content was copied to.
	 */
	@Column(name="SITE_TO_ID")
	private String siteToId;
	
	
	/**
	 * Name of the tool this content is related too
	 */
	@Column(name="TOOL_NAME")
	private String toolName;
	
	
	/**
	 * Id of this content on the site this content was copied from i.e. the master site
	 */
	@Column(name="TOOL_FROM_ID")
	private String toolFromId;
	
	
	/**
	 * Id of this content on the site this content was copies to. I.e. the group site.
	 */
	@Column(name="TOOL_TO_ID")
	private String toolToId;


	/**
	 * @return the siteFromId
	 */
	public String getSiteFromId() {
		return siteFromId;
	}


	/**
	 * @param siteFromId the siteFromId to set
	 */
	public void setSiteFromId(String siteFromId) {
		this.siteFromId = siteFromId;
	}


	/**
	 * @return the siteToId
	 */
	public String getSiteToId() {
		return siteToId;
	}


	/**
	 * @param siteToId the siteToId to set
	 */
	public void setSiteToId(String siteToId) {
		this.siteToId = siteToId;
	}


	/**
	 * @return the toolName
	 */
	public String getToolName() {
		return toolName;
	}


	/**
	 * @param toolName the toolName to set
	 */
	public void setToolName(String toolName) {
		this.toolName = toolName;
	}


	/**
	 * @return the toolFromId
	 */
	public String getToolFromId() {
		return toolFromId;
	}


	/**
	 * @param toolFromId the toolFromId to set
	 */
	public void setToolFromId(String toolFromId) {
		this.toolFromId = toolFromId;
	}


	/**
	 * @return the toolToId
	 */
	public String getToolToId() {
		return toolToId;
	}


	/**
	 * @param toolToId the toolToId to set
	 */
	public void setToolToId(String toolToId) {
		this.toolToId = toolToId;
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	

	
	
}

/**
 * 
 */
package coza.opencollab.unipoole.service.synch.so;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import coza.opencollab.unipoole.service.ServiceObject;

/**
 * @author Opencollab
 *
 */
public class SyncContentMapping extends ServiceObject{

	private static final long serialVersionUID = -5635053599802562621L;
	
	/**
	 * List of mappings for ids in the requested tool.
	 */
	private Map<String, Map<String, String>> toolMappings = new HashMap<String, Map<String, String>>();
	
	/**
	 * Mapping for the first group version for a tool.
	 */
	private Map<String, String> versionMappings = new HashMap<String, String>();

	/**
	 * @return the mappings
	 */
	public Map<String, Map<String, String>> getMappings() {
		return toolMappings;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getVersions(){
		return versionMappings;
	}

	/**
	 * @param mappings the mappings to set
	 */
	public void addMapping(ToolMapping mapping) {
		this.toolMappings.put(mapping.getToolId(), mapping.getMappings());
	}
	
	/**
	 * 
	 * @param toolname
	 * @param version
	 */
	public void addContentVersion(String toolname, String version){
		this.versionMappings.put(toolname, version);
	}
	
	/**
	 * Class representing the mappings for a tool
	 */
	public static class ToolMapping{
		private String toolId;
		private Map<String, String> mappings;
		
		public ToolMapping(String toolId, Map<String, String> mappings){
			this.toolId = toolId;
			this.mappings = mappings;
		}
		
		/**
		 * @return the mappings
		 */
		public Map<String, String> getMappings() {
			return this.mappings;
		}
		
		public String getToolId(){
			return this.toolId;
		}
		
		@JsonIgnore
		public boolean isEmpty(){
			return this.mappings.isEmpty();
		}
	}
}

/**
 * 
 */
package coza.opencollab.unipoole.service.synch.so;

import java.util.Map;

import coza.opencollab.unipoole.service.ServiceObject;

/**
 * @author Opencollab
 *
 */
public class SyncContentMapping extends ServiceObject{

	/**
	 * List of mappings for ids in the requested tool.
	 */
	private Map<String, String> mappings;

	/**
	 * @return the mappings
	 */
	public Map<String, String> getMappings() {
		return mappings;
	}

	/**
	 * @param mappings the mappings to set
	 */
	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}
	
	
	
	
	
}

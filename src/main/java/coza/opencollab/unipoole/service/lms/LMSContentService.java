package coza.opencollab.unipoole.service.lms;

/**
 * The content service for the LMS.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface LMSContentService {
    /**
     * Retrieve the resource from the lms.
     * 
     * @param sessionId The session id.
     * @param resourceId The resource id.
     * @return The resource bytes.
     */
    public byte[] getResourceData(String sessionId, String resourceId);
}

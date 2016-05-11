package coza.opencollab.unipoole.service.lms.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.sakai.client.WelcomeService;
import static coza.opencollab.unipoole.service.ErrorCodes.LMS_CONTENT;
import coza.opencollab.unipoole.service.lms.ToolContentService;
import coza.opencollab.unipoole.shared.Welcome;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 *
 * @author OpenCollab
 */
public class SakaiWelcomeHandler implements ToolContentService {

    /*
     * The Sakai announcements for a  module
     */
    @Autowired
    private WelcomeService welcomeService;

    @Override
    public Object getContent(String sessionId, String moduleId, Date fromDate) {
        Welcome welcome = new Welcome();
        try {
            welcome.setContent(welcomeService.getWelcomeForSite(moduleId,fromDate));            
        } catch (Exception e) {
            throw new UnipooleException(LMS_CONTENT, "Could not retrieve welcome data from Sakai (" + moduleId + ").", e);
        }
        if (StringUtils.isEmpty(welcome.getContent())) {
            return null;
        }
        return welcome;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUserContent(String sessionId, String username, String moduleId, Date fromDate) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map updateUserContent(String sessionId, String username, String moduleId, Map content, String originalContent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

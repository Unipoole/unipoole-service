package coza.opencollab.unipoole.service.event;

import coza.opencollab.unipoole.service.BaseController;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import coza.opencollab.unipoole.service.event.so.Event;
import coza.opencollab.unipoole.service.event.so.EventList;
import static coza.opencollab.unipoole.util.JsonParser.parseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller for logging Sakai events. This class handle RestFull service
 * calls using JSON.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
@Controller
@RequestMapping("/service-event")
public class EventController extends BaseController {

    /**
     * The implementation is injected by Spring
     */
    @Autowired
    private EventService eventService;

    /**
     * Logs the events received from the client. Formats the data for correct
     * logging in Sakai
     *
     * @param body The content of the call
     * @param username The username
     * @param deviceId The device Id
     * @param moduleId The module id
     * @return The Login object given a status and a sessionId of successful.
     */
    @RequestMapping(value = "/event/{username}/{deviceId}/{moduleId}", method = RequestMethod.POST)
    public @ResponseBody
    ServiceObject event(@RequestBody String body, @PathVariable String username, @PathVariable String deviceId, @PathVariable String moduleId) {
        EventList events = parseJson(body, EventList.class);
        for (Event event : events.getEvents()) {
            eventService.addEventForUser(username, event.getEventCode(), "student=" + username + " site=" + moduleId + " " + event.getReference() + " : " + event.getTimeStamp(), event.getContext());
        }
        return new ServiceObject(ServiceCallStatus.SUCCESS);
    }
}

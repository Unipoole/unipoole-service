package coza.opencollab.unipoole.service.event.so;

import java.util.Date;

/**
 * The Event service object
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class Event {
    
    /**
     * The event key code
     */
    private String eventCode;
    
    /**
     * The context of the event
     */
    private String context;
    
    /**
     * The reference for the event
     */
    private String reference;
    
    /**
     * The time the event occurred
     */
    private Date timeStamp;

    /**
     * @return the eventCode
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * @param eventCode the eventCode to set
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
    
}

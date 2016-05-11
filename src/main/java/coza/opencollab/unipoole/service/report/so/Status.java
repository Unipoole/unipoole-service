package coza.opencollab.unipoole.service.report.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import java.io.Serializable;

/**
 * This class represent the status of a client tool, the base code or the tools, and its content.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class Status extends ServiceObject implements Serializable{
    /**
     * The client code version
     */
    private String clientCodeVersion;
    /**
     * The client content version
     */
    private String clientContentVersion;
    /**
     * The current code version
     */
    private String currentCodeVersion;
    /**
     * The current content version
     */
    private String currentContentVersion;
    /**
     * The size of the synch package to update the version (code/files).
     */
    private long codeSynchSize;
    /**
     * The size of the synch package to update the content.
     */
    private long contentSynchSize;
    
    /**
     * Default Constructor
     */
    public Status(){}
    
    /**
     * Constructor setting the status.
     */
    public Status(ServiceCallStatus statusCode){
        super(statusCode);
    }
    
    /**
     * Copy constructor.
     */
    public Status(ServiceObject other){
        super(other);
    }

    /**
     * Set all constructor
     */
    public Status(String clientCodeVersion, String clientContentVersion, String currentCodeVersion, String currentContentVersion, int codeSynchSize, int contentSynchSize){
        this.clientCodeVersion = clientCodeVersion;
        this.clientContentVersion = clientContentVersion;
        this.currentCodeVersion = currentCodeVersion;
        this.currentContentVersion = currentContentVersion;
        this.codeSynchSize = codeSynchSize;
        this.contentSynchSize = contentSynchSize;
    }
    /**
     * The client code version
     */
    public String getClientCodeVersion() {
        return clientCodeVersion;
    }

    /**
     * The client code version
     */
    public void setClientCodeVersion(String clientCodeVersion) {
        this.clientCodeVersion = clientCodeVersion;
    }

    /**
     * The client content version
     */
    public String getClientContentVersion() {
        return clientContentVersion;
    }

    /**
     * The client content version
     */
    public void setClientContentVersion(String clientContentVersion) {
        this.clientContentVersion = clientContentVersion;
    }

    /**
     * The current code version
     */
    public String getCurrentCodeVersion() {
        return currentCodeVersion;
    }

    /**
     * The current code version
     */
    public void setCurrentCodeVersion(String currentCodeVersion) {
        this.currentCodeVersion = currentCodeVersion;
    }

    /**
     * The current content version
     */
    public String getCurrentContentVersion() {
        return currentContentVersion;
    }

    /**
     * The current content version
     */
    public void setCurrentContentVersion(String currentContentVersion) {
        this.currentContentVersion = currentContentVersion;
    }

    /**
     * The size of the synch package to update the code.
     */
    public long getCodeSynchSize() {
        return codeSynchSize;
    }

    /**
     * The size of the synch package to update the code.
     */
    public void setCodeSynchSize(long codeSynchSize) {
        this.codeSynchSize = codeSynchSize;
    }

    /**
     * The size of the synch package to update the content.
     */
    public long getContentSynchSize() {
        return contentSynchSize;
    }

    /**
     * The size of the synch package to update the content.
     */
    public void setContentSynchSize(long contentSynchSize) {
        this.contentSynchSize = contentSynchSize;
    }
}

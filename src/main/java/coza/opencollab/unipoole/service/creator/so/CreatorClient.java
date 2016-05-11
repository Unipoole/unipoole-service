package coza.opencollab.unipoole.service.creator.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import java.io.Serializable;

/**
 * The result of client creator actions.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class CreatorClient extends CreatorResponse implements Serializable{
    /**
     * The client name.
     */
    private String name;
    /**
     * The download key.
     */
    private String downloadKey;
    /**
     * The url of the client.
     */
    private String url;

    /**
     * Default constructor
     */
    public CreatorClient(){}
    
    /**
     * Constructor setting the status.
     */
    public CreatorClient(ServiceCallStatus status){
        super(status);
    }
    
    /**
     * Copy constructor
     */
    public CreatorClient(ServiceObject other){
        super(other);
    }

    /**
     * The client name.
     */
    public String getName() {
        return name;
    }

    /**
     * The client name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The download key.
     */
    public String getDownloadKey() {
        return downloadKey;
    }

    /**
     * The download key.
     */
    public void setDownloadKey(String downloadKey) {
        this.downloadKey = downloadKey;
    }
    
    /**
     * The url of the client.
     */
    public String getUrl() {
        return url;
    }

    /**
     * The url of the client.
     */
    public void setUrl(String url) {
        this.url = url;
    }
}

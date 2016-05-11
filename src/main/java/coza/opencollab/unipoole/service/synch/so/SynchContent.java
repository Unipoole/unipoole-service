package coza.opencollab.unipoole.service.synch.so;

import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.ServiceObject;
import java.util.Arrays;

/**
 * This represents any content transfered/synched between server
 * and client.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public class SynchContent extends ServiceObject{
    /**
     * What content mime type this is.
     * See: http://webdesign.about.com/od/multimedia/a/mime-types-by-content-type.htm
     */
    private String mimeType;
    /**
     * The content name. This will usually be a file name.
     */
    private String contentName;
    /**
     * The size of the content.
     */
    private long size;
    /**
     * The content being transfered
     */
    private byte[] content;
    /**
     * The content being transfered in String format if zipped binary not used
     */
    private String contentString;
    /**
     * The content version
     */
    private String version;
    
    /**
     * Default constructor
     */
    public SynchContent(){}
    
    /**
     * Constructor setting the status.
     */
    public SynchContent(ServiceCallStatus statusCode){
        super(statusCode);
    }
    
    /**
     * Copy constructor
     */
    public SynchContent(ServiceObject other){
        super(other);
    }

    /**
     * What content mime type this is.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * What content mime type this is.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * The content name. This will usually be a file name.
     */
    public String getContentName() {
        return contentName;
    }

    /**
     * The content name. This will usually be a file name.
     */
    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    /**
     * The size of the content.
     */
    public long getSize() {
        return size;
    }

    /**
     * The size of the content.
     */
    public void setSize(long size) {
        this.size = size;
    }
    
    /**
     * The content being transfered
     */
    public byte[] getContent() {
        return content == null?null:Arrays.copyOf(content, content.length);
    }

    /**
     * The content being transfered
     */
    public void setContent(byte[] content) {
        if (content != null) {
            this.content = Arrays.copyOf(content, content.length);
        }else{
            this.content = null;
        }
    }

    /**
     * The content version
     */
    public String getVersion() {
        return version;
    }

    /**
     * The content version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the contentString
     */
    public String getContentString() {
        return contentString;
    }

    /**
     * @param contentString the contentString to set
     */
    public void setContentString(String contentString) {
        this.contentString = contentString;
    }
    
}

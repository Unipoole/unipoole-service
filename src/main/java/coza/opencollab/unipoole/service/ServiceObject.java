package coza.opencollab.unipoole.service;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * The base service object.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceObject implements Serializable{
    
    /**
     * The status of the call.
     */
    private ServiceCallStatus status = ServiceCallStatus.SUCCESS;
    /**
     * The message for the call.
     * Note that this could be null if status is successful
     */
    private String message = "";
    /**
     * The errorCode, if a error occured.
     */
    private int errorCode = 0;
    /**
     * A instruction on what to do when a error occured.
     */
    private String instruction = "";
    
    /**
     * Default Constructor
     */
    public ServiceObject(){}
    
    /**
     * Constructor setting the Status Code.
     */
    public ServiceObject(ServiceCallStatus statusCode){
        this.status = statusCode;
    }
    
    /**
     * Copy constructor
     */
    public ServiceObject(ServiceObject other){
        this.status = other.status;
        this.message = other.message;
        this.instruction = other.instruction;
        this.errorCode = other.errorCode;
    }

    /**
     * The status of the call.
     */
    public ServiceCallStatus getStatus() {
        return status;
    }

    /**
     * The status of the call.
     */
    public void setStatus(ServiceCallStatus status) {
        this.status = status;
    }
    
    /**
     * Whether the status of the call was successful.
     */
    @JsonIgnore
    public boolean isSuccessful(){
        return ServiceCallStatus.SUCCESS.equals(status);
    }

    /**
     * The message for the call.
     * Note that this could be null if status is successful
     */
    public String getMessage() {
        return message;
    }

    /**
     * The message for the call.
     * Note that this could be null if status is successful
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Add a message to the existing message.
     * <p>
     * If there is no existing message this message is set as the 
     * object message. If there is a existing message this message 
     * is added to the end of the existing message.
     * 
     * @param message New message to add.
     */
    @JsonIgnore
    public void addMessage(String message){
        if(this.message != null){
            setMessage(getMessage()
                    + "\n"
                    + message);
        }else{
            setMessage(message);
        }
    }

    /**
     * The errorCode, if a error occured.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * The errorCode, if a error occured.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * A instruction on what to do when a error occured.
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * A instruction on what to do when a error occured.
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}

package coza.opencollab.unipoole.service;

import coza.opencollab.unipoole.Messages;
import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.UnipooleException;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The base of all controllers used. Just some common code.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class BaseController implements Defaults {

    /**
     * The logger.
     */
    private static final Logger log = Logger.getLogger(BaseController.class);

    /**
     * Adds a message to the message variable of the service object to inform the caller that the method used is
     * deprecated.
     *
     * @param what What is deprecated. Ex. LoginService.login
     * @param serviceObject The service object to update.
     */
    protected void addDeprecateMessage(String what, ServiceObject serviceObject) {
        serviceObject.setErrorCode(DEPRECATED);
        serviceObject.addMessage(what + " is deprecated and must not be used, it will be removed.");
        serviceObject.setInstruction(Messages.getErrorInstruction(serviceObject.getErrorCode()));
    }

    /**
     * The exception handler to be called if there is a exception in one of the service methods. This is managed by
     * spring and does not need to be called directly. This is a general exception handler
     */
    @ExceptionHandler
    public @ResponseBody ServiceObject handleException(Exception e) {
        log.error("Unsuccessful service call!", e);
        ServiceObject so = new ServiceObject(ServiceCallStatus.EXCEPTION);
        if (e.getLocalizedMessage() == null) {
            so.setMessage("Server Exception occured");
        }else{
            so.setMessage(e.getLocalizedMessage());
        }
        if(UnipooleException.class.isInstance(e)){
            so.setErrorCode(UnipooleException.class.cast(e).getErrorCode());
        }else{
            so.setErrorCode(GENERAL);
        }
        so.setInstruction(Messages.getErrorInstruction(so.getErrorCode()));
        return so;
    }
}

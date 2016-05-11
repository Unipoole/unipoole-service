package coza.opencollab.unipoole.service.code;

import static coza.opencollab.unipoole.util.JsonParser.*;
import coza.opencollab.unipoole.service.BaseController;
import coza.opencollab.unipoole.service.creator.so.CreatorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The controller for Unipoole Code Services.
 * <p>
 * Manages data about available client code.
 * <p>
 * This class handle RestFull service calls using JSON.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
@Controller
@RequestMapping("/service-code")
public class CodeController extends BaseController{
    /**
     * The Code Service injected by Spring
     */
    @Autowired
    private CodeService codeService;
    
    /**
     * Register a new client or version of the client.
     * 
     * @param body Json containing a 'location' field with the location of the client code.
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @return The result of the registration
     */
    @RequestMapping(value = "/new/client/{toolVersion:.+}", method = RequestMethod.PUT)
    public @ResponseBody CreatorResponse newClient(@RequestBody String body, @PathVariable String toolName, @PathVariable String toolVersion){
        return newTool(body, DEFAULT_CLIENT_BASE_NAME, toolVersion);
    }
    
    /**
     * Register a new tool or version of the tool.
     * 
     * @param body Json containing a 'location' field with the location of the tool code.
     * @param toolName The tool name.
     * @param toolVersion The tool version.
     * @return The result of the registration
     */
    @RequestMapping(value = "/new/{toolName:.+}/{toolVersion:.+}", method = RequestMethod.PUT)
    public @ResponseBody CreatorResponse newTool(@RequestBody String body, @PathVariable String toolName, @PathVariable String toolVersion){
        String location = parseJson(body, "location");
        return codeService.loadTool(toolName, toolVersion, location);
    }
}

package coza.opencollab.unipoole.service.event;

/**
 * All available events. 
 * <p>
 * Events may be used that is not in this class, this is
 * just for convenience.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface EventCodes {
    public static final String USER_DEVICE_REGISTRATION = "uni.user.device.reg";
    public static final String USER_DEVICE_REACTIVATE = "uni.user.device.reactivate";
    public static final String USER_MODULE_REGISTRATION = "uni.user.module.reg";
    public static final String USER_MODULE_REACTIVATE = "uni.user.module.reactivate";
    public static final String TOOL_VERSION_SET = "uni.tool.version.set";
    public static final String TOOL_VERSION_UPDATE = "uni.tool.version.update";
    public static final String CONTENT_VERSION_SET = "uni.content.version.set";
    public static final String CONTENT_VERSION_UPDATE = "uni.content.version.update";
    public static final String CREATOR_RETRIEVE_MODULES = "uni.creator.modules";
    public static final String CREATOR_RETRIEVE_TOOLS = "uni.creator.tools";
    public static final String CREATOR_ADD_MANAGED_MODULE = "uni.add.managed.module";
    public static final String SYNCH_TOOL_REQUEST = "uni.tool.synch.request";
    public static final String SYNCH_CONTENT_REQUEST = "uni.content.synch.request";
}

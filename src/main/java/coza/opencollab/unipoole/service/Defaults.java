package coza.opencollab.unipoole.service;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Just some random used defaults
 * 
 * @author OpenCollab
 */
public interface Defaults {
    /**
     * The default locale.
     */
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    /**
     * The default charset.
     */
    public static final Charset UTF8 = Charset.forName("UTF-8");
    /**
     * The master device id.
     */
    public static final String MASTER_USERNAME = "_UNIPOOLE";
    /**
     * The master device id.
     */
    public static final String MASTER_DEVICE_ID = "MASTER";
    /**
     * The master module id.
     */
    public static final String MASTER_MODULE_ID = "MASTER";
    /**
     * The client base name. The client is managed just like any tool, so it needs a name.
     */
    public static final String DEFAULT_CLIENT_BASE_NAME = "client.base";
    /**
     * The welcome tool name.
     */
    public static final String WELCOME_TOOL_NAME = "unisa.welcome";
}

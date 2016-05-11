package coza.opencollab.unipoole.service.auth;

import coza.opencollab.unipoole.service.auth.so.Login;
import coza.opencollab.unipoole.service.auth.so.Register;
import coza.opencollab.unipoole.service.synch.so.SynchStatus;

/**
 * The api for a authentication handler. Implementing classes will handle all
 * login and registrations for clients.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface AuthenticationService {
    /**
     * Attempt to log the user in to the lms using the credentials passed.
     * A successful login will return a object with a SUCCESS status
     * and a sessionId. 
     * A unsuccessful login will return a object with a ERROR status
     * and a message. 
     * 
     * @param username The username of the user to login
     * @param password The password of the user to login
     * @return The login object, never null.
     */
    public Login login(String username, String password);
    /**
     * Log a user out of the lms.
     * 
     * @param login The Login object with a valid session id.
     * @return A updated login object .
     */
    public Login logout(Login login);
    
    /**
     * Encrypt a password.
     * @param password Plain text password to encrypt.
     * @return Encrypted password.
     */
    public String encryptPassword(String password);
    
    /**
     * Decrypt a password.
     * @param encryptedPassword Encrypted password.
     * @return Decrypted password in plain text.
     */
    public String decryptPassword(String encryptedPassword);

    /**
     * Attempt to register the user for a module. 
     * A successful registration will return a object with a SUCCESS status. 
     * A unsuccessful registration will return a object with a ERROR status
     * and a message. 
     * 
     * @param username The username of the user.
     * @param password The password of the user.
     * @param synchStatus The synch status containing the device id, module id, tool versions and content versions of the device.
     * @return The register object, never null.
     */
    public Register register(String username, String password, SynchStatus synchStatus);
}

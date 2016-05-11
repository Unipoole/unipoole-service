package coza.opencollab.unipoole.service.auth.impl;

import static coza.opencollab.unipoole.service.ErrorCodes.*;
import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.ServiceCallStatus;
import coza.opencollab.unipoole.service.auth.AuthenticationService;
import coza.opencollab.unipoole.service.dbo.DeviceRegistration;
import coza.opencollab.unipoole.service.auth.so.Login;
import coza.opencollab.unipoole.service.auth.so.Register;
import coza.opencollab.unipoole.service.dao.Dao;
import coza.opencollab.unipoole.service.dbo.ModuleRegistration;
import coza.opencollab.unipoole.service.event.EventCodes;
import coza.opencollab.unipoole.service.event.EventService;
import coza.opencollab.unipoole.service.lms.LMSClient;
import coza.opencollab.unipoole.service.report.so.Status;
import coza.opencollab.unipoole.service.synch.SynchService;
import coza.opencollab.unipoole.service.synch.so.SynchStatus;

import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * The default authentication service. Authentication is done against Sakai, registration saved in database.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class DefaultAuthenticationService implements AuthenticationService {
    /**
     * The service to log any events
     */
    @Autowired
    private EventService eventService;
    /**
     * The synch service.
     */
    @Autowired
    private SynchService synchService;
    /**
     * The lms client to connect to
     */
    @Autowired
    private LMSClient lmsClient;
    /**
     * The dao
     */
    @Autowired
    private Dao dao;
    
    /**
     * Algorithm to use for encryption
     */
    private String encryptionAlgorithm;
    
    /**
     * Key used for encryption
     */
    private String encryptionKey;
    
    /**
     * Transformation to use for encryption
     */
    private String encryptionTransformation;

    /**
     * {@inheritDoc}
     */
    @Override
    public Login login(String username, String password) {
        //Events: Will be logged by the service.
        Login login = new Login(ServiceCallStatus.SUCCESS);
        //login against the LMS
        String sessionId = lmsClient.login(username, password);
        if(sessionId == null){
            login.setStatus(ServiceCallStatus.ERROR);
            login.setErrorCode(INVALID_CREDENTIALS);
            login.setMessage("Not a valid login.");
        }else{
            login.setSessionId(sessionId);
            login.setAuthToken(encryptPassword(password));
        }
        return login;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Login logout(Login login){
        if(login != null && login.getSessionId() != null && lmsClient.logout(login.getUsername())){
            login.setSessionId(null);
        }
        return login;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Register register(String username, String password, SynchStatus synchStatus) {
        Register register = new Register(ServiceCallStatus.SUCCESS);
        if(StringUtils.isEmpty(synchStatus.getDeviceId()) || StringUtils.isEmpty(synchStatus.getModuleId())){
            register.setStatus(ServiceCallStatus.ERROR);
            register.setErrorCode(INVALID_VALUE);
            register.setMessage("No device or module id set.");
            return register;
        }
        //first check login
        Login login = login(username, password);
        if (!login.isSuccessful()) {
            return new Register(login);
        }
        //get extra data of the user
        register.setUserDetails(lmsClient.getUserDetails(username));
        DeviceRegistration deviceRegistration = getDeviceRegistration(username, synchStatus.getDeviceId(), login.getSessionId(), register);
        String moduleId = lmsClient.getRegisteredModuleId(username, synchStatus.getModuleId());
        if(moduleId == null){
            register.setStatus(ServiceCallStatus.ERROR);
            register.setErrorCode(USER_NOT_REGISTERED_FOR_MODULE);
            register.setMessage("User is not registered for the module.");
            //deactivate if the user is not registered on the lms but has a module registration.
            ModuleRegistration mr = dao.getModuleRegistration(deviceRegistration, synchStatus.getModuleId());
            if(mr != null){
                dao.deactivate(mr);
                register.addMessage("The module registration was deactivated.");
            }
            return register;
        }else{
            synchStatus.setModuleId(moduleId);
            register.setModuleId(moduleId);
        }
        getModuleRegistration(deviceRegistration, synchStatus.getModuleId(), login.getSessionId(), register);
//        Map<String, Status> tools = synchStatus.getTools();
//        for(Map.Entry<String, Status> entry: tools.entrySet()){
//            synchService.updateCodeVersion(username, synchStatus.getDeviceId(), entry.getKey(), entry.getValue().getClientCodeVersion());
//            synchService.updateContentVersion(username, synchStatus.getDeviceId(), moduleId, entry.getKey(), entry.getValue().getClientContentVersion());
//        }
        logout(login);
        return register;
    }
    
    /**
     * Register a device.
     * <p>
     * If the device is already registered the device registration is
     * just returned. If the device registration was deactivated it
     * is reactivated.
     * 
     * @param username The username of the user.
     * @param deviceId The Id of the device.
     * @param sessionId The current session id
     * @param register The register object.
     * @return The existing or newly created <code>DeviceRegistration</code>
     */
    private DeviceRegistration getDeviceRegistration(String username, String deviceId, String sessionId, Register register){
        //do the device registration
        DeviceRegistration deviceRegistration = dao.getDeviceRegistration(username, deviceId);
        if(deviceRegistration == null){
            deviceRegistration = dao.createDeviceRegistration(username, deviceId);
            eventService.addEventForSession(sessionId, EventCodes.USER_DEVICE_REGISTRATION, deviceId);
        }else if(!deviceRegistration.isActive()){
            deviceRegistration.setActive(true);
            register.addMessage("Device Registration Reactivated");
            eventService.addEventForSession(sessionId, EventCodes.USER_DEVICE_REACTIVATE, deviceId);
        }
        return deviceRegistration;
    }
    
    /**
     * Register a module.
     * <p>
     * If the module is already registered the module registration is
     * just returned. If the module registration was deactivated it
     * is reactivated.
     * 
     * @param deviceRegistration The device registration to link to.
     * @param moduleId The module id that the user must be registered against.
     * @param sessionId The current session id
     * @param register The register object.
     * @return The existing or newly created <code>ModuleRegistration</code>
     */
    private ModuleRegistration getModuleRegistration(DeviceRegistration deviceRegistration, String moduleId, String sessionId, Register register){
        ModuleRegistration moduleRegistration = dao.getModuleRegistration(deviceRegistration, moduleId);
        if(moduleRegistration == null){
            moduleRegistration = dao.createModuleRegistration(deviceRegistration, moduleId);
            eventService.addEventForSession(sessionId, EventCodes.USER_MODULE_REGISTRATION, deviceRegistration.getDeviceId(), moduleId);
        }else if(!moduleRegistration.isActive()){
            register.setStatus(ServiceCallStatus.ERROR);
            register.setErrorCode(USER_DEACTIVATED_FOR_MODULE);
            register.addMessage("User was deactivated for the module.");
        }
        return moduleRegistration;
    }

	@Override
	public String encryptPassword(String password) {
		try{
	        SecretKeySpec skeySpec = new SecretKeySpec(this.encryptionKey.getBytes("UTF-8"),this.encryptionAlgorithm);
	        Cipher cipher = Cipher.getInstance(this.encryptionTransformation);
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	        byte[] encrypted = cipher.doFinal(password.getBytes());
	        return Hex.encodeHexString(encrypted);
		}catch(Exception e){
			throw new UnipooleException(INCORRECT_CONFIG, "Exception while trying to encrypt password", e);
		}
	}

	@Override
	public String decryptPassword(String encryptedPassword) {
		try {
		    SecretKeySpec skeySpec = new SecretKeySpec(this.encryptionKey.getBytes("UTF-8"), this.encryptionAlgorithm);
		    Cipher cipher = Cipher.getInstance(this.encryptionTransformation);
		    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		    byte[] original = cipher.doFinal(Hex.decodeHex(encryptedPassword.toCharArray()));
		    return new String(original);
		} catch (Exception e) {
			throw new UnipooleException(INVALID_CREDENTIALS, "Exception while trying to decrypt password", e);
		}
	}

	/**
	 * @param encryptionAlgorithm the encryptionAlgorithm to set
	 */
	public void setEncryptionAlgorithm(String encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	/**
	 * @param encryptionKey the encryptionKey to set
	 */
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	/**
	 * @param encryptionTransformation the encryptionTransformation to set
	 */
	public void setEncryptionTransformation(String encryptionTransformation) {
		this.encryptionTransformation = encryptionTransformation;
	}
	
	
}

package coza.opencollab.unipoole.service.mail;

/**
 * A email service.
 * 
 * @author OpenCollab
 * @since 1.0.0
 */
public interface MailService {
    /**
     * Send a email with the given parameters.
     * 
     * @param to The to address. Can be a comma delimited list.
     * @param subject The email subject.
     * @param text The email message.
     */
    public void sendMail(String to, String subject, String text);
    /**
     * Send a email with the given parameters.
     * 
     * @param to The to address. Can be a comma delimited list.
     * @param cc The cc address. Can be a comma delimited list.
     * @param subject The email subject.
     * @param text The email message.
     */
    public void sendMail(String to, String cc, String subject, String text);
    /**
     * Send a email with the given parameters.
     * 
     * @param to The to address. Can be a comma delimited list.
     * @param cc The cc address. Can be a comma delimited list.
     * @param bcc The bcc address. Can be a comma delimited list.
     * @param subject The email subject.
     * @param text The email message.
     */
    public void sendMail(String to, String cc, String bcc, String subject, String text);
    /**
     * Send a email using a template.
     * 
     * @param to The to address. Can be a comma delimited list.
     * @param templateName The template name.
     * @param parameters The parameters to place in the template.
     */
    public void sendMail(String to, String templateName, Object... parameters);
    /**
     * Send a email using a template.
     * 
     * @param to The to address. Can be a comma delimited list.
     * @param cc The cc address. Can be a comma delimited list.
     * @param templateName The template name.
     * @param parameters The parameters to place in the template.
     */
    public void sendMail(String to, String cc, String templateName, Object... parameters);
    /**
     * Send a email using a template.
     * 
     * @param to The to address. Can be a comma delimited list.
     * @param cc The cc address. Can be a comma delimited list.
     * @param bcc The bcc address. Can be a comma delimited list.
     * @param templateName The template name.
     * @param parameters The parameters to place in the template.
     */
    public void sendMail(String to, String cc, String bcc, String templateName, Object... parameters);
}

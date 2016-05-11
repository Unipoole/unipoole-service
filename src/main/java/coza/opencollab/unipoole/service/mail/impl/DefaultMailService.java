package coza.opencollab.unipoole.service.mail.impl;

import coza.opencollab.unipoole.UnipooleException;
import coza.opencollab.unipoole.service.Defaults;
import coza.opencollab.unipoole.service.ErrorCodes;
import coza.opencollab.unipoole.service.mail.MailService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import org.masukomi.aspirin.Aspirin;
import org.masukomi.aspirin.core.config.Configuration;
import org.masukomi.aspirin.core.listener.AspirinListener;
import org.masukomi.aspirin.core.listener.ResultState;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * The default mail service implementation.
 *
 * @author OpenCollab
 * @since 1.0.0
 */
public class DefaultMailService implements MailService, AspirinListener {

    /**
     * The logger.
     */
    private static Logger LOG = Logger.getLogger(DefaultMailService.class);
    /**
     * The spring mail sender.
     */
    private JavaMailSender mailSender;
    /**
     * Whether the service must ignore exceptions.
     */
    private boolean failSoftly = true;
    /**
     * The default from address.
     */
    private String defaultFrom;
    /**
     * The default reply to address.
     */
    private String defaultReplyTo;
    /**
     * The template directory.
     */
    private String templateDirectory;

    /**
     * The spring mail sender.
     */
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Whether the service must ignore exceptions.
     */
    public boolean isFailSoftly() {
        return failSoftly;
    }

    /**
     * Whether the service must ignore exceptions.
     */
    public void setFailSoftly(boolean failSoftly) {
        this.failSoftly = failSoftly;
    }

    /**
     * The default from address.
     */
    public void setDefaultFrom(String defaultFrom) {
        this.defaultFrom = defaultFrom;
    }

    /**
     * The default reply to address.
     */
    public void setDefaultReplyTo(String defaultReplyTo) {
        this.defaultReplyTo = defaultReplyTo;
    }

    /**
     * The template directory.
     */
    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    /**
     * Initiation method.
     */
    @PostConstruct
    public void init() {
        Configuration configuration = Aspirin.getConfiguration();
        configuration.setEncoding(Defaults.UTF8.name());
        configuration.setDeliveryDebug(true);
        Aspirin.addListener(this);
    }

    /**
     * Destroy method.
     */
    @PreDestroy
    public void destroy() {
        Aspirin.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(String to, String subject, String text) {
        sendMail(to, null, null, subject, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(String to, String cc, String subject, String text) {
        sendMail(to, cc, null, subject, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(String to, String cc, String bcc, String subject, String text) {
        sendMail(createMessage(to, cc, bcc, subject, text));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(String to, String templateName, Object... parameters) {
        sendMail(to, null, null, templateName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(String to, String cc, String templateName, Object... parameters) {
        sendMail(to, cc, null, templateName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMail(String to, String cc, String bcc, String templateName, Object... parameters) {
        sendMail(to, cc, bcc, getTemplateSubject(templateName, parameters), getTemplateText(templateName, parameters));
    }

    /**
     * Get the subject from the template.
     */
    private String getTemplateSubject(String templateName, Object... parameters) {
        try {
            return MessageFormat.format(
                    FileCopyUtils.copyToString(new FileReader(new File(templateDirectory, templateName + ".subject"))), parameters);
        } catch (IOException e) {
            LOG.warn("Could not read the template file.", e);
            return templateName;
        }
    }

    /**
     * Get the text from the template.
     */
    private String getTemplateText(String templateName, Object... parameters) {
        try {
            return MessageFormat.format(
                    FileCopyUtils.copyToString(new FileReader(new File(templateDirectory, templateName + ".text"))), parameters);
        } catch (IOException e) {
            LOG.warn("Could not read the template file.", e);
            return templateName + "\n" + Arrays.toString(parameters);
        }
    }

    /**
     * Create a MimeMessage for the parameters.
     */
    private MimeMessage createMessage(String to, String cc, String bcc, String subject, String text) {
        try {
            MimeMessage message = Aspirin.createNewMimeMessage();
            message.setFrom(new InternetAddress(defaultFrom));
            message.setReplyTo(new Address[]{new InternetAddress(defaultReplyTo)});
            message.setSentDate(new Date());
            addRecipients(message, Message.RecipientType.TO, to);
            addRecipients(message, Message.RecipientType.CC, cc);
            addRecipients(message, Message.RecipientType.BCC, bcc);
            message.setSubject(subject);
            message.setText(text);
            return message;
        } catch (Exception e) {
            throw new UnipooleException(ErrorCodes.MAIL_SERVICE, "Unable to create mail message.", e);
        }
    }

    /**
     * Add all the recipients for the type.
     */
    private void addRecipients(MimeMessage message, Message.RecipientType type, String recipients) throws AddressException, MessagingException {
        if (!StringUtils.isEmpty(recipients)) {
            String[] rs = recipients.split(",");
            for (String r : rs) {
                message.addRecipient(type, new InternetAddress(r));
            }
        }
    }

    /**
     * Send a message. First try to use the mail sender, if that does not exist
     * or does not work then use aspirin.
     */
    private void sendMail(MimeMessage message) {
        if (mailSender == null) {
            sendAspirin(message);
            return;
        }
        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            LOG.warn("Could not send the mail.", e);
            if (failSoftly) {
                sendAspirin(message);
            } else {
                throw e;
            }
        }
    }

    /**
     * Use aspirin to send the mail.
     */
    private void sendAspirin(MimeMessage message) {
        try {
            Aspirin.add(message);
        } catch (MessagingException e) {
            LOG.warn("Could not set the mail content.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delivered(String mailId, String recipient, ResultState state, String resultContent) {
        if (ResultState.FAILED.equals(state)) {
            LOG.warn("Could not sent the mail to " + recipient + " with Aspirin.\n" + resultContent);
        }
    }
}

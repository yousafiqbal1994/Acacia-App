package official.com.windowdetailssharingapp.export.emailsender;

import java.io.File;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import official.com.windowdetailssharingapp.core.db.LocalStorage;

public class GMailSender extends javax.mail.Authenticator {
    private final static String HOST = "smtp.gmail.com";
    private final static String USER = "acacia.emailsender@gmail.com";
    private final static String PASSWORD = "UCworld198";

    static {
        Security.addProvider(new JSSEProvider());
    }

    private Session session;

    public GMailSender() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", HOST);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(USER, PASSWORD);
    }


    public synchronized void sendMail(String subject, String body,
                                      String recipients, String filename) throws Exception {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USER));
        message.setSubject(subject);

        BodyPart bodyTextPart = new MimeBodyPart();
        bodyTextPart.setContent("<html><body>" + body + "</body></html>", "text/html");


        File attachmentFile = new File(LocalStorage.getExportDir(), filename);

        MimeBodyPart bodyAttachmentPart = new MimeBodyPart();
        bodyAttachmentPart.attachFile(attachmentFile);

        DataSource source = new FileDataSource(attachmentFile);
        bodyAttachmentPart.setDataHandler(new DataHandler(source));
        bodyAttachmentPart.setFileName(filename);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyTextPart);
        multipart.addBodyPart(bodyAttachmentPart);
        message.setContent(multipart);

        if (recipients.indexOf(',') > 0) {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        } else {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        }

        Transport.send(message);
    }

}
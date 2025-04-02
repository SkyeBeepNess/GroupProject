package courseSelection.src.courseselection;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHandler {
    private String fromEmail;
    private String password;

    public EmailHandler() {
        Properties props = new Properties();
        try {
            props.load(EmailHandler.class.getClassLoader().getResourceAsStream("email.properties"));
            fromEmail = props.getProperty("email");
            password = props.getProperty("password");
        } catch (Exception e) {
            System.err.println("❌ Failed to load email properties: " + e.getMessage());
            throw new RuntimeException("Cannot load email settings.");
        }
    }

    public void sendConfirmationEmail(String toEmail, String bookingId) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Course Booking Confirmation");
            message.setText("Thank you! Your booking ID is: " + bookingId);

            Transport.send(message);
            System.out.println("✅ Email sent to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}

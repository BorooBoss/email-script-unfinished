import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class emailSender {
    public static void main(String[] args) {
        try {

            String currentDir = System.getProperty("user.dir");
            FileReader reader = new FileReader(currentDir + "/data.json");
            JsonObject emailConfig = JsonParser.parseReader(reader).getAsJsonObject();

            final String sender = emailConfig.get("sender").getAsString();
            final String loginToken = emailConfig.get("loginToken").getAsString();
            final String reciever = emailConfig.get("reciever").getAsString();
            final String subject = emailConfig.get("subject").getAsString();
            final String messageText = emailConfig.get("messageText").getAsString();

            // simple mail transfer protocol configuration
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true"); // auth setup
            properties.put("mail.smtp.starttls.enable", "true"); // TLS encryption for only for 587, not  465
            properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP setup
            properties.put("mail.smtp.port", "587"); // port setup

            // email configuration between SMTP and App
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sender, loginToken);
                }
            });

            if (reciever == null || reciever.isEmpty()) {
                System.out.println("Email reciever is not set");
                return;
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reciever));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (IOException e) {
            System.out.println("Error loading email config: " + e.getMessage());
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

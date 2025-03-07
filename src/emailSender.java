import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class emailSender {
    private static void logEmail(String message) {
        String currentDir = System.getProperty("user.dir");
        try (FileWriter writer = new FileWriter(currentDir + "/sent_message.log", true)) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.append("[" + timeStamp + "] " + message + "\n");
        } catch (IOException e) {
            System.out.println("Error logging email activity: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {

            String currentDir = System.getProperty("user.dir");
            FileReader reader = new FileReader(currentDir + "/data.json");
            JsonObject emailConfig = JsonParser.parseReader(reader).getAsJsonObject();

            final String sender = emailConfig.get("sender").getAsString();
            final String loginToken = emailConfig.get("loginToken").getAsString();
            JsonArray recievers = emailConfig.getAsJsonArray("recievers");
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

            if (recievers == null || recievers.isEmpty()) {
                System.out.println("Email reciever is not set");
                System.out.println(recievers);
                return;
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            for (JsonElement reciever : recievers) {
                String email = reciever.getAsString();
                System.out.println("Sending email to " + email);

                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject(subject);
                message.setText(messageText);

                try {
                    Transport.send(message);
                    logEmail("SUCCESS: Sent email to " + email + " - Subject " + subject);
                    System.out.println("Email sent successfully!");
                } catch (MessagingException e) {
                    System.out.println("Error sending email: " + e.getMessage());
                    logEmail("ERROR: Failed to send email to " + email + "- Reason:" + e.getMessage());
                }
            }
        } catch (IOException | MessagingException e) {
            System.out.println("Error loading email config: " + e.getMessage());
            logEmail("ERROR: Failed to load email config - Reason: " + e.getMessage());

        }
    }
}

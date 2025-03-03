import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class dataLoader {
    public static Properties load(String filePath) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInput = new FileInputStream(filePath);
        properties.load(fileInput);
        fileInput.close();
        return properties;
    }
}
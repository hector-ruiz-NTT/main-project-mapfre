package mapfre;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FrameworkProperties {

    public static Properties props;
    private static String rootPath = ProjectPaths.getRootDirectory();

    private static Properties initializeProperties(String fileName) {
        Properties prop = new Properties();
//        try (InputStream input = new FileInputStream("src/test/resources/" + fileName)) {
        try (InputStream input = new FileInputStream(rootPath + "/properties/" + fileName)) {
            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            Log.info("Loading properties file '{}' failed, check configuration file location");
            ex.printStackTrace();
        }
        return prop;
    }

    private static Properties getDriverConfig() {
        if (props == null) {
            props = initializeProperties("driverConfig.properties");
        }
        return props;
    }

    public static String getProperty(String property) {
        String value = System.getProperty(property);
        if (value == null && getDriverConfig().containsKey(property)) {
            value = getDriverConfig().getProperty(property);
        }
        return value;
    }

    public static void setProperty(String property, String value) {
        if (props == null) {
            props = new Properties();
        }

        props.setProperty(property, value);
        try (FileOutputStream output = new FileOutputStream(rootPath + "/properties/driverConfig.properties")) {
            props.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

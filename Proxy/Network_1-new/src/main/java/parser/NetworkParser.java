package main.java.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import main.java.context.Context;

public class NetworkParser {
    private static final String PORT = "port";
    private static final String NOTIFY_PERIOD = "notify_period";

    private static Properties loadProperties(String file) {
        try (InputStream inputStream = NetworkParser.class.getResourceAsStream(file)) {
             Properties properties = new Properties();
             properties.load(inputStream);
             return properties;
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    public static Context getNetworkContext(String file, String ip_group) {
        Properties properties = loadProperties(file);
        return new Context(ip_group, 
        		Integer.parseInt(properties.getProperty(PORT)), 
        		Integer.parseInt(properties.getProperty(NOTIFY_PERIOD)));
    }
}

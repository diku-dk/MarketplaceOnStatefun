package dk.ku.dms.marketplace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Constants {

    public static final ObjectMapper messageMapper;
    
    public static final String TYPES_NAMESPACE = "marketplace";
    public static final String externalConfigPath = "app.properties";

    public static final boolean logging;
    public static final String connectionString;
    public static final String user;
    public static final String password;
    public static final int maxPoolSize;
    
    public static final int nShipmentPartitions;

    static {
    	boolean logging1 = false;
    	String connection_string1 = "";
    	String user1 = "";
    	String password1 = "";
        int maxPoolSize1 = 25;
        int nShipmentPartitions1 = 1;
        
        messageMapper = new ObjectMapper();
        messageMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Properties prop = new Properties();
        try {

            if (Files.exists(Paths.get(externalConfigPath))) {
                System.out.println("Loading external config file: " + externalConfigPath);
                prop.load(new FileInputStream(externalConfigPath));
            } else {
                System.out.println("Loading internal config file: app.properties");
                prop.load(Constants.class.getClassLoader().getResourceAsStream("app.properties"));
            }

            String str = prop.getProperty("num_shipments");
            nShipmentPartitions1 = Integer.parseInt(str);
            
            str = prop.getProperty("logging");
            logging1 = Boolean.parseBoolean(str);
            
            if (logging1)
            {
            	connection_string1 = prop.getProperty("connection_string");
                user1 = prop.getProperty("user");
                password1 = prop.getProperty("password");
                maxPoolSize1 = Integer.parseInt(prop.getProperty("max_pool_size"));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        logging = logging1;
        connectionString = connection_string1;
        user = user1;
        password = password1;
        nShipmentPartitions = nShipmentPartitions1;
        maxPoolSize = maxPoolSize1;

        System.out.println("NUM_SHIPMENTS: " + nShipmentPartitions);
        System.out.println("logging: " + logging);
        System.out.println("connection_string: " + connectionString);
        System.out.println("user: " + user);
        System.out.println("password: " + password);
        System.out.println("max_pool_size: " + maxPoolSize);
    }
}

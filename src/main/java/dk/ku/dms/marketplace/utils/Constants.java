package dk.ku.dms.marketplace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Properties;

public class Constants {

    public static final ObjectMapper messageMapper;
    
    public static final String TYPES_NAMESPACE = "marketplace";

    public static final boolean logging;
    public static final String connection_string;
    public static final String user;
    public static final String password;
    
    public static final int nShipmentPartitions;

    static {
    	boolean logging1 = false;
    	String connection_string1 = "";
    	String user1 = "";
    	String password1 = "";
        int nShipmentPartitions1 = 1;
        
        messageMapper = new ObjectMapper();
        messageMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Properties prop = new Properties();
        try {
            prop.load(Constants.class.getClassLoader().getResourceAsStream("app.properties"));
            String str = prop.getProperty("num_shipments");
            nShipmentPartitions1 = Integer.parseInt(str);
            
            str = prop.getProperty("logging");
            logging1 = Boolean.parseBoolean(str);
            
            if (logging1)
            {
            	connection_string1 = prop.getProperty("connection_string");
                user1 = prop.getProperty("user");
                password1 = prop.getProperty("password");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        logging = logging1;
        connection_string = connection_string1;
        user = user1;
        password = password1;
        nShipmentPartitions = nShipmentPartitions1;
        
        System.out.println("NUM_SHIPMENTS: " + nShipmentPartitions);
        System.out.println("logging: " + logging);
        System.out.println("connection_string: " + connection_string);
        System.out.println("user: " + user);
        System.out.println("passsword: " + password);
    }
}

package dk.ku.dms.marketplace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Properties;

public class Constants {

    public static final ObjectMapper messageMapper;

    static {
        int nShipmentPartitions1;
        messageMapper = new ObjectMapper();
        messageMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Properties prop = new Properties();
        try {
            prop.load(Constants.class.getClassLoader().getResourceAsStream("app.properties"));
            String str = prop.getProperty("num_shipments");
            System.out.println("NUM_SHIPMENTS: " +str);
            nShipmentPartitions1 = Integer.parseInt(str);
        } catch (IOException e) {
            e.printStackTrace();
            nShipmentPartitions1 = 1;
        }
        nShipmentPartitions = nShipmentPartitions1;
    }

    public static final String TYPES_NAMESPACE = "marketplace";

    public static final int nShipmentPartitions;

}

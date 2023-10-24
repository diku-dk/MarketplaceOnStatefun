package dk.ku.dms.marketplace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Constants {

    public static final ObjectMapper messageMapper;

    static {
        messageMapper = new ObjectMapper();
        messageMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static final String TYPES_NAMESPACE = "marketplace";

    public static final int nShipmentPartitions = 100;

}

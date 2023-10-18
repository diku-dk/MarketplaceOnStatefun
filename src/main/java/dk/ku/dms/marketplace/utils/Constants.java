package dk.ku.dms.marketplace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static final String TYPES_NAMESPACE = "marketplace.types";
    public static final String FUNCTIONS_NAMESPACE = "marketplace.fns";

    public static final int nShipmentPartitions = 100;

}

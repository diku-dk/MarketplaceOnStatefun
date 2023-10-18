package dk.ku.dms.marketplace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static final String TYPES_NAMESPACE = "marketplace.types";
    public static final String FUNCTIONS_NAMESPACE = "marketplace.fns";
    public static final String EGRESS_NAMESPACE = "io.statefun.playground";
    public static final int no = 1000; // 瞎写的
//    public static final int nProductPartitions = 1000;
    public static final int nShipmentPartitions = 100;
//    public static final int nOrderPartitions = 1000;
//    public static final int nStockPartitions = 1000;
//    public static final int nPaymentPartitions = 1000;
//    public static final int nCustomerPartitions = 1000000;
}

//package dk.ku.dms.marketplace.states;
//
//import dk.ku.dms.marketplace.entities.OrderEntry;
//import dk.ku.dms.marketplace.entities.Seller;
//import dk.ku.dms.marketplace.utils.Constants;
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.apache.flink.statefun.sdk.java.TypeName;
//import org.apache.flink.statefun.sdk.java.types.SimpleType;
//import org.apache.flink.statefun.sdk.java.types.Type;
//
//import java.util.HashSet;
//import java.util.Set;
//
//
//public class SellerState {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    public static final Type<SellerState> TYPE =
//            SimpleType.simpleImmutableTypeFrom(
//                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "SellerState"),
//                    mapper::writeValueAsBytes,
//                    bytes -> mapper.readValue(bytes, SellerState.class));
//
//    @JsonProperty("seller")
//    public Seller seller;
//
//    // entry in process, only for INVOICED / PAYMENT_PORCESSED / READY_FOR_SHIPMENT / IN_TRANSIT
//    @JsonProperty("orderEntries")
//    public Set<OrderEntry> orderEntries;
//
//    @JsonCreator
//    public SellerState() {
//        this.seller = new Seller();
////        this.orderEntriesHistory = new HashSet<>();
//        this.orderEntries = new HashSet<>();
////        this.orderEntryDetails = new java.util.HashMap<>();
//    }
//
//    @JsonIgnore
//    public void addOrderEntry(OrderEntry orderEntry) {
//        this.orderEntries.add(orderEntry);
//    }
//
//    @JsonIgnore
//    public void moveOrderEntryToHistory(int orderEntryId) {
//        OrderEntry orderEntry = this.orderEntries.stream().filter(o -> o.getOrder_id() == orderEntryId).findFirst().get();
//        this.orderEntries.remove(orderEntry);
////        this.orderEntriesHistory.add(orderEntry);
//    }
//
//}
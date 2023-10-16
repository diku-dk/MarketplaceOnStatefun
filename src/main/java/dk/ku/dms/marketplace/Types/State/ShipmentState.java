package dk.ku.dms.marketplace.Types.State;

import dk.ku.dms.marketplace.common.Entity.PackageItem;
import dk.ku.dms.marketplace.common.Entity.Shipment;
import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.constants.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
public class ShipmentState {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ShipmentState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ShipmentState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ShipmentState.class));

    @JsonProperty("shipments") private Map<Integer, Shipment> shipments = new HashMap<>();
    @JsonProperty("packages") private Map<Integer, List<PackageItem>> packages = new HashMap<>();

    @JsonIgnore
    public void addShipment(int orderId, Shipment shipment) {
        shipments.put(orderId, shipment);
    }

    @JsonIgnore
    public void addPackage(int orderId, List<PackageItem> packageItem) {
        packages.put(orderId, packageItem);
    }

    @JsonIgnore
    public Map<Integer, Integer> GetOldestOpenShipmentPerSeller() {
        Map<Integer, Integer> q = this.packages.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getPackageStatus().equals(Enums.PackageStatus.SHIPPED))
                .collect(Collectors.groupingBy(PackageItem::getSellerId,
//                        Collectors.minBy(Comparator.comparingLong(PackageItem::getShipmentId))))
                        Collectors.minBy(Comparator.comparingInt(PackageItem::getShipmentId))))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
//                .sorted(Comparator.comparingLong(entry -> entry.getValue().get().getShipmentId()))
                .sorted(Comparator.comparingInt(entry -> entry.getValue().get().getShipmentId()))
                .limit(10) // Limit the result to the first 10 sellers
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get().getShipmentId()));
        return q;
    }

    @JsonIgnore
    public List<PackageItem> GetShippedPackagesByShipmentIDAndSeller(int sellerId, int shipmentId) {
        List<PackageItem> packagesForSeller = packages.get(shipmentId).stream()
                .filter(p -> p.getSellerId() == sellerId
//                        && p.getShipmentId() == shipmentId
                        && p.getPackageStatus().equals(Enums.PackageStatus.SHIPPED))
                .collect(Collectors.toList());
        return packagesForSeller;
    }

    @JsonIgnore
    public int GetTotalDeliveredPackagesForShipment(int shipmentId) {
        int countDelivered = (int) packages.get(shipmentId).stream()
                .filter(p -> p.getPackageStatus() == Enums.PackageStatus.DELIVERED)
                .count();
        return countDelivered;
    }
}

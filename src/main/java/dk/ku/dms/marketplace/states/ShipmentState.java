package dk.ku.dms.marketplace.states;

import dk.ku.dms.marketplace.entities.Package;
import dk.ku.dms.marketplace.entities.Shipment;
import dk.ku.dms.marketplace.utils.Constants;
import dk.ku.dms.marketplace.utils.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ShipmentState {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ShipmentState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ShipmentState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ShipmentState.class));

    @JsonProperty("shipments")
    private final Map<String, Shipment> shipments;

    @JsonProperty("packages")
    private final Map<String, List<Package>> packages;

    public ShipmentState() {
        this.shipments = new HashMap<>();
        this.packages = new HashMap<>();
    }

    @JsonIgnore
    public Map<Integer, Integer> getOldestOpenShipmentPerSeller() {
        Map<Integer, Integer> q = this.packages.values().stream()
                .flatMap(List::stream)
                .filter(p -> p.getPackageStatus().equals(Enums.PackageStatus.SHIPPED))
                .collect(Collectors.groupingBy(Package::getSellerId,
//                        Collectors.minBy(Comparator.comparingLong(PackageItem::getShipmentId))))
                        Collectors.minBy(Comparator.comparingInt(Package::getShipmentId))))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
//                .sorted(Comparator.comparingLong(entry -> entry.getValue().get().getShipmentId()))
                .sorted(Comparator.comparingInt(entry -> entry.getValue().get().getShipmentId()))
                .limit(10) // Limit the result to the first 10 sellers
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get().getShipmentId()));
        return q;
    }

    @JsonIgnore
    public List<Package> getShippedPackagesByShipmentIDAndSeller(int sellerId, int shipmentId) {
        List<Package> packagesForSeller = packages.get(shipmentId).stream()
                .filter(p -> p.getSellerId() == sellerId
//                        && p.getShipmentId() == shipmentId
                        && p.getPackageStatus().equals(Enums.PackageStatus.SHIPPED))
                .collect(Collectors.toList());
        return packagesForSeller;
    }

    @JsonIgnore
    public long getTotalDeliveredPackagesForShipment(String shipmentId) {
        return packages.get(shipmentId).stream()
                .filter(p -> p.getPackageStatus() == Enums.PackageStatus.DELIVERED)
                .count();
    }
}

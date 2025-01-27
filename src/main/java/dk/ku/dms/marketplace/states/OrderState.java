package dk.ku.dms.marketplace.states;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ku.dms.marketplace.entities.Order;
import dk.ku.dms.marketplace.entities.OrderHistory;
import dk.ku.dms.marketplace.entities.OrderItem;
import dk.ku.dms.marketplace.messages.order.CheckoutRequest;
import dk.ku.dms.marketplace.utils.Constants;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OrderState {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<OrderState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "OrderState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, OrderState.class));

    @JsonProperty("checkouts")
    private final Map<Integer, CheckoutRequest> checkouts;

    @JsonProperty("orders")
    private final Map<Integer, Order> orders;

    @JsonProperty("orderItems")
    private final Map<Integer, List<OrderItem>> orderItems;

    @JsonProperty("orderHistory")
    private final Map<Integer, List<OrderHistory>> orderHistory;

    @JsonProperty("remainingAcksMap")
    private final Map<Integer, Integer> remainingAcksMap;

    @JsonProperty("inStockItems")
    public final Map<Integer, List<Integer>> inStockItems;

    @JsonCreator
    public OrderState(@JsonProperty("checkouts") Map<Integer, CheckoutRequest> checkouts,
                      @JsonProperty("orders") Map<Integer, Order> orders,
                      @JsonProperty("orderItems") Map<Integer, List<OrderItem>> orderItems,
                      @JsonProperty("orderHistory") Map<Integer, List<OrderHistory>> orderHistory,
                      @JsonProperty("remainingAcksMap") Map<Integer, Integer> remainingAcksMap,
                      @JsonProperty("inStockItems") Map<Integer, List<Integer>> inStockItems) {
        this.checkouts = checkouts;
        this.orders = orders;
        this.orderItems = orderItems;
        this.orderHistory = orderHistory;
        this.remainingAcksMap = remainingAcksMap;
        this.inStockItems = inStockItems;
    }

    private OrderState(){
        this.checkouts = new HashMap<>();
        this.orders = new HashMap<>();
        this.orderItems = new HashMap<>();
        this.orderHistory = new HashMap<>();
        this.remainingAcksMap = new HashMap<>();
        this.inStockItems = new HashMap<>();
    }

    public static OrderState build(){
        return new OrderState();
    }

    @JsonIgnore
    public Map<Integer, Order> getOrders() {
        return orders;
    }

    @JsonIgnore
    public Map<Integer, CheckoutRequest> getCheckouts() {
        return checkouts;
    }

    @JsonIgnore
    public Map<Integer, List<OrderItem>> getOrderItems() {
        return orderItems;
    }

    @JsonIgnore
    public Map<Integer, List<OrderHistory>> getOrderHistory() {
        return orderHistory;
    }

    public void addOrder(int orderId, Order order, List<OrderItem> items, OrderHistory orderHistory) {
    	this.orders.put(orderId, order);
    	this.orderItems.put(orderId, items);
    	
    	List<OrderHistory> history = new ArrayList<>();
    	if (this.orderHistory.containsKey(orderId)) history = this.orderHistory.get(orderId);
    	else this.orderHistory.put(orderId, history);
    	
    	history.add(orderHistory);
    }

    public void setUpRemainingAcks(int orderId, int itemCount){
        this.remainingAcksMap.putIfAbsent(orderId, itemCount);
    }

    public void setDownRemainingAcks(int orderId){
        this.remainingAcksMap.remove(orderId);
    }

    @JsonIgnore
    public int decreaseRemainingItems(int orderId){
        return this.remainingAcksMap.computeIfPresent(orderId, (k, v)-> v-1);
    }

    @JsonIgnore
    public int getRemainingItems(int orderId){
        return this.remainingAcksMap.get(orderId);
    }

    public void cleanState(int orderId){
        this.checkouts.remove(orderId);
        this.inStockItems.remove(orderId);
        this.orderHistory.remove(orderId);
        this.orders.remove(orderId);
        this.orderItems.remove(orderId);
    }

}

//package dk.ku.dms.marketplace.states;
//
//import dk.ku.dms.marketplace.entities.CartItem;
//import dk.ku.dms.marketplace.utils.Constants;
//import dk.ku.dms.marketplace.utils.Enums;
//import dk.ku.dms.marketplace.messages.stock.AttemptReservationEvent;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.flink.statefun.sdk.java.TypeName;
//import org.apache.flink.statefun.sdk.java.types.SimpleType;
//import org.apache.flink.statefun.sdk.java.types.Type;
//
//import java.util.*;
//
//public class ReserveStockTaskState {
//    
//
//    public static final Type<ReserveStockTaskState> TYPE =
//            SimpleType.simpleImmutableTypeFrom(
//                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "OrderAsyncState"),
//                    mapper::writeValueAsBytes,
//                    bytes -> mapper.readValue(bytes, ReserveStockTaskState.class));
//
//    @JsonProperty("remainSubTaskCnt")
////  how many partitions have not finishe attempting reservation
//    Map<Integer, Integer> remainSubTaskCnt = new HashMap<>();
//
//    @JsonProperty("itemsSuccessResv")
//    Map<Integer, List<CartItem>> itemsSuccessResv = new HashMap<>();
//    @JsonProperty("itemsFailedResv")
//    Map<Integer, List<CartItem>> itemsFailedResv = new HashMap<>();
//
//    @JsonIgnore
//    public void addNewTask(int customerId, int taskNum) {
//        remainSubTaskCnt.put(customerId, taskNum);
////        attemptResTaskList.put(customerId, new ArrayList<>());
//        itemsSuccessResv.put(customerId, new ArrayList<>());
//        itemsFailedResv.put(customerId, new ArrayList<>());
//    }
//
//    @JsonIgnore
//    public Map<Integer, CartItem> getSingleSuccessResvItems(int customerId) {
//        List<CartItem> items = itemsSuccessResv.get(customerId);
//        Map<Integer, CartItem> itemsMap = new HashMap<>();
//        for(CartItem item : items) {
//            itemsMap.put(item.getProductId(), item);
//        }
//        return itemsMap;
//    }
//
//    @JsonIgnore
//    public Map<Integer, CartItem> getSingleFailedResvItems(int customerId) {
//        List<CartItem> items = itemsFailedResv.get(customerId);
//        Map<Integer, CartItem> itemsMap = new HashMap<>();
//        for(CartItem item : items) {
//            itemsMap.put(item.getProductId(), item);
//        }
//        return itemsMap;
//    }
//
//    @JsonIgnore
//    public void addCompletedSubTask(int customerId, AttemptReservationEvent itemRes) {
//        // Decrement the taskNum for the customerId corresponding to attemptResTaskCntList.
//        int taskNum = remainSubTaskCnt.get(customerId);
//        taskNum = taskNum - 1;
//        remainSubTaskCnt.put(customerId, taskNum);
//        // add the checkoutResv to two list
//        List<CartItem> itemsSuccessResv_ = this.itemsSuccessResv.get(customerId);
//        List<CartItem> itemsFailedResv_ = this.itemsFailedResv.get(customerId);
//        if (itemRes.getStatus() == Enums.ItemStatus.IN_STOCK) {
//            itemsSuccessResv_.add(itemRes.getItem());
//        } else {
//            itemsFailedResv_.add(itemRes.getItem());
//        }
//
//        this.itemsSuccessResv.put(customerId, itemsSuccessResv_);
//        this.itemsFailedResv.put(customerId, itemsFailedResv_);
//    }
//
//    @JsonIgnore
//    public boolean isTaskComplete(int customerId) {
//        if (remainSubTaskCnt.get(customerId) == 0) {
//            return true;
//        }
//        return false;
//    }
//
//    @JsonIgnore
//    public void removeTask(int customerId) {
//        remainSubTaskCnt.remove(customerId);
////        attemptResTaskList.remove(customerId);
//        itemsSuccessResv.remove(customerId);
//        itemsFailedResv.remove(customerId);
//    }
//}

package dk.ku.dms.marketplace.types.State;

import dk.ku.dms.marketplace.common.Entity.BasketItem;
import dk.ku.dms.marketplace.constants.Constants;
import dk.ku.dms.marketplace.constants.Enums;
import dk.ku.dms.marketplace.types.MsgToStock.ReserveStockEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.*;

@Setter
@Getter
public class ReserveStockTaskState {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ReserveStockTaskState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "OrderAsyncState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ReserveStockTaskState.class));

    @JsonProperty("remainSubTaskCnt")
//  how many partitions have not finishe attempting reservation
    Map<Integer, Integer> remainSubTaskCnt = new HashMap<>();

    @JsonProperty("itemsSuccessResv")
    Map<Integer, List<BasketItem>> itemsSuccessResv = new HashMap<>();
    @JsonProperty("itemsFailedResv")
    Map<Integer, List<BasketItem>> itemsFailedResv = new HashMap<>();

    @JsonIgnore
    public void addNewTask(int customerId, int taskNum) {
        remainSubTaskCnt.put(customerId, taskNum);
//        attemptResTaskList.put(customerId, new ArrayList<>());
        itemsSuccessResv.put(customerId, new ArrayList<>());
        itemsFailedResv.put(customerId, new ArrayList<>());
    }

    @JsonIgnore
    public Map<Integer, BasketItem> getSingleSuccessResvItems(int customerId) {
        List<BasketItem> items = itemsSuccessResv.get(customerId);
        Map<Integer, BasketItem> itemsMap = new HashMap<>();
        for(BasketItem item : items) {
            itemsMap.put(item.getProductId(), item);
        }
        return itemsMap;
    }

    @JsonIgnore
    public Map<Integer, BasketItem> getSingleFailedResvItems(int customerId) {
        List<BasketItem> items = itemsFailedResv.get(customerId);
        Map<Integer, BasketItem> itemsMap = new HashMap<>();
        for(BasketItem item : items) {
            itemsMap.put(item.getProductId(), item);
        }
        return itemsMap;
    }

    @JsonIgnore
    public void addCompletedSubTask(int customerId, ReserveStockEvent itemRes) {
        // Decrement the taskNum for the customerId corresponding to attemptResTaskCntList.
        int taskNum = remainSubTaskCnt.get(customerId);
        taskNum = taskNum - 1;
        remainSubTaskCnt.put(customerId, taskNum);
        // add the checkoutResv to two list
        List<BasketItem> itemsSuccessResv_ = this.itemsSuccessResv.get(customerId);
        List<BasketItem> itemsFailedResv_ = this.itemsFailedResv.get(customerId);
        if (itemRes.getItemStatus() == Enums.ItemStatus.IN_STOCK) {
            itemsSuccessResv_.add(itemRes.getItem());
        } else {
            itemsFailedResv_.add(itemRes.getItem());
        }

        this.itemsSuccessResv.put(customerId, itemsSuccessResv_);
        this.itemsFailedResv.put(customerId, itemsFailedResv_);
    }

    @JsonIgnore
    public boolean isTaskComplete(int customerId) {
        if (remainSubTaskCnt.get(customerId) == 0) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public void removeTask(int customerId) {
        remainSubTaskCnt.remove(customerId);
//        attemptResTaskList.remove(customerId);
        itemsSuccessResv.remove(customerId);
        itemsFailedResv.remove(customerId);
    }
}

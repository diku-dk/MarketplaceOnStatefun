package dk.ku.dms.marketplace.Types.State;

import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.HashMap;

@Setter
@Getter
public class ShipmentProxyState {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ShipmentProxyState> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "ShipmentProxyState"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, ShipmentProxyState.class));

    @JsonProperty("TaskList")
    public HashMap<Integer, Integer> TaskList = new HashMap<>();

    @JsonIgnore
    public void addTask(int tid, int numTask) {
        TaskList.put(tid, numTask);
    }

    @JsonIgnore
    public void subTaskDone(int tid) {
        TaskList.put(tid, TaskList.get(tid) - 1);
    }

    @JsonIgnore
    public boolean isTaskDone(int tid) {
        return TaskList.get(tid) == 0;
    }

    @JsonIgnore
    public void removeTask(int tid) {
        TaskList.remove(tid);
    }

}

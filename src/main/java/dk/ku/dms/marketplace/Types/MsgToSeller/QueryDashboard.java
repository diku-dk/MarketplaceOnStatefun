package dk.ku.dms.marketplace.Types.MsgToSeller;

import dk.ku.dms.marketplace.constants.Constants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@Setter
@Getter
public class QueryDashboard {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<QueryDashboard> TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(Constants.TYPES_NAMESPACE, "QueryDashboard"),
                    mapper::writeValueAsBytes,
                    bytes -> mapper.readValue(bytes, QueryDashboard.class));

    @JsonProperty("tid")
    private int tid;

    @JsonCreator
    public QueryDashboard(@JsonProperty("instanceId") int tid) {
        this.tid = tid;
    }
}

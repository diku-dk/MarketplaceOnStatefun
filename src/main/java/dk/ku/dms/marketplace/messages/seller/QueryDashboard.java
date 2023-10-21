package dk.ku.dms.marketplace.messages.seller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class QueryDashboard {

    @JsonProperty("tid")
    private final int tid;

    @JsonCreator
    public QueryDashboard(@JsonProperty("tid") int tid) {
        this.tid = tid;
    }
}

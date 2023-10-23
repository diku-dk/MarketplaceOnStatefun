package dk.ku.dms.marketplace.messages.seller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class QueryDashboard {

    @JsonProperty("tid")
    private final String tid;

    @JsonCreator
    public QueryDashboard(@JsonProperty("tid") String tid) {
        this.tid = tid;
    }

    public String getTid() {
        return tid;
    }

}

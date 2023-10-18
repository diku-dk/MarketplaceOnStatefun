package dk.ku.dms.marketplace.entities;

import dk.ku.dms.marketplace.utils.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionMark {

    @JsonProperty("tid")
    private String tid;

    @JsonProperty("type")
    private Enums.TransactionType type;

    @JsonProperty("actorId")
    private int actorId;

    @JsonProperty("status")
    private Enums.MarkStatus status;

    @JsonProperty("source")
    private String source;

    @JsonCreator
    public TransactionMark(
                           @JsonProperty("tid") String tid,
                           @JsonProperty("type") Enums.TransactionType type,
                           @JsonProperty("actorId") int actorId,
                           @JsonProperty("status") Enums.MarkStatus status,
                           @JsonProperty("source") String source
    ) {

        this.tid = tid;
        this.type = type;
        this.actorId = actorId;
        this.status = status;
        this.source = source;
    }

    @Override
    public String toString() {
        return "TransactionMark{" +
                "tid=" + tid +
                ", type=" + type +
                ", actorId=" + actorId +
                ", status=" + status +
                ", source='" + source + '\'' +
                '}';
    }
}

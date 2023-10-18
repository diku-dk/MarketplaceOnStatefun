package dk.ku.dms.marketplace.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ku.dms.marketplace.utils.Enums;

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

    public String getTid() {
        return tid;
    }

    public Enums.TransactionType getType() {
        return type;
    }

    public int getActorId() {
        return actorId;
    }

    public Enums.MarkStatus getStatus() {
        return status;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "{" +
                " \"tid\" : \"" + tid + "\"" +
                ", \"type\" : \"" + type.toString() + "\"" +
                ", \"actorId\" : " + actorId +
                ", \"status\" : \"" + status.toString() + "\"" +
                ", \"source\" : \"" + source + "\"" +
                '}';
    }
}

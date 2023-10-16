package dk.ku.dms.marketplace.common.Entity;

import dk.ku.dms.marketplace.constants.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionMark {
    @JsonProperty("taskId")
    private long taskId;
    @JsonProperty("tid")
    private int tid;
    @JsonProperty("receiver") // tell stream which worker to send to
    private String receiver;
    @JsonProperty("status") // 修改
    private Enums.MarkStatus status;
    @JsonProperty("source") // 新加的
    private String source;

    @JsonCreator
    public TransactionMark(@JsonProperty("taskId") long taskId,
                           @JsonProperty("tid") int tid,
                           @JsonProperty("receiver") String receiver,
                           @JsonProperty("status") Enums.MarkStatus status,
                           @JsonProperty("source") String source
    ) {
        this.taskId = taskId;
        this.tid = tid;
        this.receiver = receiver;
        this.status = status;
        this.source = source;
    }

    @Override
    public String toString() {
        return "TransactionMark{" +
                "taskId=" + taskId +
                ", tid=" + tid +
                ", receiver='" + receiver + '\'' +
                ", status=" + status +
                ", source='" + source + '\'' +
                '}';
    }
}

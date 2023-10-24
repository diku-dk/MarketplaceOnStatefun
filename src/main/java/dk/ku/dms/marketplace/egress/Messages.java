package dk.ku.dms.marketplace.egress;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static dk.ku.dms.marketplace.utils.Constants.messageMapper;

public final class Messages {

    public static final Type<EgressRecord> EGRESS_RECORD_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf("io.statefun.playground", "EgressRecord"),
                    messageMapper::writeValueAsBytes,
                    bytes -> messageMapper.readValue(bytes, EgressRecord.class));

    public static final class EgressRecord {
        @JsonProperty("topic")
        private String topic;

        @JsonProperty("payload")
        private String payload;

        public EgressRecord() {
            this(null, null);
        }

        public EgressRecord(String topic, String payload) {
            this.topic = topic;
            this.payload = payload;
        }

        public String getTopic() {
            return topic;
        }

        public String getPayload() {
            return payload;
        }
    }

}

package dk.ku.dms.marketplace.types.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TmpUserLogin {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String username;

    public TmpUserLogin() {
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}

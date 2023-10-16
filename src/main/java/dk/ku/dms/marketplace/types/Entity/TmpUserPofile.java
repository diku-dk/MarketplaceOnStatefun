package dk.ku.dms.marketplace.types.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TmpUserPofile {
    @JsonProperty("login_cnt")
    private Integer loginCnt;

    @JsonProperty("user_name")
    private String username;

    public TmpUserPofile() {
    }

    public TmpUserPofile(String username, Integer loginCnt) {
        this.username = username;
        this.loginCnt = loginCnt;
    }

    public String getUsername() {
        return username;
    }

    public Integer getLoginCnt() {
        return loginCnt;
    }

    public static TmpUserPofile create(String username, Integer loginCnt) {
        return new TmpUserPofile(username, loginCnt);
    }
}

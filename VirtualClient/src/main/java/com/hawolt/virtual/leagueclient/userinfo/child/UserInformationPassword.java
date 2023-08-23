package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONObject;

/**
 * Created: 10/01/2023 22:14
 * Author: Twitter @hawolt
 **/

public class UserInformationPassword {
    private final long changeAt;
    private final boolean reset, mustReset;

    public UserInformationPassword(JSONObject o) {
        this.changeAt = o.getLong("cng_at");
        this.reset = o.getBoolean("reset");
        this.mustReset = o.getBoolean("must_reset");
    }

    public long getChangeAt() {
        return changeAt;
    }

    public boolean isReset() {
        return reset;
    }

    public boolean isMustReset() {
        return mustReset;
    }

    @Override
    public String toString() {
        return "UserInformationPassword{" +
                "changeAt=" + changeAt +
                ", reset=" + reset +
                ", mustReset=" + mustReset +
                '}';
    }
}

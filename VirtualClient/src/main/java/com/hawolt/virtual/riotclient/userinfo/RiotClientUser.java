package com.hawolt.virtual.riotclient.userinfo;

import com.hawolt.virtual.misc.DynamicObject;
import org.json.JSONObject;

/**
 * Created: 13/01/2023 14:20
 * Author: Twitter @hawolt
 **/

public class RiotClientUser extends DynamicObject {

    public RiotClientUser(JSONObject object) {
        super(object);
    }

    public String getPUUID() {
        return getByKeyNonNullOrThrow("sub", () -> new RuntimeException("Invalid RiotClientUser state"));
    }

    public String getDataRegion() {
        JSONObject data = getByKeyNonNullOrThrow("dat", () -> new RuntimeException("Invalid RiotClientUser state"));
        return data.has("r") && !data.isNull("r") ? data.getString("r") : null;
    }

    public long getDataUserId() {
        JSONObject data = getByKeyNonNullOrThrow("dat", () -> new RuntimeException("Invalid RiotClientUser state"));
        return data.has("r") && !data.isNull("r") ? data.getLong("u") : 0L;
    }

    public boolean isLeagueAccountAssociated() {
        return getDataRegion() != null && getDataUserId() != 0L;
    }
}

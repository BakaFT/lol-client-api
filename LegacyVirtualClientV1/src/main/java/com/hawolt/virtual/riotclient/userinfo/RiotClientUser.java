package com.hawolt.virtual.riotclient.userinfo;

import org.json.JSONObject;

import java.util.Base64;

/**
 * Created: 13/01/2023 14:20
 * Author: Twitter @hawolt
 **/

public class RiotClientUser {
    private final String sub;
    private String dataRegion;
    private long dataUserId;

    public RiotClientUser(String jwt) {
        JSONObject object = new JSONObject(new String(Base64.getDecoder().decode(jwt.split("\\.")[1])));
        this.sub = object.getString("sub");
        JSONObject data = object.getJSONObject("dat");
        if (data.has("u")) this.dataUserId = data.getLong("u");
        if (data.has("r")) this.dataRegion = data.getString("r");
    }

    public String getSub() {
        return sub;
    }

    public String getDataRegion() {
        return dataRegion;
    }

    public long getDataUserId() {
        return dataUserId;
    }

    public boolean isLeagueAccountAssociated() {
        return dataUserId != 0L && dataRegion != null;
    }

    @Override
    public String toString() {
        return "RiotClientUser{" +
                "sub='" + sub + '\'' +
                ", dataRegion='" + dataRegion + '\'' +
                ", dataUserId=" + dataUserId +
                '}';
    }
}

package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 10/01/2023 22:22
 * Author: Twitter @hawolt
 **/

public class UserInformationBanRestrictionGameData {
    private final List<String> additionalGameIds = new ArrayList<>();
    private final String productName;
    private Object gameLocation, triggerGameId;

    public UserInformationBanRestrictionGameData(JSONObject o) {
        this.productName = o.getString("productName");
        if (o.has("triggerGameId")) this.triggerGameId = o.getString("triggerGameId");
        if (o.has("gameLocation")) this.gameLocation = o.getString("gameLocation");
        if (!o.has("additionalGameIds")) return;
        JSONArray array = o.getJSONArray("additionalGameIds");
        for (int i = 0; i < array.length(); i++) {
            additionalGameIds.add(array.getString(i));
        }
    }

    public Object getGameLocation() {
        return gameLocation;
    }

    public List<String> getAdditionalGameIds() {
        return additionalGameIds;
    }

    public String getProductName() {
        return productName;
    }

    public Object getTriggerGameId() {
        return triggerGameId;
    }

    @Override
    public String toString() {
        return "UserInformationBanRestrictionGameData{" +
                "additionalGameIds=" + additionalGameIds +
                ", productName='" + productName + '\'' +
                ", gameLocation='" + gameLocation + '\'' +
                ", triggerGameId='" + triggerGameId + '\'' +
                '}';
    }
}

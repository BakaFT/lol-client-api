package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONObject;

/**
 * Created: 10/01/2023 22:22
 * Author: Twitter @hawolt
 **/

public class UserInformationBanRestrictionData {
    private UserInformationBanRestrictionGameData userInformationBanRestrictionGameData;

    public UserInformationBanRestrictionData(JSONObject o) {
        if (!o.has("gameData")) return;
        this.userInformationBanRestrictionGameData = new UserInformationBanRestrictionGameData(o.getJSONObject("gameData"));
    }

    public UserInformationBanRestrictionGameData getUserInformationBanRestrictionGameData() {
        return userInformationBanRestrictionGameData;
    }

    @Override
    public String toString() {
        return "UserInformationBanRestrictionData{" +
                "userInformationBanRestrictionGameData=" + userInformationBanRestrictionGameData +
                '}';
    }
}

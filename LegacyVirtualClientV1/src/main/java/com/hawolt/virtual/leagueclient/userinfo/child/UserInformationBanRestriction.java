package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONObject;

/**
 * Created: 10/01/2023 22:22
 * Author: Twitter @hawolt
 **/

public class UserInformationBanRestriction {
    private final UserInformationBanRestrictionData userInformationBanRestrictionData;
    private final String type, reason, scope;

    public UserInformationBanRestriction(JSONObject o) {
        this.type = o.getString("type");
        this.reason = o.getString("reason");
        this.scope = o.getString("scope");
        this.userInformationBanRestrictionData = new UserInformationBanRestrictionData(o.getJSONObject("dat"));
    }

    public UserInformationBanRestrictionData getUserInformationBanRestrictionData() {
        return userInformationBanRestrictionData;
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "UserInformationBanRestriction{" +
                "userInformationBanRestrictionData=" + userInformationBanRestrictionData +
                ", type='" + type + '\'' +
                ", reason='" + reason + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}

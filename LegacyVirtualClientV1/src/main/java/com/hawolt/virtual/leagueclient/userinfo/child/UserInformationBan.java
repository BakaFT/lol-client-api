package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 10/01/2023 22:22
 * Author: Twitter @hawolt
 **/

public class UserInformationBan {
    private final List<UserInformationBanRestriction> list = new ArrayList<>();
    private Object code, expires;
    private String description;

    public UserInformationBan(JSONObject o) {
        if (o.has("code")) this.code = o.get("code");
        if (o.has("desc")) this.description = o.getString("desc");
        if (o.has("exp")) this.expires = o.get("exp");
        JSONArray array = o.getJSONArray("restrictions");
        for (int i = 0; i < array.length(); i++) {
            list.add(new UserInformationBanRestriction(array.getJSONObject(i)));
        }
    }

    public List<UserInformationBanRestriction> getList() {
        return list;
    }

    public Object getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Object getExpires() {
        return expires;
    }

    @Override
    public String toString() {
        return "UserInformationBan{" +
                "list=" + list +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", expires=" + expires +
                '}';
    }
}

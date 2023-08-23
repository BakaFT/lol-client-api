package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 10/01/2023 22:07
 * Author: Twitter @hawolt
 **/

public class UserInformationRegion {
    private final List<String> locales = new ArrayList<>();
    private final String id, tag;

    public UserInformationRegion(JSONObject o) {
        this.id = o.getString("id");
        this.tag = o.getString("tag");
        if (o.isNull("locales")) return;
        JSONArray array = o.getJSONArray("locales");
        for (int i = 0; i < array.length(); i++) {
            locales.add(array.getString(i));
        }
    }

    public List<String> getLocales() {
        return locales;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "UserInformationRegion{" +
                "locales=" + locales +
                ", id='" + id + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}

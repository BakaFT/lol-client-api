package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 10/01/2023 22:08
 * Author: Twitter @hawolt
 **/

public class UserInformationLeagueRegion {
    private final List<UserInformationLeagueRegionAccount> list = new ArrayList<>();

    public UserInformationLeagueRegion(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            list.add(new UserInformationLeagueRegionAccount(array.getJSONObject(i)));
        }
    }

    public List<UserInformationLeagueRegionAccount> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "UserInformationLeagueRegion{" +
                "list=" + list +
                '}';
    }
}

package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONObject;

/**
 * Created: 10/01/2023 22:13
 * Author: Twitter @hawolt
 **/

public class UserInformationLeagueAccount {
    private final long summonerId, profileIcon, summonerLevel;
    private final String summonerName;

    public UserInformationLeagueAccount(JSONObject o) {
        this.summonerId = o.getLong("summoner_id");
        this.profileIcon = o.getLong("profile_icon");
        this.summonerLevel = o.getLong("summoner_level");
        this.summonerName = o.getString("summoner_name");
    }

    public long getSummonerId() {
        return summonerId;
    }

    public long getProfileIcon() {
        return profileIcon;
    }

    public long getSummonerLevel() {
        return summonerLevel;
    }

    public String getSummonerName() {
        return summonerName;
    }

    @Override
    public String toString() {
        return "UserInformationLeagueAccount{" +
                "summonerId=" + summonerId +
                ", profileIcon=" + profileIcon +
                ", summonerLevel=" + summonerLevel +
                ", summonerName='" + summonerName + '\'' +
                '}';
    }
}

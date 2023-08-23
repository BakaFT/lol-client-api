package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONObject;

/**
 * Created: 10/01/2023 22:05
 * Author: Twitter @hawolt
 **/

public class UserInformationAccount {
    private final Object gameName, tagLine;
    private final String state;
    private final long createdAt;
    private final boolean adm;
    private final int type;

    public UserInformationAccount(JSONObject o) {
        this.type = o.getInt("type");
        this.state = o.getString("state");
        this.adm = o.getBoolean("adm");
        this.gameName = o.get("game_name");
        this.tagLine = o.get("tag_line");
        this.createdAt = o.getLong("created_at");
    }

    public String getState() {
        return state;
    }

    public Object getGameName() {
        return gameName;
    }

    public Object getTagLine() {
        return tagLine;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isAdm() {
        return adm;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "UserInformationAccount{" +
                "state='" + state + '\'' +
                ", gameName='" + gameName + '\'' +
                ", tagLine='" + tagLine + '\'' +
                ", createdAt=" + createdAt +
                ", adm=" + adm +
                ", type=" + type +
                '}';
    }
}

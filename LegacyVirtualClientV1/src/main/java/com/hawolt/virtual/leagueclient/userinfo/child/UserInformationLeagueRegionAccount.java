package com.hawolt.virtual.leagueclient.userinfo.child;

import org.json.JSONObject;

/**
 * Created: 10/01/2023 22:08
 * Author: Twitter @hawolt
 **/

public class UserInformationLeagueRegionAccount {
    private final long cuid, uid;
    private final String cpid, pid;
    private final boolean lp, active;

    public UserInformationLeagueRegionAccount(JSONObject object) {
        this.cuid = object.getLong("cuid");
        this.cpid = object.getString("cpid");
        this.uid = object.getLong("uid");
        this.pid = object.getString("pid");
        this.lp = object.getBoolean("lp");
        this.active = object.getBoolean("active");
    }

    public long getCUID() {
        return cuid;
    }

    public long getUID() {
        return uid;
    }

    public String getCPID() {
        return cpid;
    }

    public String getPID() {
        return pid;
    }

    public boolean isLP() {
        return lp;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "UserInformationLeagueRegionAccount{" +
                "cuid=" + cuid +
                ", uid=" + uid +
                ", cpid='" + cpid + '\'' +
                ", pid='" + pid + '\'' +
                ", lp=" + lp +
                ", active=" + active +
                '}';
    }
}

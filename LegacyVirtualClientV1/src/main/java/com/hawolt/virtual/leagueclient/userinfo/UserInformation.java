package com.hawolt.virtual.leagueclient.userinfo;

import com.hawolt.virtual.leagueclient.userinfo.child.*;
import org.json.JSONObject;

/**
 * Created: 10/01/2023 22:02
 * Author: Twitter @hawolt
 **/

public class UserInformation {
    private final String country, sub, originalPlatformId, preferredUsername, jti, username;
    private final Object playerPLocale, countryAt, pPID, playerLocale;
    private final long originalAccountId, pvpnetAccountId;
    private final boolean emailVerified, phoneVerified;
    private final UserInformationLeagueRegion userInformationLeagueRegion;
    private final UserInformationPassword userInformationPassword;
    private final UserInformationAccount userInformationAccount;
    private final UserInformationLeague userInformationLeague;
    private final UserInformationBan userInformationBan;
    private UserInformationLeagueAccount userInformationLeagueAccount;
    private UserInformationRegion userInformationRegion;
    private Object photo;

    public UserInformation(JSONObject o) {
        this.country = o.getString("country");
        this.sub = o.getString("sub");
        if (!o.isNull("lol_account")) {
            this.userInformationLeagueAccount = new UserInformationLeagueAccount(o.getJSONObject("lol_account"));
        }
        this.emailVerified = o.getBoolean("email_verified");
        this.playerPLocale = o.get("player_plocale");
        this.countryAt = o.get("country_at");
        this.userInformationPassword = new UserInformationPassword(o.getJSONObject("pw"));
        this.userInformationLeague = new UserInformationLeague(o.getJSONObject("lol"));
        this.originalPlatformId = o.getString("original_platform_id");
        this.originalAccountId = o.getLong("original_account_id");
        this.phoneVerified = o.getBoolean("phone_number_verified");
        if (o.has("photo")) this.photo = o.get("photo");
        this.preferredUsername = o.getString("preferred_username");
        this.userInformationBan = new UserInformationBan(o.getJSONObject("ban"));
        this.pPID = o.get("ppid");
        this.userInformationLeagueRegion = new UserInformationLeagueRegion(o.getJSONArray("lol_region"));
        this.playerLocale = o.get("player_locale");
        this.pvpnetAccountId = o.getLong("pvpnet_account_id");
        if (!o.isNull("region")) {
            this.userInformationRegion = new UserInformationRegion(o.getJSONObject("region"));
        }
        this.userInformationAccount = new UserInformationAccount(o.getJSONObject("acct"));
        this.jti = o.getString("jti");
        this.username = o.getString("username");
    }

    public boolean isLeagueAccountAssociated() {
        return userInformationLeagueAccount != null;
    }

    public String getCountry() {
        return country;
    }

    public String getSub() {
        return sub;
    }

    public String getOriginalPlatformId() {
        return originalPlatformId;
    }

    public Object getPhoto() {
        return photo;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public String getJti() {
        return jti;
    }

    public String getUsername() {
        return username;
    }

    public Object getPlayerPLocale() {
        return playerPLocale;
    }

    public Object getCountryAt() {
        return countryAt;
    }

    public Object getpPID() {
        return pPID;
    }

    public Object getPlayerLocale() {
        return playerLocale;
    }

    public long getOriginalAccountId() {
        return originalAccountId;
    }

    public long getPvpnetAccountId() {
        return pvpnetAccountId;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public UserInformationLeagueAccount getUserInformationLeagueAccount() {
        return userInformationLeagueAccount;
    }

    public UserInformationLeagueRegion getUserInformationLeagueRegion() {
        return userInformationLeagueRegion;
    }

    public UserInformationPassword getUserInformationPassword() {
        return userInformationPassword;
    }

    public UserInformationAccount getUserInformationAccount() {
        return userInformationAccount;
    }

    public UserInformationLeague getUserInformationLeague() {
        return userInformationLeague;
    }

    public UserInformationRegion getUserInformationRegion() {
        return userInformationRegion;
    }

    public UserInformationBan getUserInformationBan() {
        return userInformationBan;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "country='" + country + '\'' +
                ", sub='" + sub + '\'' +
                ", originalPlatformId='" + originalPlatformId + '\'' +
                ", photo='" + photo + '\'' +
                ", preferredUsername='" + preferredUsername + '\'' +
                ", jti='" + jti + '\'' +
                ", username='" + username + '\'' +
                ", playerPLocale=" + playerPLocale +
                ", countryAt=" + countryAt +
                ", pPID=" + pPID +
                ", playerLocale=" + playerLocale +
                ", originalAccountId=" + originalAccountId +
                ", pvpnetAccountId=" + pvpnetAccountId +
                ", emailVerified=" + emailVerified +
                ", phoneVerified=" + phoneVerified +
                ", userInformationLeagueAccount=" + userInformationLeagueAccount +
                ", userInformationLeagueRegion=" + userInformationLeagueRegion +
                ", userInformationPassword=" + userInformationPassword +
                ", userInformationAccount=" + userInformationAccount +
                ", userInformationLeague=" + userInformationLeague +
                ", userInformationRegion=" + userInformationRegion +
                ", userInformationBan=" + userInformationBan +
                '}';
    }
}

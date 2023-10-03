package com.hawolt.virtual.leagueclient.userinfo;

import com.hawolt.virtual.leagueclient.userinfo.child.*;
import com.hawolt.virtual.misc.DynamicObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Created: 10/01/2023 22:02
 * Author: Twitter @hawolt
 **/

public class UserInformation extends DynamicObject {
    private UserInformationLeagueRegion userInformationLeagueRegion;
    private UserInformationPassword userInformationPassword;
    private UserInformationAccount userInformationAccount;
    private UserInformationLeague userInformationLeague;
    private UserInformationBan userInformationBan;
    private UserInformationLeagueAccount userInformationLeagueAccount;
    private UserInformationRegion userInformationRegion;
    private Object photo, countryAt, pPID, playerPLocale;

    public UserInformation(JSONObject o) {
        super(o);

        /*
        this.country = o.getString("country");
        this.sub = o.getString("sub");
        if (!o.isNull("lol_account")) {
            this.userInformationLeagueAccount = new UserInformationLeagueAccount(o.getJSONObject("lol_account"));
        }
        this.emailVerified = o.getBoolean("email_verified");
        if (o.has("player_plocale")) this.playerPLocale = o.get("player_plocale");
        if (o.has("country_at")) this.countryAt = o.get("country_at");
        this.userInformationPassword = new UserInformationPassword(o.getJSONObject("pw"));
        if (!o.isNull("lol")) {
            this.userInformationLeague = new UserInformationLeague(o.getJSONObject("lol"));
        }
        this.originalPlatformId = o.getString("original_platform_id");
        this.originalAccountId = o.getLong("original_account_id");
        this.phoneVerified = o.getBoolean("phone_number_verified");
        if (o.has("photo")) this.photo = o.get("photo");
        this.preferredUsername = o.getString("preferred_username");
        this.userInformationBan = new UserInformationBan(o.getJSONObject("ban"));
        if (o.has("pPID")) this.pPID = o.get("pPID");
        this.userInformationLeagueRegion = new UserInformationLeagueRegion(o.getJSONArray("lol_region"));
        this.playerLocale = o.get("player_locale");
        this.pvpnetAccountId = o.getLong("pvpnet_account_id");
        if (!o.isNull("region")) {
            this.userInformationRegion = new UserInformationRegion(o.getJSONObject("region"));
        }
        this.userInformationAccount = new UserInformationAccount(o.getJSONObject("acct"));
        this.jti = o.getString("jti");
        this.username = o.getString("username");*/
    }

    public String getCountry() {
        return getByKey("country");
    }

    public String getPUUID() {
        return getByKey("sub");
    }

    public boolean isEmailVerified() {
        return getByKey("email_verified");
    }

    public Optional<Object> getPlayerPLocale() {
        return Optional.ofNullable(getByKey("player_plocale"));
    }

    public long getCountryAt() {
        return getByKey("country_at");
    }


    public Optional<UserInformationPassword> getUserInformationPassword() {
        if (userInformationAccount != null) return Optional.ofNullable(userInformationPassword);
        this.userInformationPassword = new UserInformationPassword(getByKey("pw"));
        return Optional.of(userInformationPassword);
    }


    public Optional<UserInformationLeague> getUserInformationLeague() {
        if (userInformationLeague != null) return Optional.of(userInformationLeague);
        JSONObject lol = getByKey("lol");
        if (lol == null) return Optional.empty();
        this.userInformationLeague = new UserInformationLeague(lol);
        return Optional.of(userInformationLeague);
    }


    public Optional<String> getOriginalPlatformId() {
        return Optional.ofNullable(getByKey("original_platform_id"));
    }

    public Optional<Long> getOriginalAccountId() {
        return Optional.ofNullable(getByKey("original_account_id"));
    }

    public Optional<Boolean> isPhoneVerified() {
        return getByKey("phone_number_verified");
    }

    public Optional<String> getPreferredUsername() {
        return Optional.ofNullable(getByKey("preferred_username"));
    }

    public Optional<UserInformationBan> getUserInformationBan() {
        if (userInformationBan != null) return Optional.of(userInformationBan);
        this.userInformationBan = new UserInformationBan(getByKey("ban"));
        return Optional.of(userInformationBan);
    }

    public Optional<String> getpPID() {
        return Optional.ofNullable(getByKey("ppid"));
    }

    public Optional<String> getPlayerLocale() {
        return Optional.ofNullable(getByKey("player_locale"));
    }

    public Optional<UserInformationLeagueRegion> getUserInformationLeagueRegion() {
        if (userInformationLeagueRegion != null) return Optional.of(userInformationLeagueRegion);
        this.userInformationLeagueRegion = new UserInformationLeagueRegion(getByKey("lol_region"));
        return Optional.of(userInformationLeagueRegion);
    }

    public void setUserInformationLeagueRegion(JSONObject o) {
        JSONObject reference = o.getJSONObject("lol_region");
        JSONObject object = new JSONObject();
        object.put("cuid", reference.getLong("account_id"));
        object.put("cpid", reference.getString("region"));
        object.put("uid", reference.getLong("original_account_id"));
        object.put("pid", reference.getString("original_platform_id"));
        object.put("lp", false);
        object.put("active", true);
        JSONArray custom = new JSONArray().put(object);
        put("lol_region", custom);
        userInformationLeagueRegion = new UserInformationLeagueRegion(custom);
    }

    public Optional<Long> getPVPNetAccountId() {
        return Optional.ofNullable(getByKey("pvpnet_account_id"));
    }

    public Optional<UserInformationAccount> getUserInformationAccount() {
        if (userInformationAccount != null) return Optional.of(userInformationAccount);
        this.userInformationAccount = new UserInformationAccount(getByKey("acct"));
        return Optional.of(userInformationAccount);
    }

    public String getJTI() {
        return getByKey("jti");
    }

    public String getUsername() {
        return getByKey("username");
    }


    public Optional<UserInformationLeagueAccount> getUserInformationLeagueAccount() {
        if (userInformationLeagueAccount != null) return Optional.of(userInformationLeagueAccount);
        JSONObject account = getByKey("lol_account");
        if (account == null) return Optional.empty();
        this.userInformationLeagueAccount = new UserInformationLeagueAccount(account);
        return Optional.of(userInformationLeagueAccount);
    }

    public Optional<UserInformationRegion> getUserInformationRegion() {
        if (userInformationRegion != null) return Optional.of(userInformationRegion);
        JSONObject region = getByKey("region");
        if (region == null) return Optional.empty();
        this.userInformationRegion = new UserInformationRegion(region);
        return Optional.of(userInformationRegion);
    }

    public Optional<String> getPhoto() {
        return Optional.ofNullable(getByKey("photo"));
    }


    public boolean isLeagueRegionAssociated() {
        return getUserInformationLeague().isPresent();
    }

    public boolean isLeagueAccountAssociated() {
        return getUserInformationLeague().isPresent();
    }


    public void setUserInformationLeagueAccount(String name) {
        JSONObject account = new JSONObject();
        account.put("summoner_name", name);
        account.put("summoner_level", 1);
        account.put("profile_icon", 29);
        account.put("summoner_id", userInformationLeague.getCUID());
        put("lol_account", account);
    }
}

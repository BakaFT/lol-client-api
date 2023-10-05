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
    private UserInformationLeagueAccount userInformationLeagueAccount;
    private UserInformationLeagueRegion userInformationLeagueRegion;
    private UserInformationPassword userInformationPassword;
    private UserInformationAccount userInformationAccount;
    private UserInformationRegion userInformationRegion;
    private UserInformationLeague userInformationLeague;
    private UserInformationBan userInformationBan;

    public UserInformation(JSONObject o) {
        super(o);
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
        return getUserInformationLeagueRegion().isPresent();
    }

    public boolean isLeagueAccountAssociated() {
        return getUserInformationLeagueAccount().isPresent();
    }


    public void setUserInformationLeagueAccount(String name) {
        JSONObject account = new JSONObject();
        account.put("summoner_name", name);
        account.put("summoner_level", 1);
        account.put("profile_icon", 29);
        Optional<UserInformationLeagueRegionAccount> optional = userInformationLeagueRegion.getActiveAccount();
        optional.ifPresent(userInformationLeagueRegionAccount ->
                account.put("summoner_id", userInformationLeagueRegionAccount.getCUID())
        );
        put("lol_account", account);
    }
}

package com.hawolt.authentication;

/**
 * Created: 25/08/2023 11:42
 * Author: Twitter @hawolt
 **/

public enum ClientScope {
    OPENID, OFFLINE_ACCESS, LOL, BAN, PROFILE, EMAIL, PHONE, BIRTHDATE, ACCOUNT, LINK, LOL_REGION;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

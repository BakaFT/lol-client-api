package com.hawolt.authentication;

/**
 * Created: 10/01/2023 20:44
 * Author: Twitter @hawolt
 **/

public enum ClientID {
    LOL, RIOT_CLIENT;

    @Override
    public String toString() {
        return name().toLowerCase().replaceAll("_", "-");
    }
}

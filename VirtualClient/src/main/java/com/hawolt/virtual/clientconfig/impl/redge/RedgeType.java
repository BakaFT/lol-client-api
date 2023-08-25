package com.hawolt.virtual.clientconfig.impl.redge;

/**
 * Created: 25/08/2023 20:03
 * Author: Twitter @hawolt
 **/

public enum RedgeType {
    LOADOUTS, LOGIN_QUEUE, MATCH_HISTORY_QUERY, SERVICES, SESSION_EXTERNAL, V1;

    @Override
    public String toString() {
        return name().toLowerCase().replaceAll("_", "-");
    }
}

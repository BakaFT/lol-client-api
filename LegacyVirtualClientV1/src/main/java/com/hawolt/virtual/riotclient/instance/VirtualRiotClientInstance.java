package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.http.auth.Gateway;

/**
 * Created: 26/11/2022 13:39
 * Author: Twitter @hawolt
 **/

public class VirtualRiotClientInstance extends AbstractVirtualRiotClientInstance {

    public static VirtualRiotClientInstance create(ICookieSupplier cookieSupplier) {
        return new VirtualRiotClientInstance(null, cookieSupplier, false);
    }

    public static VirtualRiotClientInstance create(ICookieSupplier cookieSupplier, boolean selfUpdate) {
        return new VirtualRiotClientInstance(null, cookieSupplier, selfUpdate);
    }

    public static VirtualRiotClientInstance create(Gateway gateway, ICookieSupplier cookieSupplier, boolean selfUpdate) {
        return new VirtualRiotClientInstance(gateway, cookieSupplier, selfUpdate);
    }

    private VirtualRiotClientInstance(Gateway gateway, ICookieSupplier cookieSupplier, boolean selfUpdate) {
        super(gateway, cookieSupplier, selfUpdate);
    }
}

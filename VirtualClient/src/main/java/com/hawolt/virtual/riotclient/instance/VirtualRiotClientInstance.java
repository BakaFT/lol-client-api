package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.authentication.LocalCookieSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.virtual.client.ILoginStateConsumer;
import com.hawolt.virtual.client.LoginStateConsumer;

/**
 * Created: 26/11/2022 13:39
 * Author: Twitter @hawolt
 **/

public class VirtualRiotClientInstance extends AbstractVirtualRiotClientInstance {

    private VirtualRiotClientInstance(Gateway gateway, ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer) {
        super(gateway, cookieSupplier, stateConsumer);
    }

    public static VirtualRiotClientInstance create(Gateway gateway, ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer) {
        return new VirtualRiotClientInstance(gateway, cookieSupplier, stateConsumer);
    }

    public static VirtualRiotClientInstance create(ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer) {
        return new VirtualRiotClientInstance(null, cookieSupplier, stateConsumer);
    }

    public static VirtualRiotClientInstance create(ICookieSupplier cookieSupplier) {
        return new VirtualRiotClientInstance(null, cookieSupplier, new LoginStateConsumer());
    }

    public static VirtualRiotClientInstance create() {
        return new VirtualRiotClientInstance(null, new LocalCookieSupplier(), new LoginStateConsumer());
    }
}

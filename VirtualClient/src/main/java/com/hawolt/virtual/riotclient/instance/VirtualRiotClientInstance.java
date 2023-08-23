package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.virtual.client.ILoginStateConsumer;
import com.hawolt.virtual.client.LoginStateConsumer;

/**
 * Created: 26/11/2022 13:39
 * Author: Twitter @hawolt
 **/

public class VirtualRiotClientInstance extends AbstractVirtualRiotClientInstance {
    private static final LoginStateConsumer BLANK_CONSUMER = new LoginStateConsumer();

    public static VirtualRiotClientInstance create(ICookieSupplier cookieSupplier) {
        return new VirtualRiotClientInstance(null, cookieSupplier, BLANK_CONSUMER, false);
    }

    public static VirtualRiotClientInstance create(ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer) {
        return new VirtualRiotClientInstance(null, cookieSupplier, stateConsumer, false);
    }

    public static VirtualRiotClientInstance create(ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer, boolean selfUpdate) {
        return new VirtualRiotClientInstance(null, cookieSupplier, stateConsumer, selfUpdate);
    }

    public static VirtualRiotClientInstance create(Gateway gateway, ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer, boolean selfUpdate) {
        return new VirtualRiotClientInstance(gateway, cookieSupplier, stateConsumer, selfUpdate);
    }

    private VirtualRiotClientInstance(Gateway gateway, ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer, boolean selfUpdate) {
        super(gateway, cookieSupplier, stateConsumer, selfUpdate);
    }
}

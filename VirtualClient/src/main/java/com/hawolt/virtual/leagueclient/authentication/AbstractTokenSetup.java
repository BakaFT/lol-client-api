package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.token.impl.StringTokenSupplier;

/**
 * Created: 25/08/2023 20:21
 * Author: Twitter @hawolt
 **/

public abstract class AbstractTokenSetup extends StringTokenSupplier implements ITokenSetup {
    protected final ICookieSupplier cookieSupplier;

    public AbstractTokenSetup(ICookieSupplier cookieSupplier) {
        this.cookieSupplier = cookieSupplier;
    }

    public abstract String getToken();

    protected abstract String getURL();
}

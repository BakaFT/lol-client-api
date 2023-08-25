package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;

import java.io.IOException;

/**
 * Created: 25/08/2023 20:20
 * Author: Twitter @hawolt
 **/

public abstract class RefreshableTokenSetup extends AbstractTokenSetup {
    public RefreshableTokenSetup(ICookieSupplier cookieSupplier) {
        super(cookieSupplier);
    }

    public abstract void refresh(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException;

    protected abstract String getRefreshURL();
}

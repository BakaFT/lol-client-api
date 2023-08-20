package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.version.IVersionSupplier;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:30
 * Author: Twitter @hawolt
 **/

public interface IAuthentication {
    String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException;

    String refresh(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException;

    String getRefreshURL();

    String getURL();
}

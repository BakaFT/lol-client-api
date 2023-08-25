package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;

import java.io.IOException;

/**
 * Created: 25/08/2023 16:47
 * Author: Twitter @hawolt
 **/

public interface ITokenSetup {
    void authenticate(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException;
}

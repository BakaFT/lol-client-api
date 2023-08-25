package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.Authorization;
import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.version.local.LocalRiotFileVersion;
import com.hawolt.virtual.client.ILoginStateConsumer;
import com.hawolt.virtual.misc.Authorizable;

import java.io.IOException;

/**
 * Created: 07/08/2023 16:41
 * Author: Twitter @hawolt
 **/

public interface IVirtualRiotClientInstance {

    StringTokenSupplier getTokenSupplier(Authorization authorization) throws IOException;

    StringTokenSupplier getRiotClientTokenSupplier();

    LocalRiotFileVersion getLocalRiotFileVersion();

    ILoginStateConsumer getLoginStateConsumer();

    String getRiotClientUserAgent(String rcp);

    ICookieSupplier getCookieSupplier();

    String getRiotClientUserAgentCEF();

    Authorizable getAuthorization();

    Gateway getGateway();
}

package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.version.local.LocalRiotFileVersion;
import com.hawolt.virtual.riotclient.client.VirtualRiotClient;

import java.io.IOException;

/**
 * Created: 07/08/2023 16:41
 * Author: Twitter @hawolt
 **/

public interface IVirtualRiotClientInstance {
    StringTokenSupplier getRiotClientSupplier(Gateway gateway, String username, String password, MultiFactorSupplier multifactor) throws IOException;

    String get(String username, String password, MultiFactorSupplier multifactor, String cookie, Gateway gateway) throws IOException;

    VirtualRiotClient login(String username, String password, MultiFactorSupplier multifactor) throws IOException;

    String submit2FA(String cookie, String code) throws IOException;

    LocalRiotFileVersion getLocalRiotFileVersion();

    ICookieSupplier getCookieSupplier();

    Gateway getGateway();
}

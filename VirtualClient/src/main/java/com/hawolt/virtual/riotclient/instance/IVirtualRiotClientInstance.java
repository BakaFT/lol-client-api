package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.CookieType;
import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.exception.CaptchaException;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.version.local.LocalRiotFileVersion;
import com.hawolt.virtual.client.CookieManager;
import com.hawolt.virtual.client.ILoginStateConsumer;
import com.hawolt.virtual.client.LoginState;
import com.hawolt.virtual.riotclient.RiotClientException;
import com.hawolt.virtual.riotclient.client.VirtualRiotClient;

import java.io.IOException;

/**
 * Created: 07/08/2023 16:41
 * Author: Twitter @hawolt
 **/

public interface IVirtualRiotClientInstance {
    String get(String username, String password, CookieType type, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier, Gateway gateway, LoginState state) throws IOException;

    StringTokenSupplier getRiotClientSupplier(Gateway gateway, String username, String password, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier) throws IOException, RiotClientException, CaptchaException, InterruptedException;

    StringTokenSupplier getLoginTokenSupplier(Gateway gateway, String username, String password, MultiFactorSupplier multiFactorSupplier, String cookie, String captchaResult) throws IOException, RiotClientException;

    VirtualRiotClient login(String username, String password, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier) throws IOException, RiotClientException, CaptchaException, InterruptedException;

    VirtualRiotClient login(Platform platform, String token, Gateway gateway) throws IOException;

    String submit2FA(String cookie, String code) throws IOException;

    CaptchaInfo getCaptchaInfo() throws IOException;

    LocalRiotFileVersion getLocalRiotFileVersion();

    ILoginStateConsumer getLoginStateConsumer();

    ICookieSupplier getCookieSupplier();

    String getRiotClientUserAgentCEF();

    CookieManager getCookieManager();

    String getRiotClientUserAgent();

    Gateway getGateway();

    //   IResponse getSSID();
}

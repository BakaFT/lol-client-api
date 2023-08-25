package com.hawolt.virtual.leagueclient.authentication.impl;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.virtual.clientconfig.impl.PublicClientConfig;
import com.hawolt.virtual.clientconfig.impl.redge.RedgeConfig;
import com.hawolt.virtual.clientconfig.impl.redge.RedgeType;
import com.hawolt.virtual.leagueclient.authentication.AbstractTokenSetup;
import okhttp3.Request;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:28
 * Author: Twitter @hawolt
 **/

public class Sipt extends AbstractTokenSetup {

    private final PublicClientConfig clientConfig;

    public Sipt(ICookieSupplier cookieSupplier, PublicClientConfig clientConfig) {
        super(cookieSupplier);
        this.clientConfig = clientConfig;
    }


    @Override
    public void authenticate(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.getSimple("session_token")))
                .addHeader("User-Agent", userAgent)
                .addHeader("Accept", "application/json")
                .get()
                .build();
        IResponse response = OkHttp3Client.execute(request, gateway);
        String plain = response.asString();
        String token = plain.substring(1, plain.length() - 1);
        add("sipt_token", token);
    }

    @Override
    public String getToken() {
        return getSimple("sipt_token");
    }


    @Override
    protected String getURL() {
        RedgeConfig redgeConfig = clientConfig.getRedgeConfig();
        String value = redgeConfig.getRedgeValue(RedgeType.SERVICES);
        return String.format("%s/sipt/v1/sipt/token", value);
    }
}
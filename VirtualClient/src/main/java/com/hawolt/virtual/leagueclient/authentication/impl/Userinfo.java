package com.hawolt.virtual.leagueclient.authentication.impl;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.virtual.leagueclient.authentication.AbstractTokenSetup;
import okhttp3.Request;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:27
 * Author: Twitter @hawolt
 **/

public class Userinfo extends AbstractTokenSetup {

    public Userinfo(ICookieSupplier cookieSupplier) {
        super(cookieSupplier);
    }

    @Override
    public void authenticate(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.getSimple("access_token")))
                .addHeader("User-Agent", userAgent)
                .get()
                .build();
        IResponse response = cookieSupplier.handle(OkHttp3Client.execute(request, gateway));
        String token = response.asString();
        add("userinfo_token", token);
    }


    @Override
    public String getToken() {
        return getSimple("userinfo_token");
    }

    @Override
    protected String getURL() {
        return "https://auth.riotgames.com/userinfo";
    }
}

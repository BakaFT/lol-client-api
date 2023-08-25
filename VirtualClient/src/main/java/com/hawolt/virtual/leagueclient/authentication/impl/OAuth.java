package com.hawolt.virtual.leagueclient.authentication.impl;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.virtual.client.OAuthCode;
import com.hawolt.virtual.leagueclient.authentication.RefreshableTokenSetup;
import okhttp3.FormBody;
import okhttp3.Request;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 25/08/2023 16:47
 * Author: Twitter @hawolt
 **/

public class OAuth extends RefreshableTokenSetup {
    private final OAuthCode challenge;
    private final String code;

    public OAuth(ICookieSupplier cookieSupplier, OAuthCode challenge, String code) {
        super(cookieSupplier);
        this.challenge = challenge;
        this.code = code;
    }

    private void accept(Gateway gateway, String userAgent, FormBody body) throws IOException {
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/token")
                .addHeader("User-Agent", userAgent)
                .addHeader("Accept", "application/json")
                .post(body)
                .build();
        IResponse response = cookieSupplier.handle(OkHttp3Client.execute(request, gateway));
        JSONObject object = new JSONObject(response.asString());
        for (String key : object.keySet()) {
            add(key, String.valueOf(object.get(key)));
        }
    }


    @Override
    public void authenticate(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        FormBody.Builder builder = new FormBody.Builder()
                .add("client_id", "lol")
                .add("grant_type", "authorization_code")
                .add("redirect_uri", "http://localhost/redirect")
                .add("code", code)
                .add("code_verifier", challenge.getVerifier());
        accept(gateway, userAgent, builder.build());
    }

    @Override
    public void refresh(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        FormBody.Builder builder = new FormBody.Builder()
                .add("client_id", "lol")
                .add("grant_type", "refresh_token")
                .add("refresh_token", getSimple("refresh_token"));
        accept(gateway, userAgent, builder.build());
    }

    @Override
    public String getToken() {
        return getSimple("access_token");
    }

    @Override
    protected String getURL() {
        return "https://auth.riotgames.com/token";
    }

    @Override
    protected String getRefreshURL() {
        return getURL();
    }
}

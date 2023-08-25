package com.hawolt.virtual.leagueclient.authentication.impl;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.Constant;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.virtual.clientconfig.impl.PublicClientConfig;
import com.hawolt.virtual.clientconfig.impl.redge.RedgeConfig;
import com.hawolt.virtual.clientconfig.impl.redge.RedgeType;
import com.hawolt.virtual.leagueclient.authentication.RefreshableTokenSetup;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:28
 * Author: Twitter @hawolt
 **/

public class Session extends RefreshableTokenSetup {
    private final PublicClientConfig clientConfig;
    private final UserInformation userInformation;

    public Session(ICookieSupplier cookieSupplier, PublicClientConfig clientConfig, UserInformation userInformation) {
        super(cookieSupplier);
        this.clientConfig = clientConfig;
        this.userInformation = userInformation;
    }


    @Override
    public void authenticate(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("product", "lol");
        payload.put("puuid", userInformation.getSub());
        payload.put("region", userInformation.getUserInformationLeague().getCPID().toLowerCase());
        JSONObject claims = new JSONObject();
        claims.put("cname", "lcu");
        payload.put("claims", claims);
        RequestBody post = RequestBody.create(payload.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.getSimple("login_token")))
                .addHeader("User-Agent", userAgent)
                .addHeader("Accept", "application/json")
                .post(post)
                .build();
        execute(gateway, request);
    }

    @Override
    public void refresh(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        JSONObject payload = new JSONObject();
        String token = get("session_token");
        payload.put("lst", token);
        RequestBody post = RequestBody.create(payload.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url(getRefreshURL())
                .addHeader("Authorization", String.format("Bearer %s", token))
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", userAgent)
                .post(post)
                .build();
        execute(gateway, request);
    }

    private void execute(Gateway gateway, Request request) throws IOException {
        IResponse response = OkHttp3Client.execute(request, gateway);
        String plain = response.asString();
        String token = plain.substring(1, plain.length() - 1);
        add("session_token", token);
    }

    @Override
    public String getRefreshURL() {
        RedgeConfig redgeConfig = clientConfig.getRedgeConfig();
        String value = redgeConfig.getRedgeValue(RedgeType.SESSION_EXTERNAL);
        return String.format("%s/session-external/v1/session/refresh", value);
    }

    @Override
    public String getURL() {
        RedgeConfig redgeConfig = clientConfig.getRedgeConfig();
        String value = redgeConfig.getRedgeValue(RedgeType.SESSION_EXTERNAL);
        return String.format("%s/session-external/v1/session/create", value);
    }

    @Override
    public String getToken() {
        return getSimple("session_token");
    }
}

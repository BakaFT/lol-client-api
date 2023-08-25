package com.hawolt.virtual.leagueclient.authentication.impl;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.Constant;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.virtual.clientconfig.impl.PublicClientConfig;
import com.hawolt.virtual.clientconfig.impl.redge.RedgeConfig;
import com.hawolt.virtual.clientconfig.impl.redge.RedgeType;
import com.hawolt.virtual.leagueclient.authentication.AbstractTokenSetup;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 25/08/2023 19:44
 * Author: Twitter @hawolt
 **/

public class LoginQueue extends AbstractTokenSetup {
    private final PublicClientConfig clientConfig;
    private final Platform platform;

    public LoginQueue(ICookieSupplier cookieSupplier, PublicClientConfig clientConfig) {
        super(cookieSupplier);
        this.clientConfig = clientConfig;
        this.platform = clientConfig.getPlatform();
    }


    @Override
    public void authenticate(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("clientName", "lcu");
        payload.put("entitlements", tokenSupplier.getSimple("entitlements_token"));
        payload.put("userinfo", tokenSupplier.getSimple("userinfo_token"));
        RequestBody post = RequestBody.create(payload.toString(), Constant.APPLICATION_JSON);

        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.getSimple("access_token")))
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", userAgent)
                .post(post)
                .build();

        IResponse response = cookieSupplier.handle(OkHttp3Client.execute(request, gateway));
        JSONObject object = new JSONObject(response.asString());
        if (!object.has("token")) throw new IOException("NO_DATA_PRESENT");
        add("login_token", object.getString("token"));
    }


    @Override
    public String getToken() {
        return getSimple("login_token");
    }

    @Override
    protected String getURL() {
        RedgeConfig redgeConfig = clientConfig.getRedgeConfig();
        String value = redgeConfig.getRedgeValue(RedgeType.LOGIN_QUEUE);
        return String.format("%s/login-queue/v2/login/products/lol/regions/%s", value, platform.name().toLowerCase());
    }
}

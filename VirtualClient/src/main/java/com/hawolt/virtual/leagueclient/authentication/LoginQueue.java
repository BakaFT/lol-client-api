package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.Constant;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.version.IVersionSupplier;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 10/01/2023 19:50
 * Author: Twitter @hawolt
 **/

public class LoginQueue extends StringTokenSupplier implements IAuthentication {
    private final Platform platform;
    private final String url;

    public LoginQueue(String url, Platform platform) {
        this.platform = platform;
        //TODO why does OC1 have a custom login queue
        this.url = platform != Platform.OC1 ? url : "https://apse1-green.pp.sgp.pvp.net";
    }

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("clientName", "lcu");
        payload.put("entitlements", tokenSupplier.get("entitlement.lol.entitlements_token"));
        payload.put("userinfo", tokenSupplier.get("userinfo.lol.userinfo_token"));
        RequestBody post = RequestBody.create(payload.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.get("lol.access_token")))
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", String.format(
                        "LeagueOfLegendsClient/%s (rcp-be-lol-login)",
                        versionSupplier.getVersionValue(platform, "LeagueClientUxRender.exe"))
                )
                .post(post)
                .build();
        Call call = OkHttp3Client.perform(request, gateway);
        try (Response response = call.execute()) {
            JSONObject object = new JSONObject(response.body().string());
            if (!object.has("token")) throw new IOException("NO_DATA_PRESENT");
            String token = object.getString("token");
            add("login_token", token);
            return token;
        }
    }

    @Override
    public String refresh(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        return authenticate(gateway, versionSupplier, tokenSupplier);
    }

    @Override
    public String getRefreshURL() {
        return null;
    }

    @Override
    public String getURL() {
        return String.format("%s/login-queue/v2/login/products/lol/regions/%s", url, platform.name().toLowerCase());
    }

}

package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.version.IVersionSupplier;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 13/01/2023 17:39
 * Author: Twitter @hawolt
 **/

public class OAuthToken extends StringTokenSupplier implements IAuthentication {
    private final Platform platform;

    public OAuthToken(Platform platform) {
        this.platform = platform;
    }

    private String authenticate(Gateway gateway, RequestBody body, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        String agent = String.format(
                "LeagueOfLegendsClient/%s (rcp-be-%s)",
                versionSupplier.getVersionValue(platform, "LeagueClientUxRender.exe"),
                tokenSupplier.getSupplierName()
        );
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", "Basic bG9sOg")
                .addHeader("User-Agent", agent)
                .addHeader("Accept", "application/json")
                .post(body)
                .build();
        Call call = OkHttp3Client.perform(request, gateway);
        try (Response response = call.execute()) {
            JSONObject object = new JSONObject(response.body().string());
            String token = object.getString("access_token");
            for (String key : object.keySet()) {
                add(key, String.valueOf(object.get(key)));
            }
            return token;
        }
    }

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        FormBody.Builder builder = new FormBody.Builder()
                .add("client_id", "lol")
                .add("grant_type", "authorization_code")
                .add("redirect_uri", "http://localhost/redirect")
                .add("code", tokenSupplier.get("code"));
        return authenticate(gateway, builder.build(), versionSupplier, tokenSupplier);
    }

    @Override
    public String refresh(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        FormBody.Builder builder = new FormBody.Builder()
                .add("client_id", "lol")
                .add("grant_type", "refresh_token")
                .add("refresh_token", get("refresh_token"));
        return authenticate(gateway, builder.build(), versionSupplier, tokenSupplier);
    }

    @Override
    public String getRefreshURL() {
        return "https://auth.riotgames.com/token";
    }

    @Override
    public String getURL() {
        return "https://auth.riotgames.com/token";
    }
}

package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.version.IVersionSupplier;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:28
 * Author: Twitter @hawolt
 **/

public class Sipt extends StringTokenSupplier implements IAuthentication {

    private final Platform platform;
    private final String url;

    public Sipt(Platform platform, String url) {
        this.platform = platform;
        this.url = url;
    }

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.get("session.session_token", true)))
                .addHeader("User-Agent", String.format(
                        "LeagueOfLegendsClient/%s (rcp-be-lol-login)",
                        versionSupplier.getVersionValue(platform, "LeagueClientUxRender.exe"))
                )
                .addHeader("Accept", "application/json")
                .get()
                .build();
        Call call = OkHttp3Client.perform(request, gateway);
        try (Response response = call.execute()) {
            String plain = response.body().string();
            String token = plain.substring(1, plain.length() - 1);
            add("sipt_token", token);
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
        return String.format("%s/sipt/v1/sipt/token", url);
    }
}

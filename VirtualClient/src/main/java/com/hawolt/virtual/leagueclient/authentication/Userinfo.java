package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.version.IVersionSupplier;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:27
 * Author: Twitter @hawolt
 **/

public class Userinfo extends StringTokenSupplier implements IAuthentication {

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        String minor = versionSupplier.getVersionValue("RiotGamesApi.dll");
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.get("access_token")))
                .addHeader("User-Agent",
                        String.format(
                                "RiotClient/%s%s rso-auth (Windows;10;;Professional, x64)",
                                versionSupplier.getVersionValue("RiotClientFoundation.dll"),
                                minor.substring(minor.lastIndexOf('.'))
                        )
                )
                .get()
                .build();
        Call call = OkHttp3Client.perform(request, gateway);
        try (Response response = call.execute()) {
            String token = response.body().string();
            String key = String.join(".", tokenSupplier.getSupplierName(), "userinfo_token");
            add(key, token);
            return token;
        }
    }

    @Override
    public String refresh(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier supplier) throws IOException {
        return authenticate(gateway, versionSupplier, supplier);
    }

    @Override
    public String getRefreshURL() {
        return null;
    }

    @Override
    public String getURL() {
        return "https://auth.riotgames.com/userinfo";
    }

}

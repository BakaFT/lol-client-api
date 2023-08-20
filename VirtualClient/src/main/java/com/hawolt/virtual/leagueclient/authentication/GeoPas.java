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
 * Created: 05/02/2023 13:10
 * Author: Twitter @hawolt
 **/

public class GeoPas extends StringTokenSupplier implements IAuthentication {

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        Request request = new Request.Builder()
                .url("https://riot-geo.pas.si.riotgames.com/pas/v1/service/chat")
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.get("lol.access_token", true)))
                .get()
                .build();
        Call call = OkHttp3Client.perform(request, gateway);
        try (Response response = call.execute()) {
            String token = response.body().string();
            add("xmpp_token", token);
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
        return "https://riot-geo.pas.si.riotgames.com/pas/v1/service/chat";
    }
}

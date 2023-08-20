package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.version.IVersionSupplier;
import okhttp3.Request;

import java.io.IOException;

/**
 * Created: 05/02/2023 13:10
 * Author: Twitter @hawolt
 **/

public class RMS extends StringTokenSupplier implements IAuthentication {

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        Request request = new Request.Builder()
                .url("https://riot-geo.pas.si.riotgames.com/pas/v1/service/rms")
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.get("lol.access_token", true)))
                .get()
                .build();
        IResponse response = OkHttp3Client.execute(request, gateway);
        String token = response.asString();
        add("rms_token", token);
        return token;
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
        return "https://riot-geo.pas.si.riotgames.com/pas/v1/service/rms";
    }
}

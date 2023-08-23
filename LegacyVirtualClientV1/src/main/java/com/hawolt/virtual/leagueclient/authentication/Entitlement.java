package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.Constant;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.layer.IResponse;
import com.hawolt.version.IVersionSupplier;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:27
 * Author: Twitter @hawolt
 **/

public class Entitlement extends StringTokenSupplier implements IAuthentication {

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        RequestBody post = RequestBody.create("{ \"urn\": \"urn:entitlement:%\" }", Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.get("access_token")))
                .addHeader("User-Agent", String.format("RiotClient/%s entitlements (;;;)", versionSupplier.getVersionValue("RiotGamesApi.dll")))
                .addHeader("Accept", "application/json")
                .post(post)
                .build();
        IResponse response = OkHttp3Client.execute(request, gateway);
        JSONObject object = new JSONObject(response.asString());
        if (!object.has("entitlements_token")) throw new IOException("NO_DATA_PRESENT");
        String token = object.getString("entitlements_token");
        String key = String.join(".", tokenSupplier.getSupplierName(), "entitlements_token");
        add(key, token);
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
        return "https://entitlements.auth.riotgames.com/api/token/v1";
    }
}

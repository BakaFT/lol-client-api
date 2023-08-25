package com.hawolt.virtual.leagueclient.authentication.impl;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.generic.Constant;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.virtual.leagueclient.authentication.RefreshableTokenSetup;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:27
 * Author: Twitter @hawolt
 **/

public class Entitlement extends RefreshableTokenSetup {
    private final int type;

    public Entitlement(ICookieSupplier cookieSupplier, int type) {
        super(cookieSupplier);
        this.type = type;
    }

    private String build() throws IOException {
        //IF YOU EVER READ THIS, YES I LOVE THIS COMPANY.
        switch (type) {
            case 0 -> { //LOL
                return "{\"urn\":\"urn:entitlement:%\"}";
            }
            case 1 -> {
                return "{\n\t\"urn\": \"urn:entitlement:%\"\n}";
            }
            case 2 -> {
                return "{\n    \"urn\": \"urn:entitlement:%\"\n}";
            }
            default -> {
                throw new IOException("Unknown type for Entitlement");
            }
        }
    }

    @Override
    public void authenticate(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        RequestBody payload = RequestBody.create(build(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url(getURL())
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.getSimple("access_token")))
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", userAgent)
                .post(payload)
                .build();
        IResponse response = cookieSupplier.handle(OkHttp3Client.execute(request, gateway));
        JSONObject object = new JSONObject(response.asString());
        if (!object.has("entitlements_token")) throw new IOException("NO_DATA_PRESENT");
        add("entitlements_token", object.getString("entitlements_token"));
    }

    @Override
    public String getToken() {
        return getSimple("entitlements_token");
    }

    @Override
    protected String getURL() {
        return "https://entitlements.auth.riotgames.com/api/token/v1";
    }

    @Override
    public void refresh(Gateway gateway, String userAgent, StringTokenSupplier tokenSupplier) throws IOException {
        authenticate(gateway, userAgent, tokenSupplier);
    }

    @Override
    protected String getRefreshURL() {
        return getURL();
    }
}

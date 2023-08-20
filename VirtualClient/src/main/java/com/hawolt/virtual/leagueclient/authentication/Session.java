package com.hawolt.virtual.leagueclient.authentication;

import com.hawolt.generic.Constant;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.layer.IResponse;
import com.hawolt.version.IVersionSupplier;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 10/01/2023 17:28
 * Author: Twitter @hawolt
 **/

public class Session extends StringTokenSupplier implements IAuthentication {
    private final UserInformation userInformation;
    private final Platform platform;
    private final String url;

    public Session(UserInformation userInformation, Platform platform, String url) {
        this.userInformation = userInformation;
        this.platform = platform;
        //TODO why OCE special again?
        this.url = platform != Platform.OC1 ? url : "https://apse1-green.pp.sgp.pvp.net";
    }

    @Override
    public String authenticate(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
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
                .addHeader("Authorization", String.format("Bearer %s", tokenSupplier.get("login_token")))
                .addHeader("User-Agent", String.format(
                        "LeagueOfLegendsClient/%s (rcp-be-lol-login)",
                        versionSupplier.getVersionValue(platform, "LeagueClientUxRender.exe"))
                )
                .addHeader("Accept", "application/json")
                .post(post)
                .build();
        return execute(gateway, request);
    }

    @Override
    public String refresh(Gateway gateway, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier) throws IOException {
        JSONObject payload = new JSONObject();
        String token = get("session_token");
        payload.put("lst", token);
        RequestBody post = RequestBody.create(payload.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url(getRefreshURL())
                .addHeader("Authorization", String.format("Bearer %s", token))
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", String.format(
                        "LeagueOfLegendsClient/%s (rcp-be-lol-league-session)",
                        versionSupplier.getVersionValue(platform, "LeagueClientUxRender.exe"))
                )
                .post(post)
                .build();
        return execute(gateway, request);
    }

    private String execute(Gateway gateway, Request request) throws IOException {
        IResponse response = OkHttp3Client.execute(request, gateway);
        String plain = response.asString();
        String token = plain.substring(1, plain.length() - 1);
        add("session_token", token);
        return token;
    }

    @Override
    public String getRefreshURL() {
        return String.format("%s/session-external/v1/session/refresh", url);
    }

    @Override
    public String getURL() {
        return String.format("%s/session-external/v1/session/create", url);
    }
}

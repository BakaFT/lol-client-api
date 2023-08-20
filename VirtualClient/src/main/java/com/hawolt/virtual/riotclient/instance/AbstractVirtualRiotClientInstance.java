package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.CookieType;
import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.authentication.LocalCookieSupplier;
import com.hawolt.generic.Constant;
import com.hawolt.generic.data.QueryTokenParser;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.integrity.Diffuser;
import com.hawolt.http.layer.IResponse;
import com.hawolt.version.local.LocalRiotFileVersion;
import com.hawolt.virtual.riotclient.client.VirtualRiotClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created: 07/08/2023 16:44
 * Author: Twitter @hawolt
 **/

public abstract class AbstractVirtualRiotClientInstance implements IVirtualRiotClientInstance {
    private final LocalRiotFileVersion localRiotFileVersion;
    private final ICookieSupplier cookieSupplier;
    private final Gateway gateway;


    public AbstractVirtualRiotClientInstance(Gateway gateway, ICookieSupplier cookieSupplier, boolean selfUpdate) {
        this.localRiotFileVersion = new LocalRiotFileVersion(Arrays.asList("RiotClientFoundation.dll", "RiotGamesApi.dll"));
        if (selfUpdate) localRiotFileVersion.schedule(15, 15, TimeUnit.MINUTES);
        this.cookieSupplier = cookieSupplier;
        this.gateway = gateway;
    }

    public String payload(String username, String password) {
        JSONObject object = new JSONObject();
        object.put("type", "auth");
        object.put("username", username);
        object.put("password", password);
        object.put("remember", false);
        object.put("language", "en_GB");
        object.put("region", JSONObject.NULL);
        return object.toString();
    }

    @Override
    public String get(String username, String password, MultiFactorSupplier multifactor, String cookie, Gateway gateway) throws IOException {
        Diffuser.add(password);
        String body = payload(username, password);
        RequestBody put = RequestBody.create(body, Constant.APPLICATION_JSON);
        String minor = localRiotFileVersion.getVersionValue("RiotGamesApi.dll");
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/authorization")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent",
                        String.format(
                                "RiotClient/%s%s rso-auth (Windows;10;;Professional, x64)",
                                localRiotFileVersion.getVersionValue("RiotClientFoundation.dll"),
                                minor.substring(minor.lastIndexOf('.'))
                        )
                )
                .addHeader("Pragma", "no-cache")
                .put(put)
                .build();
        IResponse response = OkHttp3Client.execute(request, gateway);
        JSONObject tmp = new JSONObject(response.asString());
        if (tmp.has("type") && tmp.getString("type").equalsIgnoreCase("multifactor")) {
            return submit2FA(LocalCookieSupplier.build(response.headers().get("set-cookie")), multifactor.get(username, password));
        } else {
            return tmp.toString();
        }

    }

    @Override
    public String submit2FA(String cookie, String code) throws IOException {
        JSONObject object = new JSONObject();
        object.put("type", "multifactor");
        object.put("code", code);
        object.put("rememberDevice", false);
        String minor = localRiotFileVersion.getVersionValue("RiotGamesApi.dll");
        RequestBody body = RequestBody.create(object.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/authorization")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent",
                        String.format(
                                "RiotClient/%s%s rso-auth (Windows;10;;Professional, x64)",
                                localRiotFileVersion.getVersionValue("RiotClientFoundation.dll"),
                                minor.substring(minor.lastIndexOf('.'))
                        )
                )
                .addHeader("Pragma", "no-cache")
                .put(body)
                .build();
        return OkHttp3Client.execute(request, gateway).asString();
    }

    @Override
    public VirtualRiotClient login(String username, String password, MultiFactorSupplier multifactor) throws IOException {
        return new VirtualRiotClient(this, username, password, multifactor, getRiotClientSupplier(gateway, username, password, multifactor));
    }


    @Override
    public StringTokenSupplier getRiotClientSupplier(Gateway gateway, String username, String password, MultiFactorSupplier multifactor) throws IOException {
        String riotClientCookie = cookieSupplier.getClientCookie(localRiotFileVersion, CookieType.RIOT_CLIENT, null, gateway);
        return QueryTokenParser.getTokens("riot-client", get(username, password, multifactor, riotClientCookie, gateway));
    }

    @Override
    public LocalRiotFileVersion getLocalRiotFileVersion() {
        return localRiotFileVersion;
    }

    @Override
    public ICookieSupplier getCookieSupplier() {
        return cookieSupplier;
    }

    @Override
    public Gateway getGateway() {
        return gateway;
    }
}

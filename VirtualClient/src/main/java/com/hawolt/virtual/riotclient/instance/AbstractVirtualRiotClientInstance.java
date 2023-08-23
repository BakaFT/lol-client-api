package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.CookieType;
import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.authentication.LocalCookieSupplier;
import com.hawolt.exception.CaptchaException;
import com.hawolt.generic.Constant;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.data.QueryTokenParser;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.version.local.LocalLeagueFileVersion;
import com.hawolt.version.local.LocalRiotFileVersion;
import com.hawolt.virtual.client.CookieManager;
import com.hawolt.virtual.client.ILoginStateConsumer;
import com.hawolt.virtual.client.LoginState;
import com.hawolt.virtual.leagueclient.authentication.OAuthToken;
import com.hawolt.virtual.riotclient.RiotClientException;
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

public class AbstractVirtualRiotClientInstance implements IVirtualRiotClientInstance {
    private final LocalRiotFileVersion localRiotFileVersion;
    private final ILoginStateConsumer stateConsumer;
    private final ICookieSupplier cookieSupplier;
    private final CookieManager manager;
    private final Gateway gateway;


    public AbstractVirtualRiotClientInstance(Gateway gateway, ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer, boolean selfUpdate) {
        this.localRiotFileVersion = new LocalRiotFileVersion(Arrays.asList("RiotClientFoundation.dll", "RiotGamesApi.dll"));
        if (selfUpdate) localRiotFileVersion.schedule(15, 15, TimeUnit.MINUTES);
        this.cookieSupplier = cookieSupplier;
        this.stateConsumer = stateConsumer;
        this.manager = new CookieManager();
        this.gateway = gateway;
    }

    @Override
    public String getRiotClientUserAgent() {
        String minor = localRiotFileVersion.getVersionValue("RiotGamesApi.dll");
        return String.format(
                "RiotClient/%s%s rso-auth (Windows;10;;Professional, x64)",
                localRiotFileVersion.getVersionValue("RiotClientFoundation.dll"),
                minor.substring(minor.lastIndexOf('.'))
        );
    }

    @Override
    public String getRiotClientUserAgentCEF() {
        String major = localRiotFileVersion.getVersionValue("RiotClientFoundation.dll");
        String used = major.substring(0, major.lastIndexOf('.'));
        return String.format(
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) RiotClient/%s (CEF 74) Safari/537.36",
                used
        );
    }

    @Override
    public String submit2FA(String cookie, String code) throws IOException {
        stateConsumer.onStateChange(LoginState.SUBMIT_2FA);
        JSONObject object = new JSONObject();
        object.put("type", "multifactor");
        object.put("code", code);
        object.put("rememberDevice", false);
        RequestBody body = RequestBody.create(object.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/authorization")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", getRiotClientUserAgent())
                .addHeader("Pragma", "no-cache")
                .put(body)
                .build();
        IResponse response = OkHttp3Client.execute(request, gateway);
        manager.addCookiesFromList(response.headers().get("set-cookie"));
        return response.asString();
    }

    @Override
    public VirtualRiotClient login(Platform platform, String token, Gateway gateway) throws IOException {
        LocalLeagueFileVersion localLeagueFileVersion = new LocalLeagueFileVersion(
                Arrays.asList(
                        "League of Legends.exe",
                        "LeagueClientUxRender.exe",
                        "RiotGamesApi.dll"),
                platform
        );
        OAuthToken oauth = new OAuthToken(platform);
        oauth.add("refresh_token", token);
        oauth.refresh(gateway, localLeagueFileVersion, oauth);
        return new VirtualRiotClient(this, oauth);
    }

    @Override
    public VirtualRiotClient login(String username, String password, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier) throws IOException, RiotClientException, CaptchaException, InterruptedException {
        return new VirtualRiotClient(this, username, password, multifactor, captchaSupplier, getRiotClientSupplier(gateway, username, password, multifactor, captchaSupplier));
    }

    public String buildCaptchaPostBody(String clientId) {
        JSONObject object = new JSONObject();
        object.put("apple", JSONObject.NULL);
        object.put("campaign", JSONObject.NULL);
        object.put("clientId", clientId);
        object.put("code", JSONObject.NULL);
        object.put("facebook", JSONObject.NULL);
        object.put("gamecenter", JSONObject.NULL);
        object.put("google", JSONObject.NULL);
        object.put("language", "");
        object.put("multifactor", JSONObject.NULL);
        object.put("nintendo", JSONObject.NULL);
        object.put("platform", "windows");
        object.put("playstation", JSONObject.NULL);
        object.put("remember", JSONObject.NULL);
        JSONObject identity = new JSONObject();
        identity.put("campaign", JSONObject.NULL);
        identity.put("captcha", JSONObject.NULL);
        identity.put("language", "en_GB");
        identity.put("password", JSONObject.NULL);
        identity.put("remember", JSONObject.NULL);
        identity.put("state", "auth");
        identity.put("username", JSONObject.NULL);
        object.put("riot_identity", identity);
        object.put("riot_identity_signup", JSONObject.NULL);
        object.put("rso", JSONObject.NULL);
        object.put("sdkVersion", localRiotFileVersion.getVersionValue("RiotGamesApi.dll"));
        object.put("type", "auth");
        object.put("xbox", JSONObject.NULL);
        return object.toString();
    }

    @Override
    public CaptchaInfo getCaptchaInfo() throws IOException {
        stateConsumer.onStateChange(LoginState.DELETE_LOGIN_CALL);
        IResponse delete = OkHttp3Client.execute(new Request.Builder()
                .url("https://authenticate.riotgames.com/api/v1/login")
                .addHeader("User-Agent", getRiotClientUserAgent())
                .addHeader("Accept", "application/json")
                .delete()
                .build());
        manager.addCookiesFromList(delete.headers().get("set-cookie"));
        stateConsumer.onStateChange(LoginState.FETCH_CAPTCHA_INFO);
        String payload = buildCaptchaPostBody("riot-client");
        RequestBody body = RequestBody.create(payload, Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://authenticate.riotgames.com/api/v1/login")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", manager.cook())
                .addHeader("User-Agent", getRiotClientUserAgent())
                .addHeader("Pragma", "no-cache")
                .post(body)
                .build();
        return new CaptchaInfo(OkHttp3Client.execute(request, gateway));
    }

    @Override
    public StringTokenSupplier getRiotClientSupplier(Gateway gateway, String username, String password, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier) throws IOException, RiotClientException, CaptchaException, InterruptedException {
        stateConsumer.onStateChange(LoginState.FETCH_RIOT_CLIENT_COOKIE);
        manager.addCookiesFromCookieString(cookieSupplier.getClientCookie(localRiotFileVersion, CookieType.RIOT_CLIENT, null, gateway));
        CaptchaInfo captchaInfo = getCaptchaInfo();
        manager.addCookiesFromList(captchaInfo.getResponse().headers().get("set-cookie"));
        if (!captchaInfo.getType().equals("hcaptcha")) {
            throw new RiotClientException("Unknown Captcha Service provider");
        }
        CaptchaInstance instance = captchaInfo.getCurrentCaptchaInstance();
        String rqData = instance.getString("data");
        stateConsumer.onStateChange(LoginState.SOLVE_CAPTCHA);
        String result = captchaSupplier.solve(getRiotClientUserAgentCEF(), rqData);
        stateConsumer.onStateChange(LoginState.EXTRACT_RIOT_CLIENT_TOKEN);
        String captcha = String.format("%s %s", captchaInfo.getType(), result);
        StringTokenSupplier loginSupplier = getLoginTokenSupplier(gateway, username, password, multifactor, manager.cook(), captcha);
        manager.addCookiesFromList(getSSID(loginSupplier).headers().get("set-cookie"));
        return QueryTokenParser.getTokens("riot-client", get(username, password, CookieType.RIOT_CLIENT, multifactor, captchaSupplier, gateway, LoginState.EXTRACT_RIOT_CLIENT_TOKEN));
    }

    @Override
    public String get(String username, String password, CookieType type, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier, Gateway gateway, LoginState state) throws IOException {
        stateConsumer.onStateChange(state);
        String payload = LocalCookieSupplier.payload(type);
        RequestBody body = RequestBody.create(payload, Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/authorization")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", manager.cook())
                .addHeader("User-Agent", getRiotClientUserAgent())
                .addHeader("Pragma", "no-cache")
                .post(body)
                .build();
        return OkHttp3Client.execute(request, gateway).asString();
    }


    public String buildCookiePostBody(String token) {
        JSONObject object = new JSONObject();
        object.put("authentication_type", "RiotAuth");
        object.put("code_verifier", "");
        object.put("login_token", token);
        object.put("persist_login", false);
        return object.toString();
    }

    public IResponse getSSID(StringTokenSupplier loginTokenSupplier) throws IOException {
        String post = buildCookiePostBody(loginTokenSupplier.get("login_token"));
        RequestBody body = RequestBody.create(post, Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/login-token")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", manager.cook())
                .addHeader("User-Agent", getRiotClientUserAgent())
                .addHeader("Pragma", "no-cache")
                .post(body)
                .build();
        return OkHttp3Client.execute(request, gateway);
    }

    public String buildLoginPutBody(String username, String password, String result) {
        JSONObject object = new JSONObject();
        object.put("type", "auth");
        JSONObject identity = new JSONObject();
        identity.put("campaign", JSONObject.NULL);
        identity.put("captcha", result);
        identity.put("language", "en_GB");
        identity.put("password", password);
        identity.put("remember", false);
        identity.put("state", JSONObject.NULL);
        identity.put("username", username);
        object.put("riot_identity", identity);
        return object.toString();
    }

    @Override
    public StringTokenSupplier getLoginTokenSupplier(Gateway gateway, String username, String password, MultiFactorSupplier multiFactorSupplier, String cookie, String captchaResult) throws IOException, RiotClientException {
        String put = buildLoginPutBody(username, password, captchaResult);
        RequestBody body = RequestBody.create(put, Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://authenticate.riotgames.com/api/v1/login")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", getRiotClientUserAgent())
                .addHeader("Cookie", cookie)
                .put(body)
                .build();
        IResponse response = OkHttp3Client.execute(request, gateway);
        manager.addCookiesFromList(response.headers().get("set-cookie"));
        JSONObject object = new JSONObject(response.asString());
        if (!object.has("success")) {
            throw new RiotClientException("Captcha submission failed");
        }
        JSONObject success = object.getJSONObject("success");
        StringTokenSupplier supplier = new StringTokenSupplier();
        supplier.add("login_token", success.getString("login_token"));
        return supplier;
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
    public ILoginStateConsumer getLoginStateConsumer() {
        return stateConsumer;
    }


    @Override
    public Gateway getGateway() {
        return gateway;
    }

    @Override
    public CookieManager getCookieManager() {
        return manager;
    }
}

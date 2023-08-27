package com.hawolt.virtual.riotclient.instance;

import com.hawolt.authentication.*;
import com.hawolt.exception.CaptchaException;
import com.hawolt.generic.Constant;
import com.hawolt.generic.data.QueryTokenParser;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.integrity.Diffuser;
import com.hawolt.http.layer.IResponse;
import com.hawolt.version.local.LocalRiotFileVersion;
import com.hawolt.virtual.client.ILoginStateConsumer;
import com.hawolt.virtual.client.RiotClientException;
import com.hawolt.virtual.client.RiotClientExceptionType;
import com.hawolt.virtual.misc.Authorizable;
import com.hawolt.virtual.riotclient.client.VirtualRiotClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Created: 07/08/2023 16:44
 * Author: Twitter @hawolt
 **/

public class AbstractVirtualRiotClientInstance implements IVirtualRiotClientInstance, Authorizable {
    private final LocalRiotFileVersion localRiotFileVersion;
    private final ILoginStateConsumer stateConsumer;
    private final ICookieSupplier cookieSupplier;
    private StringTokenSupplier tokenSupplier;
    private final Gateway gateway;


    public AbstractVirtualRiotClientInstance(Gateway gateway, ICookieSupplier cookieSupplier, ILoginStateConsumer stateConsumer) {
        this.localRiotFileVersion = new LocalRiotFileVersion(Arrays.asList("RiotClientFoundation.dll", "RiotGamesApi.dll"));
        this.cookieSupplier = cookieSupplier;
        this.stateConsumer = stateConsumer;
        this.gateway = gateway;
    }

    public VirtualRiotClient login(String username, String password, MultiFactorSupplier multifactor, CaptchaSupplier captchaSupplier) throws IOException, RiotClientException, CaptchaException, InterruptedException {
        if (!cookieSupplier.isInCompletedState()) {
            if (!cookieSupplier.has("__cf_bm")) cookieSupplier.configure(getRiotClientUserAgent("rso-auth"));
            if (cookieSupplier.has("__cf_bm")) cookieSupplier.configure(getRiotClientUserAgent("rso-auth"));
            this.cookieSupplier.handle(submitDeleteRequest());
            CaptchaInfo info = getCaptchaInfo();
            CaptchaInstance instance = info.getCurrentCaptchaInstance();
            String captcha = captchaSupplier.solve(getRiotClientUserAgentCEF(), instance.getString("data"));
            IResponse login = login(username, password, String.format("%s %s", info.getType(), captcha), multifactor);
            JSONObject object = new JSONObject(login.asString()).getJSONObject("success");
            String token = object.getString("login_token");
            this.cookieSupplier.handle(getSSID(token));
        }
        this.tokenSupplier = getTokenSupplier(getAuthorizationSupplier().get());
        return new VirtualRiotClient(this, username, password, multifactor, captchaSupplier);
    }

    private String submit(String token) {
        JSONObject object = new JSONObject();
        object.put("authentication_type", "RiotAuth");
        object.put("code_verifier", "");
        object.put("login_token", token);
        object.put("persist_login", false);
        return object.toString();
    }

    private IResponse getSSID(String token) throws IOException {
        String post = submit(token);
        RequestBody body = RequestBody.create(post, Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/login-token")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookieSupplier.getCookie("riotgames.com"))
                .addHeader("User-Agent", getRiotClientUserAgent("rso-auth"))
                .addHeader("Pragma", "no-cache")
                .post(body)
                .build();
        return OkHttp3Client.execute(request, gateway);
    }

    private IResponse login(String username, String password, String result, MultiFactorSupplier multifactor) throws IOException, RiotClientException {
        Diffuser.add(password);
        JSONObject payload = login(username, password, result);
        RequestBody body = RequestBody.create(payload.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://authenticate.riotgames.com/api/v1/login")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", getRiotClientUserAgent("rso-auth"))
                .addHeader("Cookie", cookieSupplier.getCookie("riotgames.com"))
                .put(body)
                .build();
        IResponse response = cookieSupplier.handle(OkHttp3Client.execute(request, gateway));
        JSONObject object = new JSONObject(response.asString());
        if (!object.has("type") || object.isNull("type")) {
            throw new RiotClientException(RiotClientExceptionType.UNKNOWN_RESPONSE);
        } else if (object.getString("type").equals("multifactor")) {
            response = cookieSupplier.handle(submit2FA(multifactor.get(username, password)));
        }
        JSONObject status = new JSONObject(response.asString());
        if (!status.has("success") || status.isNull("success")) {
            throw new RiotClientException(RiotClientExceptionType.UNKNOWN_RESPONSE);
        } else {
            return response;
        }
    }

    private IResponse submitDeleteRequest() throws IOException {
        return OkHttp3Client.execute(new Request.Builder()
                .url("https://authenticate.riotgames.com/api/v1/login")
                .addHeader("User-Agent", getRiotClientUserAgent("rso-auth"))
                .addHeader("Accept", "application/json")
                .delete()
                .build());
    }

    private JSONObject base(ClientID clientID) {
        JSONObject object = new JSONObject();
        object.put("apple", JSONObject.NULL);
        object.put("campaign", JSONObject.NULL);
        object.put("clientId", clientID.toString());
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
        object.put("riot_identity_signup", JSONObject.NULL);
        object.put("rso", JSONObject.NULL);
        object.put("sdkVersion", localRiotFileVersion.getVersionValue("RiotGamesApi.dll"));
        object.put("xbox", JSONObject.NULL);
        return object;
    }

    private JSONObject getMultifactor(String code) {
        JSONObject object = new JSONObject();
        object.put("action", JSONObject.NULL);
        object.put("code", code);
        object.put("rememberDevice", false);
        return object;
    }

    private JSONObject getRiotIdentity() {
        JSONObject object = new JSONObject();
        object.put("campaign", JSONObject.NULL);
        object.put("captcha", JSONObject.NULL);
        object.put("language", "en_GB");
        object.put("password", JSONObject.NULL);
        object.put("remember", JSONObject.NULL);
        object.put("state", "auth");
        object.put("username", JSONObject.NULL);
        return object;
    }

    private CaptchaInfo getCaptchaInfo() throws IOException {
        JSONObject base = base(ClientID.RIOT_CLIENT);
        base.put("riot_identity", getRiotIdentity());
        base.put("type", "auth");
        RequestBody body = RequestBody.create(base.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://authenticate.riotgames.com/api/v1/login")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookieSupplier.getCookie("riotgames.com"))
                .addHeader("User-Agent", getRiotClientUserAgent("rso-auth"))
                .addHeader("Pragma", "no-cache")
                .post(body)
                .build();
        return new CaptchaInfo(cookieSupplier.handle(OkHttp3Client.execute(request, gateway)));
    }

    private IResponse submit2FA(String code) throws IOException {
        JSONObject base = base(ClientID.RIOT_CLIENT);
        base.put("riot_identity", JSONObject.NULL);
        base.put("multifactor", getMultifactor(code));
        base.put("type", "multifactor");
        RequestBody body = RequestBody.create(base.toString(), Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://authenticate.riotgames.com/api/v1/login")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", cookieSupplier.getCookie("riotgames.com"))
                .addHeader("User-Agent", getRiotClientUserAgent("rso-auth"))
                .addHeader("Pragma", "no-cache")
                .put(body)
                .build();
        return cookieSupplier.handle(OkHttp3Client.execute(request, gateway));
    }

    private JSONObject login(String username, String password, String result) {
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
        return object;
    }

    @Override
    public StringTokenSupplier getRiotClientTokenSupplier() {
        return tokenSupplier;
    }

    @Override
    public StringTokenSupplier getTokenSupplier(Authorization authorization) throws IOException {
        IResponse response = cookieSupplier.post(getRiotClientUserAgent("rso-auth"), authorization);
        return QueryTokenParser.getTokens("riot-client", response.asString());
    }

    @Override
    public String getRiotClientUserAgent(String rcp) {
        String minor = localRiotFileVersion.getVersionValue("RiotGamesApi.dll");
        return String.format(
                "RiotClient/%s%s %s (Windows;10;;Professional, x64)",
                localRiotFileVersion.getVersionValue("RiotClientFoundation.dll"),
                minor.substring(minor.lastIndexOf('.')),
                rcp
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
    public Authorizable getAuthorization() {
        return this;
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
    public Supplier<Authorization> getAuthorizationSupplier() {
        return () -> new Authorization.Builder()
                .setClientID(ClientID.RIOT_CLIENT)
                .setRedirectURI("http://localhost/redirect")
                .setResponseTypes(ResponseType.TOKEN, ResponseType.ID_TOKEN)
                .setScopes(ClientScope.OPENID, ClientScope.LINK, ClientScope.BAN, ClientScope.LOL_REGION, ClientScope.ACCOUNT)
                .build();
    }
}

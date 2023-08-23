package com.hawolt.virtual.leagueclient.instance;

import com.hawolt.authentication.LocalCookieSupplier;
import com.hawolt.authentication.WebOrigin;
import com.hawolt.generic.Constant;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.data.QueryTokenParser;
import com.hawolt.generic.stage.IStageCallback;
import com.hawolt.generic.stage.StageAwareObject;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.OkHttp3Client;
import com.hawolt.http.auth.Gateway;
import com.hawolt.logger.Logger;
import com.hawolt.version.local.LocalGameFileVersion;
import com.hawolt.version.local.LocalLeagueFileVersion;
import com.hawolt.version.local.LocalRiotFileVersion;
import com.hawolt.virtual.client.OAuthCode;
import com.hawolt.virtual.clientconfig.impl.PlayerClientConfig;
import com.hawolt.virtual.clientconfig.impl.PublicClientConfig;
import com.hawolt.virtual.leagueclient.authentication.*;
import com.hawolt.virtual.leagueclient.client.Authentication;
import com.hawolt.virtual.leagueclient.client.VirtualLeagueClient;
import com.hawolt.virtual.leagueclient.exception.LeagueException;
import com.hawolt.virtual.leagueclient.refresh.RefreshGroup;
import com.hawolt.virtual.leagueclient.refresh.Refreshable;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.yaml.ConfigValue;
import com.hawolt.yaml.IYamlSupplier;
import com.hawolt.yaml.YamlWrapper;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public abstract class AbstractVirtualLeagueClientInstance implements IVirtualLeagueClientInstance {
    private final StringTokenSupplier leagueClientSupplier;
    private final IVirtualRiotClient virtualRiotClient;
    private final UserInformation userInformation;
    protected final IYamlSupplier yamlSupplier;
    protected final boolean selfUpdate;

    private LocalLeagueFileVersion localLeagueFileVersion;
    private LocalGameFileVersion localGameFileVersion;
    private PlayerClientConfig playerClientConfig;
    private PublicClientConfig publicClientConfig;
    private String platformId;
    private Platform platform;
    private OAuthToken token;

    public AbstractVirtualLeagueClientInstance(IVirtualRiotClient virtualRiotClient, UserInformation userInformation, IYamlSupplier yamlSupplier, StringTokenSupplier leagueClientSupplier, boolean selfUpdate) {
        this(virtualRiotClient, userInformation, yamlSupplier, leagueClientSupplier, null, selfUpdate);
    }

    public AbstractVirtualLeagueClientInstance(IVirtualRiotClient virtualRiotClient, UserInformation userInformation, IYamlSupplier yamlSupplier, StringTokenSupplier leagueClientSupplier, OAuthToken token, boolean selfUpdate) {
        this.leagueClientSupplier = leagueClientSupplier;
        this.virtualRiotClient = virtualRiotClient;
        this.userInformation = userInformation;
        this.yamlSupplier = yamlSupplier;
        this.selfUpdate = selfUpdate;
        this.token = token;
    }

    @Override
    public CompletableFuture<VirtualLeagueClient> chat() throws LeagueException {
        return login(true, true, false, false);
    }

    @Override
    public CompletableFuture<VirtualLeagueClient> login() throws LeagueException {
        return login(false, true, true, false);
    }

    @Override
    public CompletableFuture<VirtualLeagueClient> login(boolean ignoreSummoner, boolean selfRefresh) throws LeagueException {
        return login(ignoreSummoner, selfRefresh, true, false);
    }

    @Override
    public CompletableFuture<VirtualLeagueClient> login(boolean ignoreSummoner, boolean selfRefresh, boolean complete, boolean minimal) throws LeagueException {
        if (!ignoreSummoner && !userInformation.isLeagueAccountAssociated()) {
            throw new LeagueException(LeagueException.ErrorType.NO_SUMMONER_NAME);
        }
        this.platformId = userInformation.getUserInformationLeague().getCPID();
        this.platform = Platform.valueOf(platformId);
        this.localGameFileVersion = new LocalGameFileVersion(platform, Collections.singletonList("League of Legends.exe"));
        this.localLeagueFileVersion = new LocalLeagueFileVersion(
                Arrays.asList(
                        "League of Legends.exe",
                        "LeagueClientUxRender.exe",
                        "RiotGamesApi.dll"),
                platform
        );
        if (selfUpdate) localLeagueFileVersion.schedule(15, 15, TimeUnit.MINUTES);
        CompletableFuture<VirtualLeagueClient> future = new CompletableFuture<>();
        VirtualLeagueClient virtualLeagueClient = new VirtualLeagueClient(this);
        IStageCallback<VirtualLeagueClient> callback = new IStageCallback<VirtualLeagueClient>() {
            @Override
            public void onStageReached(VirtualLeagueClient client) {
                future.complete(client);
            }

            @Override
            public void onStageError(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        };
        StageAwareObject<VirtualLeagueClient> awareness = new StageAwareObject<>(callback, () -> {
            if (selfRefresh) {
                IAuthentication entitlement = virtualLeagueClient.get(Authentication.ENTITLEMENT);
                LocalRiotFileVersion localRiotFileVersion = virtualRiotClient.getInstance().getLocalRiotFileVersion();
                StringTokenSupplier supplier = virtualLeagueClient.getWebOriginOAuthTokenMap().get(WebOrigin.ENTITLEMENTS);
                RefreshGroup group = new RefreshGroup();
                for (WebOrigin webOrigin : WebOrigin.values()) {
                    if (webOrigin == WebOrigin.UNKNOWN) continue;
                    OAuthToken token = virtualLeagueClient.getWebOriginOAuthTokenMap().get(webOrigin);
                    StringTokenSupplier tokenSupplier = virtualLeagueClient.getWebOriginStringTokenSupplierMap().get(webOrigin);
                    Refreshable refreshable = new Refreshable(virtualLeagueClient, token, localLeagueFileVersion, tokenSupplier);
                    group.add(refreshable);
                }
                Refreshable refreshable = new Refreshable(virtualLeagueClient, entitlement, localRiotFileVersion, supplier);
                try {
                    refreshable.refresh(virtualRiotClient.getInstance());
                } catch (IOException e) {
                    Logger.error(e);
                }
                group.add(refreshable);
                virtualLeagueClient.refresh(group, 55, 55);
            }
            virtualRiotClient.getMultifactorSupplier().clear(virtualRiotClient.getUsername(), virtualRiotClient.getPassword());
            return virtualLeagueClient;
        }, 2);
        LocalRiotFileVersion localRiotFileVersion = virtualRiotClient.getInstance().getLocalRiotFileVersion();
        Gateway gateway = virtualRiotClient.getInstance().getGateway();
        try {
            Userinfo userinfo = new Userinfo();
            userinfo.authenticate(gateway, localRiotFileVersion, leagueClientSupplier);
            virtualLeagueClient.setAuthentication(Authentication.USERINFO, userinfo);

            YamlWrapper wrapper = yamlSupplier.getYamlResources(platform);
            Entitlement entitlement = new Entitlement();
            if (!minimal) {
                entitlement.authenticate(gateway, localRiotFileVersion, leagueClientSupplier);
                entitlement.authenticate(gateway, localRiotFileVersion, virtualRiotClient.getRiotClientSupplier());

                StringTokenSupplier config = StringTokenSupplier.merge(
                        "clientconfig",
                        virtualRiotClient.getRiotClientSupplier(),
                        entitlement
                );
                playerClientConfig = new PlayerClientConfig(gateway, platform, config);
                publicClientConfig = new PublicClientConfig(gateway, platform);

                virtualLeagueClient.setAuthentication(Authentication.ENTITLEMENT, entitlement);
                GeoPas geoPas = new GeoPas();
                geoPas.authenticate(gateway, localLeagueFileVersion, leagueClientSupplier);
                virtualLeagueClient.setAuthentication(Authentication.GEOPAS, geoPas);

                RMS rms = new RMS();
                rms.authenticate(gateway, localLeagueFileVersion, leagueClientSupplier);
                virtualLeagueClient.setAuthentication(Authentication.RMS, rms);
            }
            virtualLeagueClient.setYamlWrapper(wrapper);

            if (complete) {
                LoginQueue loginQueue = new LoginQueue(wrapper.get(ConfigValue.PLATFORM), platform);
                loginQueue.authenticate(gateway, localLeagueFileVersion, StringTokenSupplier.merge("queue", leagueClientSupplier, userinfo, entitlement));
                virtualLeagueClient.setAuthentication(Authentication.LOGIN_QUEUE, loginQueue);
                Session session = new Session(userInformation, platform, wrapper.get(ConfigValue.PLATFORM));
                session.authenticate(gateway, localLeagueFileVersion, loginQueue);
                virtualLeagueClient.setAuthentication(Authentication.SESSION, session);
                virtualLeagueClient.refresh(virtualLeagueClient, session, localLeagueFileVersion, null, 5, 5);
            }

            if (selfRefresh) {
                OAuthToken oauth;
                if (token == null) {
                    OAuthCode code = OAuthCode.generate();
                    oauth = new OAuthToken(platform);
                    StringTokenSupplier supplier = oauth(gateway, code);
                    supplier.add("verifier", code.getVerifier());
                    oauth.authenticate(gateway, localLeagueFileVersion, supplier);
                } else {
                    oauth = token;
                }
                Map<WebOrigin, StringTokenSupplier> webOriginStringTokenSupplierMap = new HashMap<>();
                Map<WebOrigin, OAuthToken> webOriginOAuthTokenMap = new HashMap<>();
                webOriginOAuthTokenMap.put(WebOrigin.LOL_LOGIN, oauth);
                webOriginStringTokenSupplierMap.put(WebOrigin.LOL_LOGIN, oauth);
                virtualLeagueClient.setWebOriginStringTokenSupplierMap(webOriginStringTokenSupplierMap);
                virtualLeagueClient.setWebOriginOAuthTokenMap(webOriginOAuthTokenMap);
                awareness.next();
            } else {
                awareness.complete();
            }
        } catch (Exception e) {
            callback.onStageError(e);
        }
        return future;
    }


    public String payload(String challenge) {
        JSONObject object = new JSONObject();
        object.put("acr_values", "");
        object.put("claims", "");
        object.put("client_id", "lol");
        object.put("code_challenge", challenge);
        object.put("code_challenge_method", "S256");
        object.put("nonce", LocalCookieSupplier.generateNonce());
        object.put("redirect_uri", "http://localhost/redirect");
        object.put("response_type", "code");
        object.put("scope", "openid link ban lol_region account");
        return object.toString();
    }

    public StringTokenSupplier oauth(Gateway gateway, OAuthCode code) throws IOException {
        String payload = payload(code.getChallenge());
        RequestBody body = RequestBody.create(payload, Constant.APPLICATION_JSON);
        Request request = new Request.Builder()
                .url("https://auth.riotgames.com/api/v1/authorization")
                .addHeader("User-Agent", virtualRiotClient.getInstance().getRiotClientUserAgent())
                .addHeader("Cookie", virtualRiotClient.getInstance().getCookieManager().cook())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(body)
                .build();
        return QueryTokenParser.getTokens("lol-login", OkHttp3Client.execute(request, gateway).asString(), "\\?");
    }

    @Override
    public IVirtualRiotClient getVirtualRiotClient() {
        return virtualRiotClient;
    }

    @Override
    public LocalLeagueFileVersion getLocalLeagueFileVersion() {
        return localLeagueFileVersion;
    }

    @Override
    public LocalGameFileVersion getLocalGameFileVersion() {
        return localGameFileVersion;
    }

    @Override
    public StringTokenSupplier getLeagueClientSupplier() {
        return leagueClientSupplier;
    }

    @Override
    public PlayerClientConfig getPlayerClientConfig() {
        return playerClientConfig;
    }

    @Override
    public PublicClientConfig getPublicClientConfig() {
        return publicClientConfig;
    }

    @Override
    public UserInformation getUserInformation() {
        return userInformation;
    }

    @Override
    public String getPlatformId() {
        return platformId;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    abstract IYamlSupplier getYamlSupplier();

    abstract boolean isSelfUpdate();
}

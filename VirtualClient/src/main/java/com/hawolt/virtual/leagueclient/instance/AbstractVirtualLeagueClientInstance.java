package com.hawolt.virtual.leagueclient.instance;

import com.hawolt.authentication.ICookieSupplier;
import com.hawolt.authentication.WebOrigin;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.data.QueryTokenParser;
import com.hawolt.generic.stage.IStageCallback;
import com.hawolt.generic.stage.StageAwareObject;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.logger.Logger;
import com.hawolt.version.local.LocalGameFileVersion;
import com.hawolt.version.local.LocalLeagueFileVersion;
import com.hawolt.version.local.LocalRiotFileVersion;
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

    public AbstractVirtualLeagueClientInstance(IVirtualRiotClient virtualRiotClient, UserInformation userInformation, IYamlSupplier yamlSupplier, StringTokenSupplier leagueClientSupplier, boolean selfUpdate) {
        this.leagueClientSupplier = leagueClientSupplier;
        this.virtualRiotClient = virtualRiotClient;
        this.userInformation = userInformation;
        this.yamlSupplier = yamlSupplier;
        this.selfUpdate = selfUpdate;
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
                playerClientConfig = new PlayerClientConfig(platform, config);
                publicClientConfig = new PublicClientConfig(platform);

                virtualLeagueClient.setAuthentication(Authentication.ENTITLEMENT, entitlement);
                GeoPas geoPas = new GeoPas();
                geoPas.authenticate(gateway, localLeagueFileVersion, leagueClientSupplier);
                virtualLeagueClient.setAuthentication(Authentication.GEOPAS, geoPas);
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
                ICookieSupplier supplier = virtualRiotClient.getInstance().getCookieSupplier();
                Map<WebOrigin, StringTokenSupplier> webOriginStringTokenSupplierMap = new HashMap<>();
                Map<WebOrigin, OAuthToken> webOriginOAuthTokenMap = new HashMap<>();
                for (WebOrigin webOrigin : WebOrigin.values()) {
                    if (webOrigin == WebOrigin.UNKNOWN) continue;
                    prepare(gateway, supplier, webOrigin, platform).whenComplete((tokenSupplier, throwable) -> {
                        if (throwable != null) callback.onStageError(throwable);
                        else {
                            try {
                                OAuthToken token = new OAuthToken(platform);
                                token.authenticate(gateway, localLeagueFileVersion, tokenSupplier);
                                webOriginOAuthTokenMap.put(webOrigin, token);
                                webOriginStringTokenSupplierMap.put(webOrigin, tokenSupplier);
                                virtualLeagueClient.setWebOriginStringTokenSupplierMap(webOriginStringTokenSupplierMap);
                                virtualLeagueClient.setWebOriginOAuthTokenMap(webOriginOAuthTokenMap);
                                awareness.next();
                            } catch (IOException e) {
                                callback.onStageError(e);
                            }
                        }
                    });
                }
            } else {
                awareness.complete();
            }
        } catch (IOException e) {
            callback.onStageError(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<StringTokenSupplier> prepare(Gateway gateway, ICookieSupplier cookieSupplier, WebOrigin webOrigin, Platform platform) {
        CompletableFuture<StringTokenSupplier> future = new CompletableFuture<>();
        cookieSupplier.getWebCookie(localLeagueFileVersion, webOrigin, platform).whenComplete((cookie, throwable) -> {
            if (throwable != null) future.completeExceptionally(throwable);
            else {
                try {
                    String raw = virtualRiotClient.getInstance().get(virtualRiotClient.getUsername(), virtualRiotClient.getPassword(), getVirtualRiotClient().getMultifactorSupplier(), cookie, gateway);
                    StringTokenSupplier oauth = QueryTokenParser.getOAuthValues("lol-login", raw);
                    future.complete(oauth);
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
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

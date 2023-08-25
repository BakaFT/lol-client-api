package com.hawolt.virtual.leagueclient.instance;

import com.hawolt.authentication.*;
import com.hawolt.generic.data.Platform;
import com.hawolt.generic.data.QueryTokenParser;
import com.hawolt.generic.stage.IStageCallback;
import com.hawolt.generic.stage.StageAwareObject;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.http.auth.Gateway;
import com.hawolt.http.layer.IResponse;
import com.hawolt.logger.Logger;
import com.hawolt.version.local.LocalGameFileVersion;
import com.hawolt.version.local.LocalLeagueFileVersion;
import com.hawolt.virtual.client.OAuthCode;
import com.hawolt.virtual.clientconfig.impl.PlayerClientConfig;
import com.hawolt.virtual.clientconfig.impl.PublicClientConfig;
import com.hawolt.virtual.leagueclient.authentication.impl.*;
import com.hawolt.virtual.leagueclient.client.Authentication;
import com.hawolt.virtual.leagueclient.client.VirtualLeagueClient;
import com.hawolt.virtual.leagueclient.exception.LeagueException;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import com.hawolt.virtual.refresh.*;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;
import com.hawolt.yaml.IYamlSupplier;
import com.hawolt.yaml.impl.YamlSupplier;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public class AbstractVirtualLeagueClientInstance implements IVirtualLeagueClientInstance, IRefreshable {

    private final IVirtualRiotClientInstance virtualLeagueClientInstance;
    private LocalLeagueFileVersion localLeagueFileVersion;
    private final IVirtualRiotClient virtualRiotClient;
    private LocalGameFileVersion localGameFileVersion;
    private StringTokenSupplier leagueTokenSupplier;
    private PlayerClientConfig playerClientConfig;
    private PublicClientConfig publicClientConfig;
    private ScheduledRefresh<?> scheduledRefresh;
    private ClientTokenStorage tokenStorage;
    private IYamlSupplier yamlSupplier;
    private Platform platform;
    private String platformId;

    public AbstractVirtualLeagueClientInstance(IVirtualRiotClient virtualRiotClient) {
        this.virtualLeagueClientInstance = virtualRiotClient.getInstance();
        this.virtualRiotClient = virtualRiotClient;
    }

    private void checkSummonerState(UserInformation userInformation, boolean ignoreSummoner) throws LeagueException {
        if (!ignoreSummoner && !userInformation.isLeagueAccountAssociated()) {
            throw new LeagueException(LeagueException.ErrorType.NO_SUMMONER_NAME);
        }
    }

    private void configure(UserInformation userInformation) {
        this.platformId = userInformation.getUserInformationLeague().getCPID();
        this.platform = Platform.valueOf(platformId);
        this.localGameFileVersion = new LocalGameFileVersion(platform, Collections.singletonList("League of Legends.exe"));
        this.yamlSupplier = new YamlSupplier(platform);
        this.localLeagueFileVersion = new LocalLeagueFileVersion(
                Arrays.asList(
                        "League of Legends.exe",
                        "LeagueClientUxRender.exe",
                        "RiotGamesApi.dll"
                ),
                platform
        );
    }

    private IStageCallback<VirtualLeagueClient> getClientCompletionCallback(CompletableFuture<VirtualLeagueClient> future) {
        return new IStageCallback<>() {
            @Override
            public void onStageReached(VirtualLeagueClient client) {
                if (virtualRiotClient.getMultifactorSupplier() != null) {
                    virtualRiotClient.getMultifactorSupplier().clear(virtualRiotClient.getUsername(), virtualRiotClient.getPassword());
                }
                future.complete(client);
            }

            @Override
            public void onStageError(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        };
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
    public ClientTokenStorage getClientTokenStorage() {
        return tokenStorage;
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
        return leagueTokenSupplier;
    }

    @Override
    public String getRiotClientLeagueUserAgent(String rcp) {
        return String.format(
                "RiotClient/%s %s (Windows;10;;Professional, x64)",
                localLeagueFileVersion.getVersionValue(platform, "RiotGamesApi.dll"),
                rcp
        );
    }

    @Override
    public String getLeagueClientUserAgent(String rcp) {
        return String.format(
                "LeagueOfLegendsClient/%s (%s)",
                localLeagueFileVersion.getVersionValue(platform, "LeagueClientUxRender.exe"),
                rcp
        );
    }

    private StringTokenSupplier getOAuthTokenSupplier(Authorization.Builder builder) throws IOException, NoSuchAlgorithmException {
        OAuthCode challenge = OAuthCode.generate();
        Authorization authorization = builder.setChallenge(challenge.getChallenge()).build();
        ICookieSupplier cookieSupplier = virtualLeagueClientInstance.getCookieSupplier();
        IResponse response = cookieSupplier.post(
                getRiotClientLeagueUserAgent("rso-auth"),
                authorization
        );
        StringTokenSupplier codeSupplier = QueryTokenParser.getTokens("oauth", response.asString(), "\\?");
        String code = codeSupplier.getSimple("code");
        OAuth auth = new OAuth(cookieSupplier, challenge, code);
        auth.authenticate(virtualLeagueClientInstance.getGateway(), getRiotClientLeagueUserAgent("rso-auth"), null);
        return auth;
    }

    private Authorization.Builder get() {
        return new Authorization.Builder()
                .setClientID(ClientID.LOL)
                .setResponseTypes(ResponseType.CODE)
                .setRedirectURI("http://localhost/redirect")
                .setScopes(ClientScope.OPENID, ClientScope.LINK, ClientScope.BAN, ClientScope.LOL_REGION, ClientScope.ACCOUNT);
    }

    public CompletableFuture<VirtualLeagueClient> login(boolean ignoreSummoner, boolean selfRefresh, boolean complete, boolean minimal) throws LeagueException {
        UserInformation userInformation = virtualRiotClient.getClearUserinformation();
        this.checkSummonerState(userInformation, ignoreSummoner);
        this.configure(userInformation);


        CompletableFuture<VirtualLeagueClient> future = new CompletableFuture<>();
        IStageCallback<VirtualLeagueClient> callback = getClientCompletionCallback(future);
        VirtualLeagueClient virtualLeagueClient = new VirtualLeagueClient(this);

        StageAwareObject<VirtualLeagueClient> awareness = new StageAwareObject<>(callback, () -> virtualLeagueClient, 1);

        Gateway gateway = virtualRiotClient.getInstance().getGateway();
        ICookieSupplier cookieSupplier = virtualRiotClient.getInstance().getCookieSupplier();
        try {
            leagueTokenSupplier = getOAuthTokenSupplier(get());
            RefreshManager.submit(this, 60, 60);

            Userinfo userinfo = new Userinfo(cookieSupplier);
            userinfo.authenticate(
                    gateway,
                    virtualLeagueClientInstance.getRiotClientUserAgent("rso-auth"),
                    virtualRiotClient.getRiotClientSupplier()
            );
            userinfo.authenticate(gateway, getRiotClientLeagueUserAgent("rso-auth"), leagueTokenSupplier);
            virtualLeagueClient.setAuthentication(Authentication.USERINFO, userinfo);

            Entitlement entitlement = null;
            if (!minimal) {
                entitlement = new Entitlement(cookieSupplier, 1);
                entitlement.authenticate(
                        gateway,
                        getRiotClientLeagueUserAgent("entitlements"),
                        leagueTokenSupplier
                );
                virtualLeagueClient.setAuthentication(Authentication.ENTITLEMENT, entitlement);

                StringTokenSupplier config = StringTokenSupplier.merge(
                        "clientconfig",
                        leagueTokenSupplier,
                        entitlement
                );
                playerClientConfig = new PlayerClientConfig(gateway, platform, config);
                publicClientConfig = new PublicClientConfig(gateway, platform);

                XMPP xmpp = new XMPP(cookieSupplier);
                xmpp.authenticate(
                        gateway,
                        virtualLeagueClientInstance.getRiotClientUserAgent("player-affinity"),
                        virtualRiotClient.getRiotClientSupplier()
                );
                virtualLeagueClient.setAuthentication(Authentication.XMPP, xmpp);

                RMS rms = new RMS(cookieSupplier);
                rms.authenticate(
                        gateway,
                        virtualLeagueClientInstance.getRiotClientUserAgent("player-affinity"),
                        virtualRiotClient.getRiotClientSupplier()
                );
                virtualLeagueClient.setAuthentication(Authentication.RMS, rms);
            }

            if (complete) {
                StringTokenSupplier queue = StringTokenSupplier.merge("queue", leagueTokenSupplier, userinfo, entitlement);
                LoginQueue loginQueue = new LoginQueue(cookieSupplier, publicClientConfig);
                loginQueue.authenticate(gateway, getLeagueClientUserAgent("rcp-be-lol-login"), queue);
                virtualLeagueClient.setAuthentication(Authentication.LOGIN_QUEUE, loginQueue);

                Session session = new Session(cookieSupplier, publicClientConfig, userInformation);
                session.authenticate(gateway, getLeagueClientUserAgent("rcp-be-lol-league-session"), loginQueue);
                virtualLeagueClient.setAuthentication(Authentication.SESSION, session);
                RefreshTask task = new RefreshTask(
                        session,
                        gateway,
                        getLeagueClientUserAgent("(rcp-be-lol-league-session)"),
                        session
                );
                RefreshManager.submit(task, 5, 5);
            }
            if (selfRefresh) {
                this.tokenStorage = new ClientTokenStorage(this);
                this.scheduledRefresh = RefreshManager.submit(tokenStorage, 0, 60);
            }
            awareness.complete();
        } catch (Exception e) {
            callback.onStageError(e);
        }
        return future;
    }

    @Override
    public IVirtualRiotClient getVirtualRiotClient() {
        return virtualRiotClient;
    }

    @Override
    public UserInformation getUserInformation() {
        return virtualRiotClient.getClearUserinformation();
    }

    @Override
    public IYamlSupplier getYamlSupplier() {
        return yamlSupplier;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public String getPlatformId() {
        return platformId;
    }

    @Override
    public List<ExceptionalRefreshable> getRefreshableList() {
        return Collections.singletonList(
                () -> AbstractVirtualLeagueClientInstance.this.leagueTokenSupplier = getOAuthTokenSupplier(get())
        );
    }

    @Override
    public void onRefreshException(Throwable throwable) {
        Logger.error(throwable);
    }

    @Override
    public void refresh() {
        for (ExceptionalRefreshable refreshable : getRefreshableList()) {
            try {
                refreshable.refresh();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
    }
}

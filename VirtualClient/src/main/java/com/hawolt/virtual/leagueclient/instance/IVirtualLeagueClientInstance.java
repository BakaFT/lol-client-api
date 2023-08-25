package com.hawolt.virtual.leagueclient.instance;

import com.hawolt.generic.data.Platform;
import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.version.local.LocalGameFileVersion;
import com.hawolt.version.local.LocalLeagueFileVersion;
import com.hawolt.virtual.clientconfig.impl.PlayerClientConfig;
import com.hawolt.virtual.clientconfig.impl.PublicClientConfig;
import com.hawolt.virtual.leagueclient.client.VirtualLeagueClient;
import com.hawolt.virtual.leagueclient.exception.LeagueException;
import com.hawolt.virtual.leagueclient.userinfo.UserInformation;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.yaml.IYamlSupplier;

import java.util.concurrent.CompletableFuture;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public interface IVirtualLeagueClientInstance {
    CompletableFuture<VirtualLeagueClient> login(boolean ignoreSummoner, boolean selfRefresh, boolean complete, boolean minimal) throws LeagueException;

    LocalLeagueFileVersion getLocalLeagueFileVersion();

    String getRiotClientLeagueUserAgent(String rcp);

    LocalGameFileVersion getLocalGameFileVersion();

    StringTokenSupplier getLeagueClientSupplier();

    String getLeagueClientUserAgent(String rcp);

    PlayerClientConfig getPlayerClientConfig();

    PublicClientConfig getPublicClientConfig();

    ClientTokenStorage getClientTokenStorage();

    IVirtualRiotClient getVirtualRiotClient();

    UserInformation getUserInformation();

    IYamlSupplier getYamlSupplier();

    Platform getPlatform();

    String getPlatformId();
}

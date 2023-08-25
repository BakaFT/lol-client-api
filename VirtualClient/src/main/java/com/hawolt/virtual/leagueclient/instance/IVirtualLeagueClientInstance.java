package com.hawolt.virtual.leagueclient.instance;

import com.hawolt.generic.data.Platform;
import com.hawolt.version.local.LocalGameFileVersion;
import com.hawolt.version.local.LocalLeagueFileVersion;
import com.hawolt.virtual.clientconfig.impl.PlayerClientConfig;
import com.hawolt.virtual.clientconfig.impl.PublicClientConfig;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public interface IVirtualLeagueClientInstance {
    /*   CompletableFuture<VirtualLeagueClient> login(boolean ignoreSummoner, boolean selfRefresh, boolean complete, boolean minimal) throws LeagueException;

       CompletableFuture<VirtualLeagueClient> login(boolean ignoreSummoner, boolean selfRefresh) throws LeagueException;

       CompletableFuture<VirtualLeagueClient> login() throws LeagueException;

       CompletableFuture<VirtualLeagueClient> chat() throws LeagueException;

    //   StringTokenSupplier oauth(Gateway gateway, Platform platform);



       StringTokenSupplier getLeagueClientSupplier();



       UserInformation getUserInformation();

       String getPlatformId();

    ;*/
    PlayerClientConfig getPlayerClientConfig();

    PublicClientConfig getPublicClientConfig();

    LocalLeagueFileVersion getLocalLeagueFileVersion();

    LocalGameFileVersion getLocalGameFileVersion();

    String getRiotClientLeagueUserAgent(String rcp);

    String getLeagueClientUserAgent(String rcp);

    IVirtualRiotClient getVirtualRiotClient();

    Platform getPlatform();
}

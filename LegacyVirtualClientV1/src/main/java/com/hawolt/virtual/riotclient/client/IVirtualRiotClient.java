package com.hawolt.virtual.riotclient.client;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.virtual.leagueclient.exception.LeagueException;
import com.hawolt.virtual.leagueclient.instance.VirtualLeagueClientInstance;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;
import com.hawolt.virtual.riotclient.instance.MultiFactorSupplier;
import com.hawolt.virtual.riotclient.userinfo.RiotClientUser;

import java.io.IOException;

/**
 * Created: 07/08/2023 16:54
 * Author: Twitter @hawolt
 **/

public interface IVirtualRiotClient {

    VirtualLeagueClientInstance createVirtualLeagueClientInstance(boolean selfUpdate) throws LeagueException, IOException;

    VirtualLeagueClientInstance createVirtualLeagueClientInstance() throws LeagueException, IOException;

    MultiFactorSupplier getMultifactorSupplier();

    StringTokenSupplier getRiotClientSupplier();

    IVirtualRiotClientInstance getInstance();

    RiotClientUser getRiotClientUser();

    String getUsername();

    String getPassword();
}

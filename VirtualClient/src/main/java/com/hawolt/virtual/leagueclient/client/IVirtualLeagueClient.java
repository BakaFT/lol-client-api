package com.hawolt.virtual.leagueclient.client;

import com.hawolt.virtual.leagueclient.authentication.AbstractTokenSetup;
import com.hawolt.virtual.leagueclient.instance.IVirtualLeagueClientInstance;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;

import java.util.Set;

/**
 * Created: 13/01/2023 11:46
 * Author: Twitter @hawolt
 **/

public interface IVirtualLeagueClient {

    void setAuthentication(Authentication type, AbstractTokenSetup authentication);

    IVirtualLeagueClientInstance getVirtualLeagueClientInstance();

    IVirtualRiotClientInstance getVirtualRiotClientInstance();

    Set<Authentication> getAvailableAuthenticators();

    AbstractTokenSetup get(Authentication type);

    IVirtualRiotClient getVirtualRiotClient();
}

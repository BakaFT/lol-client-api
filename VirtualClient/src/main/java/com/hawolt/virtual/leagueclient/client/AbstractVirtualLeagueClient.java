package com.hawolt.virtual.leagueclient.client;

import com.hawolt.virtual.leagueclient.authentication.AbstractTokenSetup;
import com.hawolt.virtual.leagueclient.instance.IVirtualLeagueClientInstance;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created: 07/08/2023 17:26
 * Author: Twitter @hawolt
 **/

public class AbstractVirtualLeagueClient implements IVirtualLeagueClient {
    private final Map<Authentication, AbstractTokenSetup> authenticators = new HashMap<>();
    private final IVirtualLeagueClientInstance virtualLeagueClientInstance;
    private final IVirtualRiotClientInstance virtualRiotClientInstance;
    private final IVirtualRiotClient virtualRiotClient;

    public AbstractVirtualLeagueClient(IVirtualLeagueClientInstance virtualLeagueClientInstance) {
        this.virtualRiotClient = virtualLeagueClientInstance.getVirtualRiotClient();
        this.virtualRiotClientInstance = virtualRiotClient.getInstance();
        this.virtualLeagueClientInstance = virtualLeagueClientInstance;
    }

    public void setAuthentication(Authentication type, AbstractTokenSetup setup) {
        this.authenticators.put(type, setup);
    }


    public Set<Authentication> getAvailableAuthenticators() {
        return authenticators.keySet();
    }

    public AbstractTokenSetup get(Authentication type) {
        return authenticators.get(type);
    }


    @Override
    public IVirtualLeagueClientInstance getVirtualLeagueClientInstance() {
        return virtualLeagueClientInstance;
    }

    @Override
    public IVirtualRiotClientInstance getVirtualRiotClientInstance() {
        return virtualRiotClientInstance;
    }

    @Override
    public IVirtualRiotClient getVirtualRiotClient() {
        return virtualRiotClient;
    }
}

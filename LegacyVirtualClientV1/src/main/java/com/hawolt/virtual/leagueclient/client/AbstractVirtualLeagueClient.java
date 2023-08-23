package com.hawolt.virtual.leagueclient.client;

import com.hawolt.generic.token.impl.StringTokenSupplier;
import com.hawolt.logger.Logger;
import com.hawolt.version.IVersionSupplier;
import com.hawolt.virtual.leagueclient.authentication.IAuthentication;
import com.hawolt.virtual.leagueclient.instance.IVirtualLeagueClientInstance;
import com.hawolt.virtual.leagueclient.refresh.IRefreshStatus;
import com.hawolt.virtual.leagueclient.refresh.RefreshGroup;
import com.hawolt.virtual.leagueclient.refresh.Refreshable;
import com.hawolt.virtual.leagueclient.refresh.ScheduledRefresh;
import com.hawolt.virtual.riotclient.client.IVirtualRiotClient;
import com.hawolt.virtual.riotclient.instance.IVirtualRiotClientInstance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 07/08/2023 17:26
 * Author: Twitter @hawolt
 **/

public class AbstractVirtualLeagueClient implements IVirtualLeagueClient, IRefreshStatus {
    private final Map<Authentication, IAuthentication> authenticators = new HashMap<>();
    private final IVirtualLeagueClientInstance virtualLeagueClientInstance;
    private final IVirtualRiotClientInstance virtualRiotClientInstance;
    private final IVirtualRiotClient virtualRiotClient;

    public AbstractVirtualLeagueClient(IVirtualLeagueClientInstance virtualLeagueClientInstance) {
        this.virtualRiotClient = virtualLeagueClientInstance.getVirtualRiotClient();
        this.virtualRiotClientInstance = virtualRiotClient.getInstance();
        this.virtualLeagueClientInstance = virtualLeagueClientInstance;
    }

    @Override
    public void onError(Throwable throwable) {
        Logger.error(throwable);
    }

    @Override
    public ScheduledRefresh<?> refresh(IRefreshStatus status, IAuthentication authentication, IVersionSupplier versionSupplier, StringTokenSupplier tokenSupplier, long initial, long minutes) {
        Refreshable refreshable = new Refreshable(status, authentication, versionSupplier, tokenSupplier);
        return refresh(refreshable, initial, minutes);
    }

    @Override
    public ScheduledRefresh<?> refresh(Refreshable refreshable, long initial, long minutes) {
        return refresh(new RefreshGroup(refreshable), initial, minutes);
    }

    @Override
    public ScheduledRefresh<?> refresh(RefreshGroup group, long initial, long minutes) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = service.scheduleAtFixedRate(() -> {
            for (Refreshable refreshable : group) {
                try {
                    refreshable.refresh(virtualRiotClientInstance);
                } catch (IOException e) {
                    onError(e);
                }
            }
        }, initial, minutes, TimeUnit.MINUTES);
        return new ScheduledRefresh<>(service, future);
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

    @Override
    public IAuthentication get(Authentication type) {
        return authenticators.get(type);
    }

    @Override
    public Set<Authentication> getAvailableAuthenticators() {
        return authenticators.keySet();
    }

    @Override
    public void setAuthentication(Authentication type, IAuthentication authentication) {
        this.authenticators.put(type, authentication);
    }
}
